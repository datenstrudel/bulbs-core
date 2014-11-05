/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.domain.model.identity.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbsContextUserServiceImplTest {
    
    private static final Logger log = LoggerFactory.getLogger(BulbsContextUserServiceImplTest.class);
    
    BulbsContextUserServiceImpl instance = new BulbsContextUserServiceImpl();
    BulbsContextUserRepository mk_userRepository;
    BulbsContextUserDomainService mk_userDomainService;
    ModelFacadeOutPort mk_modelFacadeOutport;
    PasswordEncoder mk_passwordEncoder;
    public BulbsContextUserServiceImplTest() {
    }
    
    @Before
    public void setUp() {
        mk_userRepository = createMock(BulbsContextUserRepository.class);
        ReflectionTestUtils.setField(instance, "userRepository", mk_userRepository);
        mk_userDomainService = createMock(BulbsContextUserDomainService.class);
        ReflectionTestUtils.setField(instance, "userService", mk_userDomainService);
        mk_modelFacadeOutport = new ModelFacadeOutPort();
        ReflectionTestUtils.setField(instance, "outPort", mk_modelFacadeOutport);
        mk_passwordEncoder = createMock(PasswordEncoder.class);
        ReflectionTestUtils.setField(instance, "passwordEncoder", mk_passwordEncoder);
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
        expect(mk_userDomainService.createNewApiKey()).andReturn(UUID.randomUUID().toString().toUpperCase());
        expect(mk_userRepository.nextIdentity()).andReturn(new BulbsContextUserId("GEN_USER_ID"));
        expect(mk_userRepository.loadByEmail(email)).andReturn(null);
        expect(mk_passwordEncoder.encode(credentials)).andReturn("encryptedCredentials");
        mk_userRepository.store(isA(BulbsContextUser.class));
        expectLastCall();
        replay(mk_userDomainService, mk_userRepository, mk_passwordEncoder);
        BulbsContextUser result = instance.signUp(email, credentials, nickname, notificationHandler);
        log.info("Created TestUser: " + result);
        verify(mk_userRepository);
        
        assertEquals(new BulbsContextUserId("GEN_USER_ID"), result.getBulbsContextUserlId());
        assertEquals(email, result.getEmail());
        assertEquals("encryptedCredentials", result.getCredentials());
        assertEquals(nickname, result.getNickname());
        
    }
}