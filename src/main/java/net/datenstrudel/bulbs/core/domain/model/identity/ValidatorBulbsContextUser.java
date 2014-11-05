package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.core.domain.model.identity.ValidatorBulbsContextUser.NotificationHandlerBulbsContextUser;
import net.datenstrudel.bulbs.shared.domain.validation.Validator.ValidationNotificationHandler;
import net.datenstrudel.bulbs.shared.domain.validation.ValidationException;
import net.datenstrudel.bulbs.shared.domain.validation.Validator;

/**
 *
 * @author Thomas Wendzinski
 */
public class ValidatorBulbsContextUser extends Validator<NotificationHandlerBulbsContextUser> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private final BulbsContextUserRepository userRepository;
    private final BulbsContextUser user2Val;
    private final String oldMail;

    //~ Construction ///////////////////////////////////////////////////////////
    public ValidatorBulbsContextUser(
            BulbsContextUser bulbsContextUser,
            BulbsContextUserRepository userRepository,
            String oldMail,
            NotificationHandlerBulbsContextUser notificationHandler) {
        super(notificationHandler);
        this.userRepository = userRepository;
        this.user2Val = bulbsContextUser;
        this.oldMail = oldMail;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void validateNew() throws ValidationException{

        BulbsContextUser existingUsr = userRepository.loadByEmail(user2Val.getEmail());
        if(existingUsr != null){
            notificationHandler().handleDuplicateEmail(user2Val.getEmail());
            throw new ValidationException("User with given mail["+user2Val.getEmail()+"] already exists!");
        }
        if(user2Val.getPassword().length() < 6) {
            notificationHandler().handleInvalidPassword();
            throw new ValidationException("Password error");
        }
    }
    @Override
    public void validateExisting() throws ValidationException{
        if(oldMail != null && !user2Val.getEmail().equals(this.oldMail)){
            BulbsContextUser existingUsr = userRepository.loadByEmail(user2Val.getEmail());
            if(existingUsr != null){
                notificationHandler().handleDuplicateEmail(user2Val.getEmail());
                throw new ValidationException("User with given mail["+user2Val.getEmail()+"] already exists!");
            }
        }
        if(user2Val.getPassword().length() < 6) {
            notificationHandler().handleInvalidPassword();
            throw new ValidationException("Password error");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public static interface NotificationHandlerBulbsContextUser extends ValidationNotificationHandler{
        void handleDuplicateEmail(String mailAddressConcerned);
        void handleInvalidPassword();
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
