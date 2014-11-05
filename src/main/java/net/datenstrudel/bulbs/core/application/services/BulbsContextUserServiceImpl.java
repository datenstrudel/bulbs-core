package net.datenstrudel.bulbs.core.application.services;

import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.domain.model.identity.*;
import net.datenstrudel.bulbs.core.domain.model.identity.ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser;
import net.datenstrudel.bulbs.core.domain.model.messaging.IllegalArgumentExceptionNotRepeatable;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thomas Wendzinski
 */
@Service(value="bulbsContextUserService")
public class BulbsContextUserServiceImpl implements BulbsContextUserService{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbsContextUserServiceImpl.class);
    @Autowired
    private BulbsContextUserRepository userRepository;
    @Autowired
    private BulbsContextUserDomainService userService;
    @Autowired 
    private ModelFacadeOutPort outPort;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //~ Construction ///////////////////////////////////////////////////////////
    public BulbsContextUserServiceImpl() {}

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public BulbsContextUser loadById(BulbsContextUserId userId){
        BulbsContextUser res = userRepository.loadById(userId);
        outPort.write(res);
        return res;
    }

    @Override
    public BulbsContextUser signUp(
            String email, 
            String credentials, 
            String nickname,
            NotificationHandlerBulbsContextUser notificationHandler) {
        
        log.info("|-- Going to create new BulbsContextUser with email[" + email + "]..");
        BulbsContextUser user = new BulbsContextUser(
                userRepository.nextIdentity(), email,
                passwordEncoder.encode(credentials),
                nickname,
                userService.createNewApiKey());
        user.validateNew(notificationHandler, userRepository);
        userRepository.store(user);
        log.info(" -- BulbsContextUser created: " + user);
        outPort.write(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BulbsContextUser user = userRepository.loadByEmail(username);
        if(user == null) throw new UsernameNotFoundException("User not found: " + username);
        return user;
    }
    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        String apiKey = (String)token.getPrincipal();
        BulbsContextUser user = userRepository.loadByApiKey(apiKey);
        if(user == null) throw new UsernameNotFoundException("User not found: " + apiKey);
        return user;
    }
    
    @Override
    public BulbsContextUser signIn(
            String email, 
            String credentials
            ) {
        
        log.info("|-- Going to sign in BulbsContextUser with email['"+email+"']..");
        BulbsContextUser user = userRepository.loadByEmail(email);
        if(user == null) throw new IllegalArgumentException("errors.email.notfound");
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, credentials, user.getAuthorities());
        authenticationManager.authenticate(authentication);
        log.info("-- BulbsContextUser authenticated: " + user);
        outPort.write(user);
        return user;
    }

    @Override
    public void modifyPassword(String apiKey, String newCredentials,
                               NotificationHandlerBulbsContextUser validationNotificationHandler) {
        BulbsContextUser contextUser = this.userRepository.loadByApiKey(apiKey);

        contextUser.modifyPassword(passwordEncoder.encode(newCredentials));
        contextUser.validateExisting(validationNotificationHandler, userRepository, null);
        this.userRepository.store(contextUser);
    }

    @Override
    public void addBulbsPrincipal(BulbsContextUserId userId, BulbsPrincipal newPrincipal) {
        log.info("|-- Going to assign new BulbsPrincipal["+newPrincipal+"] to BulbsContextUser["+userId+"]");
        assert(userId != null);
        BulbsContextUser user = userRepository.loadById(userId);
        if(user == null) throw new IllegalArgumentExceptionNotRepeatable("User["+userId+"] doesn't exist!");
        
        if( newPrincipal.getState() != BulbsPrincipalState.PENDING )
            throw new IllegalStateException("BulbsPrincipal for assignment has not state PENDING which indicates it is not suited to be assigned to a user yet.");
        user.addBulbsPrincipal(newPrincipal);
        userRepository.store(user);
        log.info(" -- BulbsPrincipal["+newPrincipal+"] successfully assigned to BulbsContextUser["+userId+"].");
    }
    @Override
    public void removeBulbsPrincipal(BulbsContextUserId userId, BulbsPrincipal principal) {
        log.info("|-- Going to finally delete BulbsPrincipal["+principal+"] from BulbsContextUser["+userId+"]");
        assert(userId != null);
        BulbsContextUser user = userRepository.loadById(userId);
        if(user == null) throw new IllegalArgumentException("User["+userId+"] doesn't exist!");
        
        user.removeBulbsPrincipal(principal);
        userRepository.store(user);
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    
}
