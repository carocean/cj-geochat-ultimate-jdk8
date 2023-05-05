package cj.geochat.ability.oauth2.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DefaultAppAuthenticationDetails {
    private String remoteAddress;
    private String sessionId;

    public DefaultAppAuthenticationDetails(HttpServletRequest request) {
        this.remoteAddress = request.getRemoteAddr();
        HttpSession session = request.getSession(false);
        this.sessionId = session != null ? session.getId() : null;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
}
