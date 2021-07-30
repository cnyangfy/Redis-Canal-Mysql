package com.example.redismysql.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;

    public void redisInsert(List<CanalEntry.Column> columns, JSONObject json){
        redisTemplate.opsForValue().set(columns,json.toJSONString(),120, TimeUnit.SECONDS);
    }

    public void redisDelete(){

    }

    public void redisUpdate(){

    }
}
