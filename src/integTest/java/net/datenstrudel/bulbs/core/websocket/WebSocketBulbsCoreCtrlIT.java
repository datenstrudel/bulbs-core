package net.datenstrudel.bulbs.core.websocket;

import net.datenstrudel.bulbs.core.AbstractBulbsWebIT;
import net.datenstrudel.bulbs.core.TestUtil;
import net.datenstrudel.bulbs.core.application.services.ActuatorService;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.LinkedList;

import static net.datenstrudel.bulbs.core.web.controller.ControllerTestUtil.getTestPrincipal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WebSocketBulbsCoreCtrlIT extends AbstractBulbsWebIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private ActuatorService actuatorService;

    @Before
    public void setUp() {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build(); // standaloneSetup(instance).build();
        reset(actuatorService);
    }

    @Test
    public void executeBulbActuation() throws Exception {
        DtoBulbActuatorCmd cmdIn = newBulbCmd(); //new DtoBulbActuatorCmd("test_bulbID");
        ArgumentCaptor<BulbActuatorCommand> argCap = ArgumentCaptor.forClass(BulbActuatorCommand.class);
        UsernamePasswordAuthenticationToken testPrincipal = getTestPrincipal();
        MvcResult mvcResult = mockMvc.perform(
                post("/core/bulbs/actuation").content(TestUtil.jacksonSerialize(cmdIn))
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(testPrincipal)
        ).andReturn();
        assertThat(mvcResult.getResponse().getStatus(), is(200));
        verify(actuatorService).executeDeferred(argCap.capture());
        BulbActuatorCommand cmdExecuted = argCap.getValue();
        assertThat(cmdExecuted.getStates(), is(cmdIn.getStates()));
        assertThat(cmdExecuted.getAppId().getUniqueAppName(), is(cmdIn.getAppId()));
        assertThat(cmdExecuted.isLoop(), is(cmdIn.isLoop()));
        assertThat(cmdExecuted.getPriority(), is(cmdIn.getPriority()));
        BulbsContextUser bulbsUser = (BulbsContextUser) testPrincipal.getPrincipal();
        assertThat(cmdExecuted.getUserApiKey(), is(bulbsUser.getApiKey()));
    }

    @Test
    @Ignore("Must be implemented")
    public void executeGroupActuation() throws Exception {
        fail("-Implement-");

    }

    @Test
    @Ignore("Must be implemented")
    public void executePresetActuation() throws Exception {
        fail("-Implement-");

    }

    @Test
    @Ignore("Must be implemented")
    public void executeCancelActuation() throws Exception {
        fail("-Implement-");

    }

    public DtoBulbActuatorCmd newBulbCmd() {
        LinkedList<BulbState> states = new LinkedList<>();
        states.add(new BulbState(new ColorRGB(1, 2, 3), true, 10));
        return new DtoBulbActuatorCmd(
                "test_bulbID-00000000000000000000000000000000000000000",
                AppIdCore.instance().getUniqueAppName(),
                CommandPriority.standard(),
                states, true);
    }

}