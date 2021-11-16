package com.zzm.equaltor;

import com.zzm.equaltor.basepackage.FieldBaseEquator;
import com.zzm.equaltor.basepackage.FieldInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @Author zhangzhimiao
 * @Date 2021/11/16 11:29 上午
 */
public class MutiUserTest {

    @Test
    public void getDiffTest(){
        Company company1 = new Company();
        Company company2 = new Company();
        List<User> list1 = new ArrayList<>();
        List<User> list2 = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            User user = new User(1,"zzm",new Date(),new String[]{"basketball"});
            list1.add(user);
            list2.add(user);
        }
        User user1 = new User(2,"zzm1",new Date(),new String[]{"computer"});
        User user2 = new User(3,"zzm2",new Date(),new String[]{"computer2"});
        list1.add(user1);
        list2.add(user2);
        company1.setUsers(list1);
        company1.setAddress("sdasdas");
        company1.setUser(new User(4,"zzm4",new Date(),new String[]{"computer2"}));
        company2.setUsers(list2);
        company2.setAddress("wwww");

        FieldBaseEquator equator = new FieldBaseEquator();
        List<FieldInfo> diffFields = equator.getDiffFields(company1, company2);
        System.out.println(diffFields);

    }

}
