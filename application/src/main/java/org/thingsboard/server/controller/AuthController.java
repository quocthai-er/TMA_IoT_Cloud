/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.Role;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.EdgeEventActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.security.event.UserAuthDataChangedEvent;
import org.thingsboard.server.common.data.security.model.JwtToken;
import org.thingsboard.server.common.data.security.model.SecuritySettings;
import org.thingsboard.server.common.data.security.model.UserPasswordPolicy;
import org.thingsboard.server.dao.audit.AuditLogService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.auth.jwt.RefreshTokenRepository;
import org.thingsboard.server.service.security.auth.rest.RestAuthenticationDetails;
import org.thingsboard.server.service.security.model.ActivateUserRequest;
import org.thingsboard.server.service.security.model.ChangePasswordRequest;
import org.thingsboard.server.service.security.model.JwtTokenPair;
import org.thingsboard.server.service.security.model.ResetPasswordEmailRequest;
import org.thingsboard.server.service.security.model.ResetPasswordRequest;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.model.UserPrincipal;
import org.thingsboard.server.service.security.model.token.JwtTokenFactory;
import org.thingsboard.server.service.security.system.SystemSecurityService;
import ua_parser.Client;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

import static org.thingsboard.server.dao.service.DataValidator.validateEmail;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenFactory tokenFactory;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MailService mailService;
    private final SystemSecurityService systemSecurityService;
    private final AuditLogService auditLogService;
    private final ApplicationEventPublisher eventPublisher;


    @ApiOperation(value = "Get current User (getUser)",
            notes = "Get the information about the User which credentials are used to perform this REST API call.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/auth/user", method = RequestMethod.GET)
    public @ResponseBody
    User getUser() throws ThingsboardException {
        try {
            SecurityUser securityUser = getCurrentUser();
            User user = userService.findUserById(securityUser.getTenantId(), securityUser.getId());
            if (user.getRoleId() != null) {
                Role role = roleService.findRoleById(user.getRoleId());
                if (role != null) {
                    user.setRoleTitle(role.getTitle());
                }
            }
            return user;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Logout (logout)",
            notes = "Special API call to record the 'logout' of the user to the Audit Logs. Since platform uses [JWT](https://jwt.io/), the actual logout is the procedure of clearing the [JWT](https://jwt.io/) token on the client side. ")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/auth/logout", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void logout(HttpServletRequest request) throws ThingsboardException {
        logLogoutAction(request);
    }

    @ApiOperation(value = "Change password for current User (changePassword)",
            notes = "Change the password for the User which credentials are used to perform this REST API call. Be aware that previously generated [JWT](https://jwt.io/) tokens will be still valid until they expire.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/auth/changePassword", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ObjectNode changePassword(
            @ApiParam(value = "Change Password Request")
            @RequestBody ChangePasswordRequest changePasswordRequest) throws ThingsboardException {
        try {
            String currentPassword = changePasswordRequest.getCurrentPassword();
            String newPassword = changePasswordRequest.getNewPassword();
            SecurityUser securityUser = getCurrentUser();
            UserCredentials userCredentials = userService.findUserCredentialsByUserId(TenantId.SYS_TENANT_ID, securityUser.getId());
            if (!passwordEncoder.matches(currentPassword, userCredentials.getPassword())) {
                throw new ThingsboardException("Current password doesn't match!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            systemSecurityService.validatePassword(securityUser.getTenantId(), newPassword, userCredentials);
            if (passwordEncoder.matches(newPassword, userCredentials.getPassword())) {
                throw new ThingsboardException("New password should be different from existing!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            userCredentials.setPassword(passwordEncoder.encode(newPassword));
            userService.replaceUserCredentials(securityUser.getTenantId(), userCredentials);

            sendEntityNotificationMsg(getTenantId(), userCredentials.getUserId(), EdgeEventActionType.CREDENTIALS_UPDATED);

            eventPublisher.publishEvent(new UserAuthDataChangedEvent(securityUser.getId()));
            ObjectNode response = JacksonUtil.newObjectNode();
            response.put("token", tokenFactory.createAccessJwtToken(securityUser).getToken());
            response.put("refreshToken", tokenFactory.createRefreshToken(securityUser).getToken());
            return response;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Get the current User password policy (getUserPasswordPolicy)",
            notes = "API call to get the password policy for the password validation form(s).")
    @RequestMapping(value = "/noauth/userPasswordPolicy", method = RequestMethod.GET)
    @ResponseBody
    public UserPasswordPolicy getUserPasswordPolicy() throws ThingsboardException {
        try {
            SecuritySettings securitySettings =
                    checkNotNull(systemSecurityService.getSecuritySettings(TenantId.SYS_TENANT_ID));
            return securitySettings.getPasswordPolicy();
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Check Activate User Token (checkActivateToken)",
            notes = "Checks the activation token and forwards user to 'Create Password' page. " +
                    "If token is valid, returns '303 See Other' (redirect) response code with the correct address of 'Create Password' page and same 'activateToken' specified in the URL parameters. " +
                    "If token is not valid, returns '409 Conflict'.")
    @RequestMapping(value = "/noauth/activate", params = {"activateToken"}, method = RequestMethod.GET)
    public ResponseEntity<String> checkActivateToken(
            @ApiParam(value = "The activate token string.")
            @RequestParam(value = "activateToken") String activateToken) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseStatus;
        UserCredentials userCredentials = userService.findUserCredentialsByActivateToken(TenantId.SYS_TENANT_ID, activateToken);
        if (userCredentials != null) {
            String createURI = "/login/createPassword";
            try {
                URI location = new URI(createURI + "?activateToken=" + activateToken);
                headers.setLocation(location);
                responseStatus = HttpStatus.SEE_OTHER;
            } catch (URISyntaxException e) {
                log.error("Unable to create URI with address [{}]", createURI);
                responseStatus = HttpStatus.BAD_REQUEST;
            }
        } else {
            responseStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(headers, responseStatus);
    }

    @ApiOperation(value = "Request reset password by phone number (resetPasswordByPhoneNumber)",
            notes = "Request to get the reset password token if the user with specified phone number is present in the database. " +
                    "Always return '200 OK' status for security purposes.")
    @RequestMapping(value = "/noauth/resetPasswordByPhoneNumber", method = RequestMethod.POST, produces = "text/plain")
    @ResponseStatus(value = HttpStatus.OK)
    public String resetPasswordByPhoneNumber(
            @ApiParam(value = "The JSON object representing the reset password phone number request.")
            @RequestParam (value = "phone") String phone,
            HttpServletRequest request) throws ThingsboardException {
        try {
            UserCredentials userCredentials = userService.requestPasswordResetByPhoneNumber(TenantId.SYS_TENANT_ID, phone);
            return userCredentials.getResetToken();
        } catch (Exception e) {
            //log.warn("Error occurred: {}", e.getMessage());
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Request reset password by email (resetPasswordByEmail)",
            notes = "Request to send the reset password email if the user with specified email address is present in the database. " +
                    "Always return '200 OK' status for security purposes.")
    @RequestMapping(value = "/noauth/resetPasswordByEmail", method = RequestMethod.POST, produces = "text/plain")
    @ResponseStatus(value = HttpStatus.OK)
    public String resetPasswordByEmail(
            @ApiParam(value = "The JSON object representing the reset password email request.")
            @RequestBody ResetPasswordEmailRequest resetPasswordByEmailRequest,
            HttpServletRequest request) throws ThingsboardException {
        try {
            String email = resetPasswordByEmailRequest.getEmail();
            UserCredentials userCredentials = userService.requestPasswordResetByEmail(TenantId.SYS_TENANT_ID, email);
            User user = userService.findUserById(TenantId.SYS_TENANT_ID, userCredentials.getUserId());
            String baseUrl = systemSecurityService.getBaseUrl(user.getTenantId(), user.getCustomerId(), request);
            String resetUrl = String.format("%s/api/noauth/resetPassword?resetToken=%s", baseUrl,
                    userCredentials.getResetToken());
            mailService.sendResetPasswordEmailAsync(resetUrl, email);
            return userCredentials.getResetToken();
        } catch (Exception e) {
            //log.warn("Error occurred: {}", e.getMessage());
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Check password reset token (checkResetToken)",
            notes = "Checks the password reset token and forwards user to 'Reset Password' page. " +
                    "If token is valid, returns '303 See Other' (redirect) response code with the correct address of 'Reset Password' page and same 'resetToken' specified in the URL parameters. " +
                    "If token is not valid, returns '409 Conflict'.")
    @RequestMapping(value = "/noauth/resetPassword", params = {"resetToken"}, method = RequestMethod.GET)
    public ResponseEntity<String> checkResetToken(
            @ApiParam(value = "The reset token string.")
            @RequestParam(value = "resetToken") String resetToken) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseStatus;
        String resetURI = "/login/resetPassword";
        UserCredentials userCredentials = userService.findUserCredentialsByResetToken(TenantId.SYS_TENANT_ID, resetToken);
        if (userCredentials != null) {
            try {
                URI location = new URI(resetURI + "?resetToken=" + resetToken);
                headers.setLocation(location);
                responseStatus = HttpStatus.SEE_OTHER;
            } catch (URISyntaxException e) {
                log.error("Unable to create URI with address [{}]", resetURI);
                responseStatus = HttpStatus.BAD_REQUEST;
            }
        } else {
            responseStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(headers, responseStatus);
    }

    @ApiOperation(value = "Activate User",
            notes = "Checks the activation token and updates corresponding user password in the database. " +
                    "Now the user may start using his password to login. " +
                    "The response already contains the [JWT](https://jwt.io) activation and refresh tokens, " +
                    "to simplify the user activation flow and avoid asking user to input password again after activation. " +
                    "If token is valid, returns the object that contains [JWT](https://jwt.io/) access and refresh tokens. " +
                    "If token is not valid, returns '404 Bad Request'.")
    @RequestMapping(value = "/noauth/activate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JwtTokenPair activateUser(
            @ApiParam(value = "Activate user request.")
            @RequestBody ActivateUserRequest activateRequest,
            @RequestParam(required = false, defaultValue = "false") boolean sendActivationMail,
            HttpServletRequest request) throws ThingsboardException {
        try {
            String activateToken = activateRequest.getActivateToken();
            String password = activateRequest.getPassword();
            systemSecurityService.validatePassword(TenantId.SYS_TENANT_ID, password, null);
            String encodedPassword = passwordEncoder.encode(password);
            UserCredentials credentials = userService.activateUserCredentials(TenantId.SYS_TENANT_ID, activateToken, encodedPassword);
            User user = userService.findUserById(TenantId.SYS_TENANT_ID, credentials.getUserId());
            UserPrincipal principal = new UserPrincipal(UserPrincipal.Type.USER_NAME, user.getPhone());
            SecurityUser securityUser = new SecurityUser(user, credentials.isEnabled(), principal);
            userService.setUserCredentialsEnabled(user.getTenantId(), user.getId(), true);
            String baseUrl = systemSecurityService.getBaseUrl(user.getTenantId(), user.getCustomerId(), request);
            String loginUrl = String.format("%s/login", baseUrl);
            String email = user.getEmail();
            if (!StringUtils.isEmpty(email)) {
                sendActivationMail = true;
            }
            else {
                sendActivationMail = false;
            }
            if (sendActivationMail) {
                try {
                    mailService.sendAccountActivatedEmail(loginUrl, email);
                } catch (Exception e) {
                    log.info("Unable to send account activation email [{}]", e.getMessage());
                }
            }

            sendEntityNotificationMsg(user.getTenantId(), user.getId(), EdgeEventActionType.CREDENTIALS_UPDATED);

            JwtToken accessToken = tokenFactory.createAccessJwtToken(securityUser);
            JwtToken refreshToken = refreshTokenRepository.requestRefreshToken(securityUser);

            return new JwtTokenPair(accessToken.getToken(), refreshToken.getToken());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation(value = "Reset password (resetPassword)",
            notes = "Checks the password reset token and updates the password. " +
                    "If token is valid, returns the object that contains [JWT](https://jwt.io/) access and refresh tokens. " +
                    "If token is not valid, returns '404 Bad Request'.")
    @RequestMapping(value = "/noauth/resetPassword", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JwtTokenPair resetPassword(
            @ApiParam(value = "Reset password request.")
            @RequestBody ResetPasswordRequest resetPasswordRequest,
            HttpServletRequest request) throws ThingsboardException {
        try {
            String resetToken = resetPasswordRequest.getResetToken();
            String password = resetPasswordRequest.getPassword();
            UserCredentials userCredentials = userService.findUserCredentialsByResetToken(TenantId.SYS_TENANT_ID, resetToken);
            if (userCredentials != null) {
                systemSecurityService.validatePassword(TenantId.SYS_TENANT_ID, password, userCredentials);
                if (passwordEncoder.matches(password, userCredentials.getPassword())) {
                    throw new ThingsboardException("New password should be different from existing!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
                }
                String encodedPassword = passwordEncoder.encode(password);
                userCredentials.setPassword(encodedPassword);
                userCredentials.setResetToken(null);
                userCredentials = userService.replaceUserCredentials(TenantId.SYS_TENANT_ID, userCredentials);
                User user = userService.findUserById(TenantId.SYS_TENANT_ID, userCredentials.getUserId());
                UserPrincipal principal = new UserPrincipal(UserPrincipal.Type.USER_NAME, user.getPhone());
                SecurityUser securityUser = new SecurityUser(user, userCredentials.isEnabled(), principal);
                //String baseUrl = systemSecurityService.getBaseUrl(user.getTenantId(), user.getCustomerId(), request);
                //String loginUrl = String.format("%s/login", baseUrl);
                //String email = user.getEmail();
                //mailService.sendPasswordWasResetEmail(loginUrl, email);

                eventPublisher.publishEvent(new UserAuthDataChangedEvent(securityUser.getId()));
                JwtToken accessToken = tokenFactory.createAccessJwtToken(securityUser);
                JwtToken refreshToken = refreshTokenRepository.requestRefreshToken(securityUser);

                return new JwtTokenPair(accessToken.getToken(), refreshToken.getToken());
            } else {
                throw new ThingsboardException("Invalid reset token!", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private void logLogoutAction(HttpServletRequest request) throws ThingsboardException {
        try {
            SecurityUser user = getCurrentUser();
            RestAuthenticationDetails details = new RestAuthenticationDetails(request);
            String clientAddress = details.getClientAddress();
            String browser = "Unknown";
            String os = "Unknown";
            String device = "Unknown";
            if (details.getUserAgent() != null) {
                Client userAgent = details.getUserAgent();
                if (userAgent.userAgent != null) {
                    browser = userAgent.userAgent.family;
                    if (userAgent.userAgent.major != null) {
                        browser += " " + userAgent.userAgent.major;
                        if (userAgent.userAgent.minor != null) {
                            browser += "." + userAgent.userAgent.minor;
                            if (userAgent.userAgent.patch != null) {
                                browser += "." + userAgent.userAgent.patch;
                            }
                        }
                    }
                }
                if (userAgent.os != null) {
                    os = userAgent.os.family;
                    if (userAgent.os.major != null) {
                        os += " " + userAgent.os.major;
                        if (userAgent.os.minor != null) {
                            os += "." + userAgent.os.minor;
                            if (userAgent.os.patch != null) {
                                os += "." + userAgent.os.patch;
                                if (userAgent.os.patchMinor != null) {
                                    os += "." + userAgent.os.patchMinor;
                                }
                            }
                        }
                    }
                }
                if (userAgent.device != null) {
                    device = userAgent.device.family;
                }
            }
            auditLogService.logEntityAction(
                    user.getTenantId(), user.getCustomerId(), user.getId(),
                    user.getName(), user.getId(), null, ActionType.LOGOUT, null, clientAddress, browser, os, device);

        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
