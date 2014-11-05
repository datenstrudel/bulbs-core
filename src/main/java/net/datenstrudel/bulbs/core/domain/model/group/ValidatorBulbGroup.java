package net.datenstrudel.bulbs.core.domain.model.group;

import net.datenstrudel.bulbs.shared.domain.validation.ValidationException;
import net.datenstrudel.bulbs.shared.domain.validation.Validator;
import net.datenstrudel.bulbs.core.domain.model.group.ValidatorBulbGroup.NotificationHandlerBulbGroup;
import net.datenstrudel.bulbs.shared.domain.validation.Validator.ValidationNotificationHandler;

/**
 *
 * @author Thomas Wendzinski
 */
public class ValidatorBulbGroup extends Validator<NotificationHandlerBulbGroup>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private final BulbGroup group2Validate;
    private final BulbGroupRepository groupRepository;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public ValidatorBulbGroup(NotificationHandlerBulbGroup notificationHandler,
            BulbGroup group2Validate,
            BulbGroupRepository groupRepository
            ) {
        super(notificationHandler);
        this.group2Validate = group2Validate;
        this.groupRepository = groupRepository;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public void validateNew() {
        BulbGroup grp = groupRepository.loadByName(
                group2Validate.getGroupId().getUserId(), group2Validate.getName());
        if(grp != null){
            notificationHandler().handleDuplicateGroupname();
            throw new ValidationException("Group with given name already exists!");
        }
    }
    @Override
    public void validateExisting() {
        BulbGroup grp = groupRepository.loadByName(
                group2Validate.getGroupId().getUserId(), group2Validate.getName());
        if(grp != null && !grp.sameIdentityAs(group2Validate)){
            notificationHandler().handleDuplicateGroupname();
            throw new ValidationException("Grp with given name already exists!");
        }
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    //~ Additional Artifact(s) ////////////////////////////////////////////////////
    public static interface NotificationHandlerBulbGroup extends ValidationNotificationHandler{
        void handleDuplicateGroupname();
    }

}
