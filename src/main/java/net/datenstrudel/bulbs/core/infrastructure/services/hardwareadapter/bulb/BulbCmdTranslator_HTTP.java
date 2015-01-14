package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb;

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
