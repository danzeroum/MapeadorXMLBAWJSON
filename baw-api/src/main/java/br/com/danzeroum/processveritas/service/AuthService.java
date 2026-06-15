package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.UserEntity;
import br.com.danzeroum.processveritas.domain.repository.UserRepository;
import br.com.danzeroum.processveritas.service.exception.AuthException;
import br.com.danzeroum.processveritas.web.dto.response.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtDecoder jwtDecoder;

    public AuthResponse login(String email, String password) throws AuthException {
        UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!user.isActive()) {
            throw new AuthException("Account is disabled");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthException("Invalid credentials");
        }

        log.info("Successful login for user={}", email);
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, jwtService.getAccessExpiry());
    }

    public AuthResponse refresh(String refreshToken) throws AuthException {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(refreshToken);
        } catch (JwtException e) {
            throw new AuthException("Invalid or expired refresh token", e);
        }

        String type = jwt.getClaim("type");
        if (!"refresh".equals(type)) {
            throw new AuthException("Token is not a refresh token");
        }

        String email = jwt.getSubject();
        UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        if (!user.isActive()) {
            throw new AuthException("Account is disabled");
        }

        log.info("Token refreshed for user={}", email);
        String newAccessToken = jwtService.createAccessToken(user);
        String newRefreshToken = jwtService.createRefreshToken(user);
        return new AuthResponse(newAccessToken, newRefreshToken, jwtService.getAccessExpiry());
    }
}
