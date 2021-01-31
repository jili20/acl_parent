package com.atguigu.security.security;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.lettuce.core.codec.CompressionCodec;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 生成 token
 *
 * @author bing  @create 2021/1/30-下午11:32
 */
@Component
public class TokenManager {

    //编码秘钥：要 JVM 方式生成复杂一些的
    private String tokenSignKey = "123456";

    // 1. 使用 jwt 根据用户名生成 token
    public String createToken(String username) {
        long tokenEcpiration = 24 * 60 * 60 * 1000; // token 有效时长；单位：毫秒；1天过期
        return Jwts.builder().setSubject(username) // 主体
                .setExpiration(new Date(System.currentTimeMillis() + tokenEcpiration)) // 过期时间 当前时间毫秒数+有效时长
                .signWith(SignatureAlgorithm.ES512, tokenSignKey).compressWith(CompressionCodecs.GZIP).compact();
    }

    // 2. 根据 token 字符串得到用户信息
    public String getUserInfoFromToken(String token){
        return Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 3 删除token：不需要写，前端不携带 token 就行
    public void removeToken(String token) { }
}
