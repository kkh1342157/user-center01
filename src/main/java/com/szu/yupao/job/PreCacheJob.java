package com.szu.yupao.job;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.TimeUtil;
import com.szu.yupao.mapper.UserMapper;
import com.szu.yupao.pojo.User;
import com.szu.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PreCacheJob {

    @Autowired
    private RedisTemplate<String ,Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    //重点用户
    List<Long> mainUserList = Arrays.asList(1L);

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 每天加载，预热推荐用户
     */
    @Scheduled(cron = "0 47 13 * * *")
    synchronized public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("yupao:preachejob:recommend:lock");
        try {
            if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
                System.out.println(Thread.currentThread().getId() + "获得lock");
                for (Long userId :mainUserList) {
                    Page<User> page = userService.page(new Page<>(1, 20));

                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String ,Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(redisKey,page,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set fail,",e);
                    }
                }

            }
        } catch (InterruptedException e) {
            log.error("doRecommendUser error message: ",e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
                System.out.println(Thread.currentThread().getId() +  "lock 释放");
            }
        }




    }
}
