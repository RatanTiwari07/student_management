package com.student_mng.student_management.controller;

import com.student_mng.student_management.config.JwtUtil;
import com.student_mng.student_management.dto.AuthRequest;
import com.student_mng.student_management.dto.AuthResponse;
import com.student_mng.student_management.entity.Admin;
import com.student_mng.student_management.entity.User;
import com.student_mng.student_management.enums.Role;
import com.student_mng.student_management.repository.AdminRepository;
import com.student_mng.student_management.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/v1/auth")
@RestController
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

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
    @Autowired
    PasswordEncoder passwordEncoder;

    @Operation(
        summary = "Health check endpoint",
        description = "Simple endpoint to check if the authentication service is running"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is running",
                    content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "Hello welcome to this horrible world")))
    })
    @GetMapping("/check")
    public String check () {
        return "Hello welcome to this horrible world" ;
    }

    @Autowired
    BCryptPasswordEncoder ps;

    @Operation(
        summary = "Demo admin registration",
        description = "Creates a demo admin user for testing purposes"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Demo admin created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Admin.class)))
    })
    @GetMapping("/regDemo")
    public Admin regDemoAdmin () {
        Admin a = new Admin("lalitoam", passwordEncoder.encode("123456"), "demo@gmail.com", Role.ADMIN, "CSE");
        adminRepo.save(a);
        return a;
    }

    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT token with user details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = @ExampleObject(value = """
                        {
                            "token": "eyJhbGciOiJIUzI1NiJ9...",
                            "userId": "22cf4cb4-2dc3-4622-aa3d-9702733a16c7",
                            "role": "ADMIN"
                        }
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "User login credentials", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(schema = @Schema(implementation = AuthRequest.class),
                examples = @ExampleObject(value = """
                    {
                        "username": "lalitoam",
                        "password": "123456"
                    }
                    """)))
            @RequestBody AuthRequest authRequest) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        Optional<User> user = userRepository.findByUsername(authRequest.getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String token = jwtUtil.generateToken(user.get());

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
