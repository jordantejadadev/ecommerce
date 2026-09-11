package com.jordan.ecommerce.controller;

import com.jordan.ecommerce.dto.user.AdminUserResponse;
import com.jordan.ecommerce.dto.user.UpdateUserRoleRequest;
import com.jordan.ecommerce.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {

        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<AdminUserResponse> updateRole(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRoleRequest request
            ) {
        return ResponseEntity.ok(
                adminUserService.updateRole(userId, request.role())
        );
    }
}
