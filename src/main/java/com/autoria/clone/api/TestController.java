package com.autoria.clone.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    @PreAuthorize("isAuthenticated()")
    public String protectedEndpoint() {
        return "This is a protected endpoint";
    }

    @PostMapping("/test-role")
    @PreAuthorize("hasRole('ADMIN')")
    public String testRole() {
        return "This is an admin-only endpoint";
    }
}