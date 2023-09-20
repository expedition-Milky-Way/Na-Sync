package vip.yzxh.Util.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
@Configuration
public class LoginStatusLockConfig {


    /**
     * 设置登录状态时的不可重入锁（每次并发只有一个用户能够成功登录）
     *
     * @return
     */
    @Bean
    public Lock loginLock() {
        return new ReentrantLock(false);
    }
}
