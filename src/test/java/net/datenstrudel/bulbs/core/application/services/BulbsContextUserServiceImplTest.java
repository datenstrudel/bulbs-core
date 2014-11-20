/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.domain.model.identity.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Thomas Wendzinski
 */
@RunWith(MockitoJUnitRunner.class)
public class BulbsContextUserServiceImplTest {
    
    private static final Logger log = LoggerFactory.getLogger(BulbsContextUserServiceImplTest.class);
    @InjectMocks
    BulbsContextUserServiceImpl instance;// = new BulbsContextUserServiceImpl();
    @Mock
    BulbsContextUserRepository mk_userRepository;
    @Mock
    BulbsContextUserDomainService mk_userDomainService;
    @Mock
    ModelFacadeOutPort mk_modelFacadeOutport;
    @Mock
    PasswordEncoder mk_passwordEncoder;
    public BulbsContextUserServiceImplTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testSignUp() {
        System.out.println("signUp");
        String email = "testMail";
        String credentials = "testCredentials";
        String nickname = "testNickname";
        ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser notificationHandler = 
                new ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser() {
            @Override
            public void handleDuplicateEmail(String mailAddressConcerned) {
            }
            @Override
            public void handleInvalidPassword() {
                log.error("Invalid PW");
            }
        };
        when(mk_userDomainService.createNewApiKey()).thenReturn(UUID.randomUUID().toString().toUpperCase());
        when(mk_userRepository.nextIdentity()).thenReturn(new BulbsContextUserId("GEN_USER_ID"));
        when(mk_userRepository.findByEmail(email)).thenReturn(null);
        when(mk_passwordEncoder.encode(credentials)).thenReturn("encryptedCredentials");
        BulbsContextUser result = instance.signUp(email, credentials, nickname, notificationHandler);
        log.info("Created TestUser: " + result);
        verify(mk_userRepository, atLeastOnce()).save(isA(BulbsContextUser.class));
        
        assertEquals(new BulbsContextUserId("GEN_USER_ID"), result.getBulbsContextUserlId());
        assertEquals(email, result.getEmail());
        assertEquals("encryptedCredentials", result.getCredentials());
        assertEquals(nickname, result.getNickname());
    }
}