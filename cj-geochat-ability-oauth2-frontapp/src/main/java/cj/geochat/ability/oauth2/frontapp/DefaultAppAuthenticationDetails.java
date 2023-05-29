package cj.geochat.ability.oauth2.frontapp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DefaultAppAuthenticationDetails {
    boolean isFromGateway;
    private String remoteAddress;
    private String sessionId;

    public DefaultAppAuthenticationDetails(boolean isFromGateway, HttpServletRequest request) {
        this.remoteAddress = request.getRemoteAddr();
        HttpSession session = request.getSession(false);
        this.sessionId = session != null ? session.getId() : null;
        this.isFromGateway = isFromGateway;
    }

    public boolean isFromGateway() {
        return isFromGateway;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
}
