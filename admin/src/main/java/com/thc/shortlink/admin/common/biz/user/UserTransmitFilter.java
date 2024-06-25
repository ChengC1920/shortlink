package com.thc.shortlink.admin.common.biz.user;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.thc.shortlink.admin.dao.entity.UserDO;
import com.thc.shortlink.admin.dto.resp.UserRespDTO;
import com.thc.shortlink.admin.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.calcite.runtime.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用户信息传输过滤器
 */
@Component
public class UserTransmitFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = httpServletRequest.getHeader("token");
        // 检查 token 是否有效
        if (StpUtil.isLogin() && StpUtil.getTokenValue().equals(token)) {
            UserDO user = (UserDO) StpUtil.getSession().get("userInfo");
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setUserId(user.getId().toString());
            userInfoDTO.setUsername(user.getUsername());
            userInfoDTO.setRealName(user.getRealName());
            UserContext.setUser(userInfoDTO);
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
