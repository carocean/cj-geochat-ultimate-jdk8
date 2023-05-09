package cj.geochat.ability.oauth2.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthPageAddress {
    String host;
    String confirm_access_url;
    String login_url;
}
