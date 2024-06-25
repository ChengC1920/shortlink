package com.thc.shortlink.admin.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.shortlink.admin.common.convention.exception.ClientException;
import com.thc.shortlink.admin.common.convention.exception.ServiceException;
import com.thc.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.thc.shortlink.admin.dao.entity.UserDO;
import com.thc.shortlink.admin.dao.mapper.UserMapper;
import com.thc.shortlink.admin.dto.req.UserLoginReqDTO;
import com.thc.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.thc.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.thc.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.thc.shortlink.admin.dto.resp.UserRespDTO;
import com.thc.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.thc.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.thc.shortlink.admin.common.enums.UserErrorCodeEnum.*;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.userPassword;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    @Override
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

    @Override
    public boolean hasUsername(String username) {
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void userRegister(UserRegisterReqDTO userRegisterReqDTO) {
        if (!hasUsername(userRegisterReqDTO.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + userRegisterReqDTO.getUsername());

        try {
            if (lock.tryLock()) {
                int inserted = baseMapper.insert(BeanUtil.toBean(userRegisterReqDTO, UserDO.class));
                if (inserted < 1) {
                    throw new ClientException(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(userRegisterReqDTO.getUsername());
                return;
            }
            throw new ClientException(USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO userUpdateReqDTO) {
        //TODO 判断修改的用户信息是否是当前登录用户
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, userUpdateReqDTO.getUsername());
        baseMapper.update(BeanUtil.toBean(userUpdateReqDTO, UserDO.class), updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userLoginReqDTO.getUsername(), userLoginReqDTO.getPassword())) {
            throw new ClientException(USER_NAME_IS_BLANK);
        }
        // 判断用户是否存在
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userLoginReqDTO.getUsername());
        queryWrapper.eq("password", userLoginReqDTO.getPassword());
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(USER_NULL);
        }
        StpUtil.login(userLoginReqDTO.getUsername());
        UserLoginRespDTO userLoginRespDTO = new UserLoginRespDTO();
        StpUtil.getTokenValue();
        userLoginRespDTO.setToken(StpUtil.getTokenValue());
        return userLoginRespDTO;
    }

    @Override
    public void checkLogin() {
        try {
            StpUtil.checkLogin();
        } catch (NotLoginException e) {
            throw new ClientException(USER_NOT_LOGIN);
        }
    }
}
