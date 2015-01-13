package net.datenstrudel.bulbs.core.infrastructure.services.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;

/**
 * This interface separates the translation of <b>hardware vendor specific commands<(b> and
 * <b>command responses</b> from technical implementation of communication mechanisms.
 * 
 * @author Thomas Wendzinski
 */
public interface BulbCmdTranslator_HTTP extends BulbCmdTranslator<String, HttpCommand> {

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
