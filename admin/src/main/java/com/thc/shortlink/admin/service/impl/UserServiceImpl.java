package com.thc.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.shortlink.admin.common.convention.exception.ClientException;
import com.thc.shortlink.admin.common.convention.exception.ServiceException;
import com.thc.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.thc.shortlink.admin.dao.entity.UserDO;
import com.thc.shortlink.admin.dao.mapper.UserMapper;
import com.thc.shortlink.admin.dto.resp.UserRespDTO;
import com.thc.shortlink.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    public UserRespDTO getUserByUsername(String username) {
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<UserDO>();
        queryWrapper.eq("username", username);
        UserDO userDO = this.baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ServiceException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO, userRespDTO);
        return userRespDTO;
    }
}
