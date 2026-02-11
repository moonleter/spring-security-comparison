package cz.osu.kunz.springsecuritycomparison.service.impl;

import cz.osu.kunz.springsecuritycomparison.model.entity.AccessLog;
import cz.osu.kunz.springsecuritycomparison.repository.AccessLogRepository;
import cz.osu.kunz.springsecuritycomparison.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessLogServiceImpl implements AccessLogService {
    private final AccessLogRepository accessLogRepository;

    @Override
    public void logAccess(String username, String action, String protocol, boolean success) {
        AccessLog accessLog = new AccessLog();
        accessLog.setUsername(username);
        accessLog.setAction(action);
        accessLog.setProtocol(protocol);
        accessLog.setSuccess(success);
        accessLogRepository.save(accessLog);
    }
}
