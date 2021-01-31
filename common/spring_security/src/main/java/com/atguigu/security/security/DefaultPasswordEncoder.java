package com.atguigu.security.security;

import com.atguigu.utils.utils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 *
 * @author bing  @create 2021/1/30-下午11:13
 */
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    // 有参构造器
    public DefaultPasswordEncoder() {
        this(-1);
    }

    // 无参构造
    public DefaultPasswordEncoder(int strength) {
    }

    // 进行MD5加密
    @Override
    public String encode(CharSequence charSequence) {
        return MD5.encrypt(charSequence.toString());
    }

    // 进行密码比对，参数：加密后密码，传入的密码
    @Override
    public boolean matches(CharSequence charSequence, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(charSequence.toString()));
    }

}

