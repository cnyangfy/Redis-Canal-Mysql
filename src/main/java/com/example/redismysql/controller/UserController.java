package com.example.redismysql.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.redismysql.canal.Canal;
import com.example.redismysql.mapper.UserMapper;
import com.example.redismysql.pojo.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {
    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/create_user", method = RequestMethod.POST)
    public String createUser(@RequestBody User user){
        try {
            userMapper.insertUser(user.getUsername(), user.getPassword());

            String username = user.getUsername();
            String key = "username:" + username + ":password";
            redisTemplate.opsForValue().set(key, user.getPassword(), 120, TimeUnit.SECONDS);
            return "successful";
        }catch(Exception e){
            return "false";
        }

    }

    @RequestMapping(path = "/canal_create_user", method = RequestMethod.POST)
    public String canalCreateUser(@RequestBody User user){
        try {
            userMapper.insertUser(user.getUsername(), user.getPassword());
            Canal canal = new Canal();
            canal.run(redisTemplate);
            return "successful";
        }catch(Exception e){
            System.out.println(e);
            return "false";
        }
    }

    @GetMapping(path = "/get_user")
    public User getUser(@RequestParam(value = "userId") int userId) {
        String key = "user:" + userId;
        JSONObject info = JSON.parseObject((String) redisTemplate.opsForValue().get(key));
        try {
            if (info != null) {
                System.out.println("redis");
                return new User((String) info.get("username"), (String) info.get("password"));
            } else {
                User user = userMapper.selectByUserId(userId);
                JSONObject json = new JSONObject();
                json.put("id", user.getId());
                json.put("password", user.getPassword());
                json.put("username", user.getUsername());
                redisTemplate.opsForValue().set("user:" + user.getId(), json.toJSONString(), 60, TimeUnit.SECONDS);
                System.out.println("mysql");
                return user;
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    @GetMapping(path = "/canal_delete_user")
    public String canalDeleteUser(@RequestParam(value = "id") int id){
        try {
            userMapper.deleteUser(id);
            Canal canal = new Canal();
            canal.run(redisTemplate);
            return "successful";
        }catch(Exception e){
            System.out.println(e);
            return "false";
        }
    }

    @GetMapping(path = "/change_password")
    public String changePassword(@RequestParam(value = "id") int id,@RequestParam(value = "newp") String newp){
        try {
            userMapper.changeP(id, newp);
            Canal canal = new Canal();
            canal.run(redisTemplate);
            return "successful";
        }catch(Exception e){
            System.out.println(e);
            return "false";
        }
    }
    @GetMapping(path = "/get_all")
    public String getAll(){
        try {
            int pageSize = 5;
            int pageNum = 1;
            PageHelper.startPage(pageNum, pageSize);
            List<User> list = userMapper.selectAll();
            PageInfo<User> pageInfo = new PageInfo<User>(list);
            System.out.println(pageInfo);
            System.out.println(pageInfo.getPages());
            return "successful";
        }catch(Exception e){
            System.out.println(e);
            return "false";
        }
    }


}
