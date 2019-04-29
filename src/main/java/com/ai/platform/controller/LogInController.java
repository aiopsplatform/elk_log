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
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        List<Login> list = new ArrayList<>();
        Login login;
        if (username.equals("admin") && password.equals("admin")) {
            login = new Login("0");
            list.add(login);
        } else if (!username.equals("admin") && password.equals("admin")) {
            login = new Login("1");
            list.add(login);
        } else if (username.equals("admin") && !password.equals("admin")) {
            login = new Login("2");
            list.add(login);
        } else {
            login = new Login("3");
            list.add(login);
        }


        return list;
    }

}
