package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import java.util.Objects;

/**
 *
 * @author Thomas Wendzinski
 */
public class PriorityCoordinator implements ValueObject<PriorityCoordinator>{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(PriorityCoordinator.class);
    private AppId lastActor;
    private AppId suspendedActor;
    private Long lastOverrideTime;
    private Integer overrideDurationMillis;
    

    public PriorityCoordinator() {
    }
    public PriorityCoordinator(
            AppId lastActor, AppId suspendedActor, Long lastOverrideTime, 
            Integer overrideDurationMillis) {
        this.lastActor = lastActor;
        this.suspendedActor = suspendedActor;
        this.lastOverrideTime = lastOverrideTime;
        this.overrideDurationMillis = overrideDurationMillis;
    }

    public AppId getLastActor() {
        return lastActor;
    }
    public AppId getSuspendedActor() {
        return suspendedActor;
    }
    public Long getLastOverrideTime() {
        return lastOverrideTime;
    }
    public Integer getOverrideDurationMillis() {
        return overrideDurationMillis;
    }

    public boolean executionAllowedFor(AbstractActuatorCmd cmd){
        AppId appId = cmd.getAppId();
        CommandPriority priority = cmd.getPriority();
        if(appId == null){
            log.warn("Command did not contain an AppId: " + cmd);
//            lastActionTime = System.currentTimeMillis();
            return false;
        }
//        if(appId.sameValueAs(lastActor) ){
//            log.warn("Multiple overrides by same Application not allowed: " + appId);
//            return false;
//        }
        if(lastOverrideTime == null)lastOverrideTime = System.currentTimeMillis();
        
        if(CommandPriority.OVERRIDE == priority.getPriority()
                && priority.getOverrideTempMillis() != null){
            this.overrideDurationMillis = priority.getOverrideTempMillis();
            this.lastOverrideTime = System.currentTimeMillis();
            this.suspendedActor = this.lastActor;
            this.lastActor = appId;
            return true;
        }
        if(CommandPriority.OVERRIDE == priority.getPriority() ){
            overrideDurationMillis = null;
            this.lastOverrideTime = System.currentTimeMillis();
            this.lastActor = appId;
            return true;
        }
        if(CommandPriority.STANDARD == priority.getPriority()){
            if(overrideDurationMillis != null && 
                    (lastOverrideTime + overrideDurationMillis) < System.currentTimeMillis()){
                overrideDurationMillis = null; //Duration elapsed
                this.lastActor = suspendedActor;
                if(appId.sameValueAs(lastActor))return true;
                else return false;
            }else{
                if(lastActor == null)return true;
                if(appId.sameValueAs(lastActor))return true;
                else return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.lastActor);
        hash = 97 * hash + Objects.hashCode(this.suspendedActor);
        hash = 97 * hash + Objects.hashCode(this.lastOverrideTime);
        hash = 97 * hash + Objects.hashCode(this.overrideDurationMillis);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PriorityCoordinator other = (PriorityCoordinator) obj;
        return this.sameValueAs(other);
    }
    
    @Override
    public boolean sameValueAs(PriorityCoordinator other) {
        if(other == null) return false;
        if (!Objects.equals(this.lastActor, other.lastActor)) {
            return false;
        }
        if (!Objects.equals(this.suspendedActor, other.suspendedActor)) {
            return false;
        }
        if (!Objects.equals(this.lastOverrideTime, other.lastOverrideTime)) {
            return false;
        }
        if (!Objects.equals(this.overrideDurationMillis, other.overrideDurationMillis)) {
            return false;
        }
        return true;
    }

    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
