package com.thc.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thc.shortlink.admin.dao.entity.UserDO;
import com.thc.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.thc.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否存在
     * @param username 用户名
     * @return boolean
     */
    boolean hasUsername(String username);

    /**
     * 用户注册
     * @param userRegisterReqDTO
     */
    void userRegister(UserRegisterReqDTO userRegisterReqDTO);

}
