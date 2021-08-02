package com.example.redismysql.controller;

import com.example.redismysql.canal.Canal;
import com.example.redismysql.mapper.AgeMapper;
import com.example.redismysql.pojo.Age;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class AgeController {
    @Resource
    private AgeMapper ageMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/canal_create_age", method = RequestMethod.POST)
    public String canalCreateAge(@RequestBody Age age){
        System.out.println(age.getName());
        ageMapper.insertAge(age.getName(), age.getAge());
        Canal canal = new Canal();
        canal.run(redisTemplate);
        return "successful";
    }
}
