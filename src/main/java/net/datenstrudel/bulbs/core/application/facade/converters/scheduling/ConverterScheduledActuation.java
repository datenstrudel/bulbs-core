/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.application.facade.converters.scheduling;

import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.scheduling.JobCoordinator;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.shared.domain.model.client.scheduling.DtoScheduledActuation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author Thomas Wendzinski
 */
@Component
public class ConverterScheduledActuation implements DtoConverter<ScheduledActuation, DtoScheduledActuation>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    @Autowired
    JobCoordinator jobCoordinator;
    //~ Construction ///////////////////////////////////////////////////////////
    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    public DtoScheduledActuation convert(ScheduledActuation src) {
        DtoScheduledActuation res = new DtoScheduledActuation();
        res.setScheduledActuationId(
                (String) DtoConverterRegistry.instance()
                        .converterForDomain(ScheduledActuationId.class)
                        .convert(src.getScheduleId()));
        res.setCreated(src.getCreated());
        res.setDeleteAfterExecution(src.isDeleteAfterExecution());
        res.setName(src.getName());
        res.setStates(
                (List) DtoConverterRegistry.instance()
                    .converterForDomain(AbstractActuatorCmd.class)
                    .convertCollection(src.getStates())
        );
        res.setTrigger(src.getTrigger());
        res.setActivated(src.isActive(jobCoordinator));
        res.setNextTriggerTime(src.getTrigger().nextTriggerTime());
        return res;
    }
    @Override
    public ScheduledActuation reverseConvert(DtoScheduledActuation src) {
        throw new UnsupportedOperationException("Won't be supported.");
    }

    @Override
    public Class<DtoScheduledActuation> supportedDtoClass() {
        return DtoScheduledActuation.class;
    }
    @Override
    public Class<ScheduledActuation> supportedDomainClass() {
        return ScheduledActuation.class;
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
