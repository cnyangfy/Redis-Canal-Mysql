package com.example.redismysql.controller;

import com.example.redismysql.canal.Canal;
import com.example.redismysql.mapper.UserMapper;
import com.example.redismysql.pojo.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserController {
    @Resource
    private UserMapper userMapper;

//    @Autowired
//    private RedisTemplate redisTemplate;

//    @RequestMapping(path = "/create_user", method = RequestMethod.POST)
//    public String createUser(@RequestBody User user){
//        try {
//            userMapper.insertUser(user.getUsername(), user.getPassword());
//
//            String username = user.getUsername();
//            String key = "username:" + username + ":password";
//            redisTemplate.opsForValue().set(key, user.getPassword(), 120, TimeUnit.SECONDS);
//            return "successful";
//        }catch(Exception e){
//            return "false";
//        }
//
//    }

    @RequestMapping(path = "/canal_create_user", method = RequestMethod.POST)
    public String canalCreateUser(@RequestBody User user){
        try {
            userMapper.insertUser(user.getUsername(), user.getPassword());
            Canal canal = new Canal();
            canal.run();
            return "successful";
        }catch(Exception e){
            return "false";
        }
    }

//    @GetMapping(path = "/get_user")
//    public User getUser(@RequestParam(value = "userId") int userId) {
//        String key = "user:" + userId;
//        JSONObject info = JSON.parseObject((String) redisTemplate.opsForValue().get(key));
//        try {
//            if (info != null) {
//                System.out.println("redis");
//                return new User((String) info.get("username"), (String) info.get("password"));
//            } else {
//                User user = userMapper.selectByUserId(userId);
//                JSONObject json = new JSONObject();
//                json.put("id", user.getId());
//                json.put("password", user.getPassword());
//                json.put("username", user.getUsername());
//                redisTemplate.opsForValue().set("user:" + user.getId(), json.toJSONString(), 60, TimeUnit.SECONDS);
//                System.out.println("mysql");
//                return user;
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//            return null;
//        }
//    }






}
