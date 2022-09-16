package org.thingsboard.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.queue.util.TbCoreComponent;

@RestController
@TbCoreComponent
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoleController extends BaseController{



}
