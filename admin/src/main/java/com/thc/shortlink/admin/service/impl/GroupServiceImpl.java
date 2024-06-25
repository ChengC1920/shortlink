package com.thc.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.shortlink.admin.dao.entity.GroupDO;
import com.thc.shortlink.admin.dao.mapper.GroupMapper;
import com.thc.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.thc.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.thc.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.thc.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.distsql.parser.autogen.KernelDistSQLStatementParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void saveGroup(String groupName) {

    }

    @Override
    public void saveGroup(String username, String groupName) {

    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        return null;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {

    }

    @Override
    public void deleteGroup(String gid) {

    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {

    }
}
