package com.ai.platform.controller;

import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.net.UnknownHostException;
import java.util.Set;

@RestController
public class DemoController {

    @RequestMapping("/test")
    public String test(HttpRequest request, @RequestParam String name){

        return null;

    }


}
