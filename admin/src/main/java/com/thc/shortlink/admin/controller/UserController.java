package com.thc.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.thc.shortlink.admin.common.convention.result.Result;
import com.thc.shortlink.admin.common.convention.result.Results;
import com.thc.shortlink.admin.dto.resp.UserActualRespDTO;
import com.thc.shortlink.admin.dto.resp.UserRespDTO;
import com.thc.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByName(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByName(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }

}
