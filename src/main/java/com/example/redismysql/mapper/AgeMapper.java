package com.example.redismysql.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AgeMapper {
    void insertAge(@Param("username") String username, @Param("age") int age);
}
