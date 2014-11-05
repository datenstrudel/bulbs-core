package net.datenstrudel.bulbs.core.web.controller;

import net.datenstrudel.bulbs.core.application.services.ScheduledActuationService;
import net.datenstrudel.bulbs.core.testConfigs.WebTestConfig;
import net.datenstrudel.bulbs.shared.domain.model.client.preset.DtoPreset;
import net.datenstrudel.bulbs.shared.domain.model.client.scheduling.DtoScheduledActuation;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.easymock.Mock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import static org.junit.Assert.fail;


/**
 *
 * @author Thomas Wendzinski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
//@EnableWebMvc
@ContextConfiguration(classes = {
//    TestConfig.class,
    WebTestConfig.class
})
@Ignore
public class ScheduledActuationCtrlTest {
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledActuationCtrlTest.class);
    ScheduledActuationCtrl instance = new ScheduledActuationCtrl();
    private RequestMappingHandlerAdapter handlerAdapter;  
//    @Autowired
//    private WebApplicationContext wac;
     
    @Mock
    ScheduledActuationService scheduledActuationService;

    
//    private MockMvc mockMvc;
    
    public ScheduledActuationCtrlTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        // Setup Spring test in standalone mode
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
//                .build(); // standaloneSetup(instance).build();
    }

//    @Test
    public void testLoadById() {
        System.out.println("loadById");
        String scheduledActuationUuid = "";
        Authentication authentication = null;
        
        
        DtoPreset expResult = null;
//        DtoPreset result = instance.loadById(scheduledActuationUuid, authentication);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

//    @Test
    public void testCreateAndActivate() throws Exception {
        System.out.println("createAndActivate");
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
