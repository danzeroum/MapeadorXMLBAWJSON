package br.com.danzeroum.processveritas.domain.repository;

import br.com.danzeroum.processveritas.domain.model.PlatformSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformSettingRepository extends JpaRepository<PlatformSettingEntity, String> {
}
