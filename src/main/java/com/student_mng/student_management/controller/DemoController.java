package com.student_mng.student_management.controller;

import com.student_mng.student_management.entity.User;
import com.student_mng.student_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    UserRepository userRepo;

    @GetMapping("/check")
    public String check () {

        return "Hello welcome to this horrible world" ;

    }

    @GetMapping("/demo")
    public String demoUser () {


        return "done";
    }


}
