package com.thc.shortlink.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.shortlink.admin.common.biz.user.UserContext;
import com.thc.shortlink.admin.dao.entity.GroupDO;
import com.thc.shortlink.admin.dao.entity.UserDO;
import com.thc.shortlink.admin.dao.mapper.GroupMapper;
import com.thc.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.thc.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.thc.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.thc.shortlink.admin.service.GroupService;
import com.thc.shortlink.admin.toolkit.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.distsql.parser.autogen.KernelDistSQLStatementParser;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void saveGroup(String groupName) {
        UserDO userInfo = (UserDO) StpUtil.getSession().get("userInfo");
        String username = userInfo.getUsername();
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (!hasGid(gid));
        GroupDO groupDD = GroupDO.builder()
                .gid(gid)
                .sortOrder(0)
                .name(groupName)
                .username(username)
                .build();
        baseMapper.insert(groupDD);
    }

    @Override
    public void saveGroup(String username, String groupName) {

    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(lambdaQueryWrapper);
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        return shortLinkGroupRespDTOList;
    }

    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(GroupDO::getGid, gid)
                //TODO 设置用户名
                .eq(GroupDO::getUsername, UserContext.getUsername());
        GroupDO hasGroupFlag = baseMapper.selectOne(lambdaQueryWrapper);
        return hasGroupFlag == null;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        UserDO userInfo = (UserDO) StpUtil.getSession().get("userInfo");
        LambdaQueryWrapper<GroupDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupDO::getUsername, userInfo.getUsername())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, lambdaQueryWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        UserDO userInfo = (UserDO) StpUtil.getSession().get("userInfo");
        LambdaQueryWrapper<GroupDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GroupDO::getUsername, userInfo.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        baseMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        UserDO userInfo = (UserDO) StpUtil.getSession().get("userInfo");
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaQueryWrapper<GroupDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(GroupDO::getUsername, userInfo.getUsername())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, lambdaQueryWrapper);
        });
    }


}
