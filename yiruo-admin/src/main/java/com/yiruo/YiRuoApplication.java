package com.yiruo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author yiruo
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class YiRuoApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(YiRuoApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  阳光系统启动成功   ლ(´ڡ`ლ)ﾞ  " );
    }
}
