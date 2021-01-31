package com.atguigu.security.filter;

import com.atguigu.security.entity.SecurityUser;
import com.atguigu.security.entity.User;
import com.atguigu.security.security.TokenManager;
import com.atguigu.utils.utils.R;
import com.atguigu.utils.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 认证过滤器
 *
 * @author bing  @create 2021/1/31-上午9:36
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;
    private AuthenticationManager authenticationManager; // 权限管理工具

    // 有参构造器
    public TokenLoginFilter(AuthenticationManager authenticationManager,TokenManager tokenManager,
                            RedisTemplate redisTemplate) {
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
        this.authenticationManager = authenticationManager;
        this.setPostOnly(false); // 非只有 post 提交
        // 设置登录路径匹配 post 提交
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/acl/login", "POST"));
    }

    // 1、获取表单提交的用户名和密码
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 获取表单提交过来的数据(流中获取），交给 springSecurity 管理
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            // 参数：用户名、密码、权限列表
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    // 2、认证成功调用的方法
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 认证成功，得到认证成功之后用户信息
        // getPrincipal 方法得到用户认证之后的信息
        // Object principal = authResult.getPrincipal(); // 默认返回对象是 Object，用实体类 SecurityUser 来封装数据
        SecurityUser user = (SecurityUser) authResult.getPrincipal(); // 类型强转
        // 根据用户名生成 token
        String token = tokenManager.createToken(user.getCurrentUserInfo().getUsername());
        // 把用户名和权限列表放到 redis
        redisTemplate.opsForValue().set(user.getCurrentUserInfo().getUsername(),user.getPermissionValueList());
        // 返回 token
        ResponseUtil.out(response, R.ok().data("token",token));
    }

    // 3、认证失败调用的方法
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ResponseUtil.out(response,R.error());
    }
}
