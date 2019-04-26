package com.ai.platform.controller;


import com.ai.pojo.Login;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户登陆操作
 */
@RestController
@RequestMapping(value = "index")
public class LogInController {

    @PostMapping(value = "loginRequest")
    @ResponseBody
    public List getLogin(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
//        Map<String, String> map = new HashMap<>();
        List list = new ArrayList();
        Login login;
        if (username.equals("admin") && password.equals("admin")) {
            login = new Login("0");
            list.add(login);
//            map.put("0", "登陆成功");
        } else if (!username.equals("admin") && password.equals("admin")) {
            login = new Login("1");
            list.add(login);
//            map.put("1", "登陆失败，用户名错误");
        } else if (username.equals("admin") && !password.equals("admin")) {
            login = new Login("2");
            list.add(login);
//            map.put("2", "登陆失败，密码错误");
        } else {
            login = new Login("3");
            list.add(login);
//            map.put("3", "登陆失败，用户名密码错误");
        }


        return list;
    }

}
