package cj.geochat.ability.oauth2.gateway;

import cj.geochat.ability.oauth2.gateway.properties.ClientInfo;

public interface ILookupClient {
    ClientInfo lookup(String client_id);
}
