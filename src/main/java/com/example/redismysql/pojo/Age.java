package com.example.redismysql.pojo;

import java.io.Serializable;

public class Age implements Serializable{

    private int id;
        private String username;
        private int age;
        public Age(){
        }

    public Age(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
