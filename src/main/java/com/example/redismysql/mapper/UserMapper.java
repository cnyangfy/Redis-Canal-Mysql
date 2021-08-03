package com.example.redismysql.mapper;

import com.example.redismysql.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    void insertUser(@Param("username") String username, @Param("password") String password);
    void deleteUser(@Param("id") int id);
    void changeP(@Param("id") int id, @Param("password") String password);
    User selectByUsername(@Param("username") String username);
    User selectByUserId(@Param("id") int id);
}
