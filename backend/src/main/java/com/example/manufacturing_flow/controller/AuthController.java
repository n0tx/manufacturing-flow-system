package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.dto.AuthRequest;
import com.example.manufacturing_flow.dto.AuthResponse;
import com.example.manufacturing_flow.entity.Role;
import com.example.manufacturing_flow.entity.User;
import com.example.manufacturing_flow.repository.UserRepository;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Login user to get JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        
        final String jwt = jwtUtil.generateToken(userDetails, user.getRole().name());

        return ResponseEntity.ok(ApiResponse.success("Login successful", new AuthResponse(jwt, user.getRole().name())));
    }
    
    // Seed users endpoint for demo purpose (MVP)
    @PostMapping("/seed")
    @Operation(summary = "Seed dummy users for demo")
    public ResponseEntity<ApiResponse<String>> seedUsers() {
        if (userRepository.count() == 0) {
            userRepository.save(new User(null, "admin", passwordEncoder.encode("admin123"), Role.ADMIN));
            userRepository.save(new User(null, "gudang", passwordEncoder.encode("gudang123"), Role.GUDANG));
            userRepository.save(new User(null, "produksi", passwordEncoder.encode("produksi123"), Role.PRODUKSI));
            userRepository.save(new User(null, "finance", passwordEncoder.encode("finance123"), Role.FINANCE));
            return ResponseEntity.ok(ApiResponse.success("Users seeded successfully", null));
        }
        return ResponseEntity.ok(ApiResponse.success("Users already seeded", null));
    }
}
