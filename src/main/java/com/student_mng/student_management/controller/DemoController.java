package com.student_mng.student_management.controller;

import com.student_mng.student_management.config.JwtUtil;
import com.student_mng.student_management.dto.AuthRequest;
import com.student_mng.student_management.dto.AuthResponse;
import com.student_mng.student_management.entity.Admin;
import com.student_mng.student_management.entity.User;
import com.student_mng.student_management.enums.Role;
import com.student_mng.student_management.repository.AdminRepository;
import com.student_mng.student_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.student_mng.student_management.config.*;

import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class DemoController {

    @Autowired
    UserRepository userRepo;
    @Autowired
AuthenticationManager authenticationManager;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AdminRepository adminRepo;
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/check")
    public String check () {

        return "Hello welcome to this horrible world" ;

    }

    @GetMapping("/demo")
    public String demoUser () {


        return "done";
    }

        @Autowired
        BCryptPasswordEncoder ps;
    @GetMapping("/regDemo")
    public Admin regDemoAdmin () {

        Admin a = new Admin("lalitoam", ps.encode("123456") , Role.ADMIN, "CSE");
        adminRepo.save(a);
        return a;
    }

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // Fetch full user entity (to get role)
        Optional<User> user = userRepository.findByUsername(authRequest.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.get());

        System.out.println(jwtUtil.extractUserId(token));

        // Create response
        AuthResponse response = new AuthResponse(token,user.get().getId(), user.get().getRole().name());

        return ResponseEntity.ok(response);
    }

}

/*

{
    "id": "22cf4cb4-2dc3-4622-aa3d-9702733a16c7",
    "username": "lalitoam",
    "password": "123456",
    "role": "ADMIN",
    "department": "CSE"
}
 */
