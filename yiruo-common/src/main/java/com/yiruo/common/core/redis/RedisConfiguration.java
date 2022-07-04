package com.yiruo.common.core.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * <pre>
 * Title :  Redis的配置类
 * </pre>
 *
 * @author : SunJiYong
 * @since : 2022-02-28
 **/

@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String host;// 主机IP
    @Value("${spring.redis.port}")
    private Integer port;// 端口
//    @Value("${spring.redis.password}")
    private String password;// 密码
    @Value("${spring.redis.expiredTime}")
    private Long expiredTime;// 过期时间

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Long expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Bean
    public JedisPool getPool() {
        return new JedisPool(this.getHost(), this.getPort());
    }

}
