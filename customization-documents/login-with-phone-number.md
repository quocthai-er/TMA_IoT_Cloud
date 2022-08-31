### 1. Thay đổi cấu trúc table trong database (tb_user): Thêm column 'phone'

Để thay đổi cấu trúc database theo từng phiên bản, Thingsboard sử dụng các yếu tố chính:

- Update command: upgrade.bat --fromVersion="Phiên bản cần update"
- application/target/windows/upgrade.bat: Config đường dẫn các thư mục cần sử dụng trong việc update.
- application/src/main/data/upgrade: Danh sách các thư mục nói trên, với mỗi phiên bản là một thư mục riêng.
- Function performInstall() của class application/src/main/java/org/thingsboard/server/ThingsboardInstallApplication.java: Function này sử dụng switch case tham số fromVersion từ update command để thực hiện sửa đổi theo thư mục tương ứng.

Việc thay đổi cấu trúc database cũng được thực hiện tương tự:

- Tạo thư mục và file udpate: application/src/main/data/upgrade/3.3.4.1.test/schema_update.sql.

```sql
+--Update Thingsboard customized database

ALTER TABLE customer ADD COLUMN IF NOT EXISTS avatar varchar(1000000);
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS phone character varying(255) COLLATE pg_catalog."default";

DO
$$
    BEGIN
        IF NOT EXISTS(SELECT 1 FROM pg_constraint WHERE conname = 'tb_user_phone_key') THEN
            ALTER TABLE tb_user ADD CONSTRAINT tb_user_phone_key UNIQUE (phone);
        END IF;
    END;
$$;
```

- Trong function performInstall(), thêm case mới cho thư mục update vừa tạo

```java
    case "3.3.4.1.test":
        log.info("Upgrading ThingsBoard from version 3.3.4 to 3.3.4.1.test ...");
        databaseEntitiesUpgradeService.upgradeDatabase("3.3.4.1.test");
        break;
```

- Thực hiện tương tự với function upgradeDatabase() của class application/src/main/java/org/thingsboard/server/service/install/SqlDatabaseUpgradeService.java

```java
    case "3.3.4.1.test":
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword)) {
            log.info("Updating schema ...");
            schemaUpdateFile = Paths.get(installScripts.getDataDir(), "upgrade", "3.3.4.1.test", SCHEMA_UPDATE_SQL);
            loadSql(schemaUpdateFile, conn);
        } catch (Exception e) {
            log.error("Failed updating schema!!!", e);
        }
        break;
```

**Lưu ý: Các thay đổi nói trên được ta thực hiện trong folder application/src, trong khi đó, Thingsboard sử dụng phiên bản đã được build của project trong folder application/target để tiến hành update. Vậy nên ta cần phải build lại project để folder target có thể cập nhập các thay đổi.**

- Sau khi build hoàn tất, ta thay đổi file application/target/windows/upgrade.bat như sau:

```bash
@ECHO OFF

setlocal ENABLEEXTENSIONS

@ECHO Upgrading thingsboard ...

SET BASE=C:\\Users\\NguyenMinhHanh\\Downloads\\Save\\TMA_IoT_Cloud\\application\target

:loop
IF NOT "%1"=="" (
    IF "%1"=="--fromVersion" (
        SET fromVersion=%2
    )
    SHIFT
    GOTO :loop
)

if not defined fromVersion (
    echo "--fromVersion parameter is invalid or unspecified!"
    echo "Usage: upgrade.bat --fromVersion {VERSION}"
    exit /b 1
)

SET LOADER_PATH=%BASE%\conf,%BASE%\extensions
SET SQL_DATA_FOLDER=%BASE%\data\sql
SET jarfile=%BASE%\thingsboard-3.4.0-boot.jar
SET installDir=%BASE%\data

PUSHD "%BASE%\conf"

java -cp %jarfile% -Dloader.main=org.thingsboard.server.ThingsboardInstallApplication^
                    -Dinstall.data_dir="%installDir%"^
                    -Dspring.jpa.hibernate.ddl-auto=none^
                    -Dinstall.upgrade=true^
                    -Dinstall.upgrade.from_version=%fromVersion%^
                    -Dlogging.config=%BASE%\windows\install\logback.xml^
                    org.springframework.boot.loader.PropertiesLauncher

if errorlevel 1 (
   @echo ThingsBoard upgrade failed!
   POPD
   exit /b %errorlevel%
)
POPD

@ECHO ThingsBoard upgraded successfully!

GOTO END

:END
```

Trong đó, biến BASE là đường dẫn chứa project Thingsboard.

- Truy cập đến thư mục application/target/windows và chạy lệnh sau: upgrade.bat --fromVersion="3.3.4.1.test"

### 2. Thay đổi trong class User

- Tạo field "phone", getter, setter và thay đổi function toString()

```java
    private String phone;
```

```java
    @ApiModelProperty(position = 11, required = true, value = "Phone number", example = "+1(415)777-7777")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
```

```java
    public String toString() {
        ...
        builder.append(", phone=");
        builder.append(phone);
        builder.append("]");
        return builder.toString();
        }
```

- Đổi attribute required = true của User email thành false để ta có thể bỏ qua trường này khi tạo user mới thông qua swagger.

```java
    @ApiModelProperty(position = 5, required = false, value = "Email of the user", example = "user@example.com")
    public String getEmail() {
        return email;
    }
```

- Field textSearch được sử dụng để tìm kiếm user theo phone number

```java
    @Override
    public String getSearchText() {
        return getPhone();
    }
```

### 3. Tạo enum mới cho field "phone" trong class ModelConstant

```java
    public static final String USER_PHONE_PROPERTY = "phone";
```

### 4. Thay đổi trong class UserEntity

- Tạo column tương ứng cho field "phone"

```java
    @Column(name = ModelConstants.USER_PHONE_PROPERTY, unique = true)
    private String phone;
```

- Set giá trị cho field "phone" trong constructor và function toData()

```java
    public UserEntity(User user) {
            ...
            this.phone=user.getPhone();
            ...
    }
```

```java
    @Override
    public User toData() {
        ...
        user.setPhone(phone);
        ...
        return user;
    }
```

- Tương tự với class User, ta cũng trả về textSearch là phone number

```java
    @Override
    public String getSearchTextSource() {
        return phone;
}
```

### 3. Thêm method tìm User theo tenantId và phone number. Method này sẽ được implements ở class \dao\src\main\java\org\thingsboard\server\dao\sql\user\JpaUserDao.java

```java
    User findByPhone(TenantId tenantId, String phone);
```

### 4. Implements method ở findByPhone() ở interface UserDao

```java
    @Override
    public User findByPhone(TenantId tenantId, String phone) {
        return DaoUtil.getData(userRepository.findByPhone(phone));
    }
```

### 5. Thêm method tìm User theo phone number cho class dao\src\main\java\org\thingsboard\server\dao\sql\user\UserRepository.java

```java
    UserEntity findByPhone(String phone);
```

### 6. Thực hiện tương tự với interface UserService và class UserServiceImpl

- UserService

```java
    User findUserByPhone(TenantId tenantId, String phone);
```

- UserServiceImpl

```java
    public User findUserByPhone(TenantId tenantId, String phone) {
        log.trace("Executing findUserByPhone [{}]", phone);
        validateString(phone, "Incorrect phone " + phone);
        return userDao.findByPhone(tenantId, phone);
    }
```

### 7. Thay đổi trong class UserDataValidator. Đây là class chịu trách nhiệm validate các trường thông tin của User trước khi lưu vào database.

```java
    @Override
    protected void validateDataImpl(TenantId requestTenantId, User user) {
        //Kiểm tra độ dài của phone number
        if (StringUtils.isEmpty(user.getPhone())) {
            throw new DataValidationException("Phone number should be specified!");
        }
        ...
        // Kiểm tra email đã tòn tại
        if (!StringUtils.isEmpty(user.getEmail())) {
            validateEmail(user.getEmail());
            //throw new DataValidationException("User email should be specified!");
            User existentUserWithEmail = userService.findUserByEmail(tenantId, user.getEmail());
            if (existentUserWithEmail != null && !isSameData(existentUserWithEmail, user)) {
                throw new DataValidationException("User with email '" + user.getEmail() + "' "
                        + " already present in database!");
            }
        }
        //Kiểm tra phone number đã tồn tại
        User existentUserWithPhone = userService.findUserByPhone(tenantId, user.getPhone());
        if (existentUserWithPhone != null && !isSameData(existentUserWithPhone, user)) {
            throw new DataValidationException("User with phone '" + user.getPhone() + "' "
                    + " already present in database!");
        }
        ...
    }
```

### 8. Tìm User theo phone number khi authenticate trong class RestAuthenticationProvider

```java
    private SecurityUser authenticateByUsernameAndPassword(Authentication authentication, UserPrincipal userPrincipal, String username, String password) {
        User user = userService.findUserByPhone(TenantId.SYS_TENANT_ID, username);
        if (user == null) {
            user = userService.findUserByEmail(TenantId.SYS_TENANT_ID, username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
        }
       ...
    }
```

### 9. Thay đổi trong class AuthController

- Kiểm tra email của user trước khi gửi activation mail

```java
     public JwtTokenPair activateUser(
            @ApiParam(value = "Activate user request.")
            @RequestBody ActivateUserRequest activateRequest,
            @RequestParam(required = false, defaultValue = "true") boolean sendActivationMail,
            @RequestParam(required = false, defaultValue = "false") boolean sendActivationMail,
            HttpServletRequest request) throws ThingsboardException {
            ...
            String email = user.getEmail();
            //Kiểm tra email
            if (!StringUtils.isEmpty(email)) {
                sendActivationMail = true;
            }
            else {
                sendActivationMail = false;
            }
            //Send mail
            if (sendActivationMail) {
                try {
                    mailService.sendAccountActivatedEmail(loginUrl, email);
                } catch (Exception e) {
                    log.info("Unable to send account activation email [{}]", e.getMessage());
                }
            }
            ...
    }
```
