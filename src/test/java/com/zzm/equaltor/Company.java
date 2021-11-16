package com.zzm.equaltor;

import java.util.List;
import lombok.Data;

/**
 * @Author zhangzhimiao
 * @Date 2021/11/16 3:18 下午
 */
@Data
public class Company {
    List<User> users;
    String address;
    User user;

}
