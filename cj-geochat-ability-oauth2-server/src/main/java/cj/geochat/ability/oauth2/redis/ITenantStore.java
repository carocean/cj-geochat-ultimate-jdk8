package cj.geochat.ability.oauth2.redis;

public interface ITenantStore {
    public void storeTenant(String user,String client_id, String tenant_id) ;
    void remoteTenant(String user,String client_id) ;
}
