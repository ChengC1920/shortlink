package com.thc.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.thc.shortlink.admin.common.convention.result.Result;
import com.thc.shortlink.admin.common.convention.result.Results;
import com.thc.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.thc.shortlink.admin.dto.resp.UserActualRespDTO;
import com.thc.shortlink.admin.dto.resp.UserRespDTO;
import com.thc.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByName(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    @GetMapping("/api/short-link/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByName(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }

    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    @PostMapping("/api/short-link/v1/user")
    public Result<Void> userRegister(@RequestBody UserRegisterReqDTO userRegisterReqDTO) {
        userService.userRegister(userRegisterReqDTO);
        return Results.success();
    }
}
