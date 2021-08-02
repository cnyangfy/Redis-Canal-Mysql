//package com.example.redismysql.util;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.otter.canal.protocol.CanalEntry;
//
//import java.util.List;
//
//public class RedisUtil {
//
//    public static void insert(String tableName, List<CanalEntry.Column> columns, JSONObject json){
//        redisTemplate.opsForValue().set(tableName+columns,json.toJSONString(),120, TimeUnit.SECONDS);
//    }
//
//    public void redisDelete(){
//
//    }
//
//    public void redisUpdate(){
//
//    }
//}
