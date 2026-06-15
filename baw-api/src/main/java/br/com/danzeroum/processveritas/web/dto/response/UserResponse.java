package br.com.danzeroum.processveritas.web.dto.response;

import br.com.danzeroum.processveritas.domain.model.UserEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String email;
    private String name;
    private String role;
    private boolean active;
    private OffsetDateTime createdAt;

    public static UserResponse from(UserEntity user) {
        UserResponse r = new UserResponse();
        r.id = user.getId();
        r.email = user.getEmail();
        r.name = user.getName();
        r.role = user.getRole().name();
        r.active = user.isActive();
        r.createdAt = user.getCreatedAt();
        return r;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
