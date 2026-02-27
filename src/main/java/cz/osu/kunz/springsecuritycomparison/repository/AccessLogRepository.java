package cz.osu.kunz.springsecuritycomparison.repository;

import cz.osu.kunz.springsecuritycomparison.model.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, UUID> {
}
