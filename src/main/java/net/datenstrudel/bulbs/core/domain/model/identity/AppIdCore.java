package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;

/**
 *
 * @author Thomas Wendzinski
 */
public class AppIdCore {
    
    private static final AppId ID_CORE = new AppId("APP_TYPE__BULBS_CORE");

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

    //~ Construction ///////////////////////////////////////////////////////////
    private AppIdCore() {
    }
    public static AppId instance(){
        return ID_CORE;
    }

}
