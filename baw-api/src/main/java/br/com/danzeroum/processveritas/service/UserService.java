package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.UserEntity;
import br.com.danzeroum.processveritas.domain.model.UserEntity.Role;
import br.com.danzeroum.processveritas.domain.repository.UserRepository;
import br.com.danzeroum.processveritas.web.dto.request.CreateUserRequest;
import br.com.danzeroum.processveritas.web.dto.request.UpdateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<UserEntity> listUsers(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    public UserEntity getUserById(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    @Transactional
    public UserEntity createUser(CreateUserRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use: " + request.getEmail());
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setRole(Role.valueOf(request.getRole()));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        UserEntity saved = userRepo.save(user);
        log.info("User created: id={} email={} role={}", saved.getId(), saved.getEmail(), saved.getRole());
        return saved;
    }

    @Transactional
    public UserEntity updateUser(UUID id, UpdateUserRequest request) {
        UserEntity user = getUserById(id);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getRole() != null) {
            user.setRole(Role.valueOf(request.getRole()));
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        UserEntity saved = userRepo.save(user);
        log.info("User updated: id={}", id);
        return saved;
    }

    @Transactional
    public void deactivateUser(UUID id) {
        UserEntity user = getUserById(id);
        user.setActive(false);
        userRepo.save(user);
        log.info("User deactivated: id={}", id);
    }
}
