package net.datenstrudel.bulbs.core.util;

import net.datenstrudel.bulbs.core.application.services.BulbsContextUserService;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.identity.ValidatorBulbsContextUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Thomas Wendzinski.
 */
@Component
public class IdentityUtil {

    @Autowired
    BulbsContextUserService userService;

    public BulbsContextUser createNewTestUser(String credentials){
        BulbsContextUser res = userService.signUp(
                "test_email" + (Math.random() * 1000l) + "@test.datenstrudel.net",
                credentials,
                "test_nick",
                new ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser() {
            @Override
            public void handleDuplicateEmail(String mailAddressConcerned) {
            }

            @Override
            public void handleInvalidPassword() {
            }
        });
        return res;
    }

    public BulbsContextUser signInTestUser(BulbsContextUser toSignIn, String credentials) {
        return userService.signIn(toSignIn.getEmail(), credentials);
    }

    public BulbsContextUser createNewTestUserAndSignIn(String credentials) {
        return signInTestUser(createNewTestUser(credentials), credentials);
    }

}
