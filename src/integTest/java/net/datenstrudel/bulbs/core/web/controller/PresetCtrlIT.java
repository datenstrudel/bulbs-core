package net.datenstrudel.bulbs.core.web.controller;


import net.datenstrudel.bulbs.core.TestUtil;
import net.datenstrudel.bulbs.core.application.facade.DtoConverter;
import net.datenstrudel.bulbs.core.application.facade.DtoConverterRegistry;
import net.datenstrudel.bulbs.core.application.facade.ModelFacadeOutPort;
import net.datenstrudel.bulbs.core.application.services.PresetService;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.testConfigs.WebTestConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoAbstractActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.bulb.DtoBulbActuatorCmd;
import net.datenstrudel.bulbs.shared.domain.model.client.preset.DtoPreset;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static net.datenstrudel.bulbs.core.web.controller.ControllerTestUtil.getTestPrincipal;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration()
@EnableWebMvc
@ContextConfiguration(classes = {
        PresetCtrlIT.Cfg.class,
        WebTestConfig.class
})
public class PresetCtrlIT {

    private static final Logger log = LoggerFactory.getLogger(PresetCtrlIT.class);

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private ModelFacadeOutPort outPort;
    @Autowired
    DtoConverterRegistry converterFactory;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build(); // standaloneSetup(instance).build();
    }

    @Test
    public void create()throws Exception {
        List<DtoAbstractActuatorCmd> states = new LinkedList<>();
        states.add(
                new DtoBulbActuatorCmd("test_bulbId-000000000000000000000000000000000000", AppIdCore.instance().getUniqueAppName(),
                CommandPriority.standard(), new LinkedList<BulbState>(){{
                    add(new BulbState(new ColorHSB(120f, 10f, 11f), true, 10) );
                }}, false )
        );
        final DtoPreset entityIn = new DtoPreset(
                "test_presetId-000000000000000000000000000000000000000000000000",
                "test_presetName",
                states
        );

        DtoConverter dtoConverterMk = mock(DtoConverter.class);
        when(converterFactory.converterForDomain(AbstractActuatorCmd.class)).thenReturn(dtoConverterMk);
        ArgumentCaptor<Collection> presetStatesCaptor = ArgumentCaptor.forClass(Collection.class);
        when(outPort.outputAs(DtoPreset.class)).thenReturn(entityIn);
        MvcResult mvcResult = mockMvc.perform(
                post("/core/presets").content(TestUtil.jacksonSerialize(entityIn))
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(getTestPrincipal())
        ).andReturn();

        assertThat(mvcResult.getResponse().getStatus(), is(200));
        verify(dtoConverterMk).reverseConvertCollection(presetStatesCaptor.capture());
        assertThat(presetStatesCaptor.getValue(), is(entityIn.getStates()));

        String serializedRes = mvcResult.getResponse().getContentAsString();
        DtoPreset entityOut = TestUtil.jacksonDeserialize(DtoPreset.class, serializedRes);
        assertThat(entityOut.getStates(), is(entityIn.getStates()));
        assertThat(entityOut.getName(), is(entityIn.getName()));
        assertThat(entityOut.getPresetId(), is(entityIn.getPresetId()) );
    }

    @Configuration
    public static class Cfg{
        @Bean
        public PresetCtrl presetCtrl() {
            return new PresetCtrl();
        }
        @Bean
        public PresetService presetService() {
            return mock(PresetService.class);
        }

        @Bean
        public ModelFacadeOutPort modelPort() {
            return mock(ModelFacadeOutPort.class);
        }
        @Bean
        public DtoConverterRegistry converterFactory() {
            return mock(DtoConverterRegistry.class);
        }
    }
}
