package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Autowired
    private JwtEncoder encoder;

    @Value("${pv.jwt.access-token-expiry:900}")
    private long accessExpiry;

    @Value("${pv.jwt.refresh-token-expiry:604800}")
    private long refreshExpiry;

    public String createAccessToken(UserEntity user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("process-veritas")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessExpiry))
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId().toString())
                .claim("type", "access")
                .build();
        log.debug("Creating access token for user={}", user.getEmail());
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String createRefreshToken(UserEntity user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("process-veritas")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshExpiry))
                .subject(user.getEmail())
                .claim("type", "refresh")
                .build();
        log.debug("Creating refresh token for user={}", user.getEmail());
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long getAccessExpiry() {
        return accessExpiry;
    }
}
