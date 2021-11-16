package com.zzm.equaltor;

import java.util.Arrays;
import java.util.Date;

public class User extends BaseUser {
    private Date expireTime;
    /**
     * 爱好，此属性用于测试数组属性 deepEquals
     */
    private String[] hobbies;

    public User(long id, String username, Date expireTime, String[] hobbies) {
        super(id, username);
        this.expireTime = expireTime;
        this.hobbies = hobbies;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public void setHobbies(String[] hobbies) {
        this.hobbies = hobbies;
    }

    /**
     * 是否过期，由expireTime计算得出
     * <p>
     * 此属性用于测试基于getter的比对器和基于属性的比对器的不同结果
     */
    public boolean isExpired() {
        return expireTime.before(new Date());
    }

    @Override
    public String toString() {
        return "User{" + super.toString() +
                ",expireTime=" + expireTime +
                ", hobbies=" + Arrays.toString(hobbies) +
                '}';
    }
}
