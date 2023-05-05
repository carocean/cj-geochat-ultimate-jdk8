package cj.geochat.ability.oauth2.gateway;

public interface ITenantStore {
    String readTenantId(String x_principal, String clientId);

}
