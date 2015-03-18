package net.datenstrudel.bulbs.core.web.controller;

import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Created by Thomas Wendzinski.
 */
public class ControllerTestUtil {

    public static UsernamePasswordAuthenticationToken getTestPrincipal() {
        BulbsContextUser user = new BulbsContextUser(
                new BulbsContextUserId("test_uuid"),
                "test_email", "test_Cred", "test_username", "test_apiKey");
        return new UsernamePasswordAuthenticationToken(user, "test_credentials");
    }
}
