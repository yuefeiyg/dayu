package com.blcultra.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;


@RequestMapping("/totalsum")
@RestController

public class RedisDistriController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Redisson redisson;

    @RequestMapping("/decrease")
    public String deduceStock(){
        String lockkey = "user";
        RLock redissonlock = redisson.getLock(lockkey);
        try{

            redissonlock.lock(30,TimeUnit.SECONDS);

            int totalsum = Integer.parseInt(stringRedisTemplate.opsForValue().get("totalsum"));

            if(totalsum > 0){
                totalsum -= 1;
                stringRedisTemplate.opsForValue().set("totalsum",totalsum +"");
                System.out.println("减值成功，totalsum = " + totalsum);
            }else{
                System.out.println("减值失败，totalsum = 0");
            }

        }finally {
            redissonlock.unlock();

        }

        return " end ";
    }

}
