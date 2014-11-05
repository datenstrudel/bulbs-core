package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.Entity;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;

import java.util.*;

/**
 *
 * @author Thomas Wendzinski
 */
public class ScheduledActuation extends Entity<ScheduledActuation, String>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private ScheduledActuationId scheduleId;
    private String name;
    private List<AbstractActuatorCmd> states = new ArrayList<>(10);
    private Date created = new Date();
    private boolean deleteAfterExecution = false;
    private Trigger trigger;

    //~ Construction ///////////////////////////////////////////////////////////
    private ScheduledActuation() {
        
    }
    public ScheduledActuation(
            ScheduledActuationId scheduleId, 
            String name, 
            Trigger trigger,
            boolean deleteAfterExecution) {
        this.scheduleId = scheduleId;
        this.name = name;
        this.trigger = trigger;
        this.deleteAfterExecution = deleteAfterExecution;
    }
    public ScheduledActuation(
            ScheduledActuationId scheduleId, 
            String name, 
            boolean deleteAfterExecution) {
        this.scheduleId = scheduleId;
        this.name = name;
        this.deleteAfterExecution = deleteAfterExecution;
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    /**
     * 
     * @param coordinator
     * @throws IllegalStateException in case no trigger has been set so far
     */
    public void activate(JobCoordinator coordinator) throws IllegalStateException{
        if( trigger == null ) throw new IllegalStateException("error.noTrigger");
        if( states == null || states.isEmpty() ) throw new IllegalStateException("error.noStates");
        if(this.isActive(coordinator)){
            coordinator.unSchedule(this.scheduleId.getUuid());
        }
        coordinator.schedule(this.scheduleId.getUuid(), trigger.toCronExpression(), 
                new ScheduledActuationExecutor(scheduleId, scheduleId.getUserId(), name));
        //TODO: Fire SchedulerStatusChangedEvent!
    }
    public void deActivate(JobCoordinator coordinator){
        coordinator.unSchedule(this.scheduleId.getUuid());
    }
    public boolean isActive(JobCoordinator coordinator) {
        return coordinator.isScheduled(this.scheduleId.getUuid());
    }
    /**
     * 
     * @return whether <code>this</code> won't trigger anymore in the future
     * @throws IllegalStateException in case no trigger has been set so far
     */
    public boolean isExpired()throws IllegalStateException{
        if(trigger == null) throw new IllegalStateException("error.noTriggerSet");
        return trigger.isExpired();
    }
    
    public void setNewStates(Collection<AbstractActuatorCmd> states, JobCoordinator coordinator){
        if(states == null)throw new IllegalArgumentException("error.statesMustNotBeNull");
        if(this.isActive(coordinator) && states.isEmpty())
            throw new IllegalStateException("error.emptyStaesNotAllowedOnActiveSchedule");
        this.states.clear();
        this.states.addAll(states);
    }
    
    /**
     * Set a new trigger for this schedule object. The old one will be overridden.
     * In case this scheduler is activated it will be deactivated, and the new trigger will
     * be scheduled
     * @param trigger
     * @param coordinator 
     */
    public void defineTrigger(Trigger trigger, JobCoordinator coordinator){
        boolean active = this.isActive(coordinator);
        if(active){
            this.deActivate(coordinator);
        }
        setTrigger(trigger);
        if(active && !trigger.isExpired()){
            this.activate(coordinator);
        }
    }

    //~ ////////////////////////////////////////////////////////////////////////
    public ScheduledActuationId getScheduleId() {
        return scheduleId;
    }
    public String getName() {
        return name;
    }
    public Trigger getTrigger() {
        return trigger;
    }
    public List<AbstractActuatorCmd> getStates() {
        return new ArrayList<>(this.states);
    }
    public Date getCreated() {
        return created;
    }
    public boolean isDeleteAfterExecution() {
        return deleteAfterExecution;
    }
    
    //~ ////////////////////////////////////////////////////////////////////////
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.scheduleId);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ScheduledActuation other = (ScheduledActuation) obj;
        if (!Objects.equals(this.scheduleId, other.scheduleId)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "ScheduledActuation{" + "scheduleId=" + scheduleId + ", name=" + name + ", trigger=" + trigger + '}';
    }
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private void setScheduleId(ScheduledActuationId scheduleId) {
        this.scheduleId = scheduleId;
    }
    private void setName(String name) {
        this.name = name;
    }
    private void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }
    private void setStates(List<AbstractActuatorCmd> states) {
        this.states = states;
    }
    private void setCreated(Date created) {
        this.created = created;
    }
    private void setDeleteAfterExecution(boolean deleteAfterExecution) {
        this.deleteAfterExecution = deleteAfterExecution;
    }
    
    

}
