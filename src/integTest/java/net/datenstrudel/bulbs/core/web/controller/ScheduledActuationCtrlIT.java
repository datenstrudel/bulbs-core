package net.datenstrudel.bulbs.core.web.controller;

import net.datenstrudel.bulbs.core.AbstractBulbsWebIT;
import net.datenstrudel.bulbs.core.TestUtil;
import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.shared.domain.model.client.scheduling.DtoScheduledActuation;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static net.datenstrudel.bulbs.core.web.controller.ControllerTestUtil.getTestPrincipal;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * @author Thomas Wendzinski
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ScheduledActuationCtrlIT extends AbstractBulbsWebIT {

    private static final Logger log = LoggerFactory.getLogger(ScheduledActuationCtrlIT.class);

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @MockBean
    private ModelFacadeOutPort outPort;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void scheduledActuationsByUser() throws Exception {
        final DtoScheduledActuation schedAct = new DtoScheduledActuation("test_Id", false, new Date(), false, "test_SchedName", new Date(), new ArrayList<>(), new PointInTimeTrigger(new Date(), "CET"));

        when(outPort.outputAsSet(DtoScheduledActuation.class)).thenReturn(
                new HashSet<DtoScheduledActuation>() {{
                    add(schedAct);
                }}
        );
        MvcResult mvcResult = mockMvc.perform(
                get("/core/schedules")
                        .accept("application/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(getTestPrincipal())
        ).andReturn();
        String serializedRes = mvcResult.getResponse().getContentAsString();
        List<DtoScheduledActuation> dtoScheduledActuation = TestUtil.jacksonDeserializeList(DtoScheduledActuation.class, serializedRes);
        assertThat(dtoScheduledActuation.get(0), is(schedAct));
    }

    @Test
    public void setNewTrigger_emptyStartAtFails() throws Exception {
        final DtoScheduledActuation schedAct = new DtoScheduledActuation(
                "test_Id", false, new Date(), false, "test_SchedName", new Date(), new ArrayList<>(),
                new PointInTimeTrigger(new Date(), "CET"));
        ReflectionTestUtils.setField(schedAct.getTrigger(), "startAt", null); // !

        when(outPort.outputAsSet(DtoScheduledActuation.class)).thenReturn(
                new HashSet<DtoScheduledActuation>() {{
                    add(schedAct);
                }}
        );
        MvcResult mvcResult = mockMvc.perform(
                put("/core/schedules/test_ID000000000000000000000000000000000000000000000000/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
                        .content(TestUtil.jacksonSerialize(schedAct.getTrigger()))
                        .principal(getTestPrincipal())
        ).andReturn();
        assertThat(mvcResult.getResponse().getStatus(), is(400));
    }

    //    @Test
    public void testCreateAndActivate() throws Exception {
        DtoScheduledActuation dtoNewSchedule = null;
        Authentication authentication = null;

////        MvcResult mvcRes = 
//        this.mockMvc.perform( post("/core/schedules/")
//                .content("{}")
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON)
//        )
////        .andReturn();
////        log.info(mvcRes);
//////        log.info(mvcRes.getAsyncResult());
////        log.info(mvcRes.getResolvedException());
////        log.info(mvcRes.getResponse().getErrorMessage());
//                
//        .andExpect(status().isOk())
//        .andExpect(content().contentType("application/json"));

//        this.mockMvc.perform( get("/core/schedules/tesstId")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//        .andExpect(status().isOk())
//        .andExpect(content().contentType("application/json"));


//        .andExpect(jsonPath("$.name").value("Lee"));

//        
//        DtoScheduledActuation expResult = null;
//        DtoScheduledActuation result = instance.createAndActivate(dtoNewSchedule, authentication);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

}
