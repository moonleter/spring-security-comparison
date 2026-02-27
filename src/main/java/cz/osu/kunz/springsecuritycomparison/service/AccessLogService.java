package cz.osu.kunz.springsecuritycomparison.service;

public interface AccessLogService {
    void logAccess(String username, String action, String protocol, boolean success);
}