package com.atguigu.security.security;

import com.atguigu.utils.utils.R;
import com.atguigu.utils.utils.ResponseUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 退出处理器
 * @author bing  @create 2021/1/31-上午9:08
 */
public class TokenLogoutHandler implements LogoutHandler {

    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;

    // 无参构造器
    public TokenLogoutHandler() {
    }

    // 有参构造器
    public TokenLogoutHandler(TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    // 移除 Token
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 1、从 header 里面获取 token
        // 2、token 不为空，移除 token,从 redis 删除 token; [ key 用户名；value 用户权限列表 】
        String token = request.getHeader("token");
        if (token != null) {
            // 移除 token
            tokenManager.removeToken(token);
            // 从 token 获取用户名
            String username = tokenManager.getUserInfoFromToken(token);
            redisTemplate.delete(username);
        }
        ResponseUtil.out(response, R.ok());

    }
}
