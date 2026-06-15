package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.processveritas.domain.model.UserEntity;
import br.com.danzeroum.processveritas.service.AuditService;
import br.com.danzeroum.processveritas.service.UserService;
import br.com.danzeroum.processveritas.web.dto.request.CreateUserRequest;
import br.com.danzeroum.processveritas.web.dto.request.UpdateUserRequest;
import br.com.danzeroum.processveritas.web.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> listUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> page = userService.listUsers(pageable).map(UserResponse::from);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        UserEntity user = userService.createUser(request);
        auditService.log(
                null,
                jwt.getSubject(),
                "CREATE_USER",
                "user:" + user.getId(),
                Map.of("email", user.getEmail(), "role", user.getRole().name()),
                httpRequest.getRemoteAddr()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        UserEntity user = userService.updateUser(id, request);
        auditService.log(
                null,
                jwt.getSubject(),
                "UPDATE_USER",
                "user:" + id,
                request,
                httpRequest.getRemoteAddr()
        );
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {

        userService.deactivateUser(id);
        auditService.log(
                null,
                jwt.getSubject(),
                "DEACTIVATE_USER",
                "user:" + id,
                null,
                httpRequest.getRemoteAddr()
        );
        return ResponseEntity.noContent().build();
    }
}
