package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.philipshue.BulbCmdTranslator_PhilipsHue;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.Assert.*;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbCmdTranslator_PhilipsHueTest {
    
    //~ Member(s) ////////////////////////////////////////////////////////////////
    static final Logger log = LoggerFactory.getLogger(BulbCmdTranslator_PhilipsHueTest.class);
    BulbBridgeAddress T_BRIDGE_ADDRESS = new BulbBridgeAddress("localhost", 0);
    BulbsPrincipal T_PRINCIPAL = new BulbsPrincipal(
            "test_username", new AppId("test_deviceType"), "test_bridgeId", BulbsPrincipalState.REQUESTED);
    BulbCmdTranslator_PhilipsHue instance = new BulbCmdTranslator_PhilipsHue();
    
    public BulbCmdTranslator_PhilipsHueTest() {
    }
    
    @Test
    public void testBridgeFromJson() {
        System.out.println("bridgeFromPayload");
        String json = "{\n" +
        "    \"proxyport\": 0,\n" +
        "    \"utc\": \"2012-10-29T12:00:00\",\n" +
        "    \"name\": \"Smartbridge 1\",\n" +
        "    \"swupdate\": {\n" +
        "        \"updatestate\":1,\n" +
        "         \"url\": \"www.meethue.com/patchnotes/1453\",\n" +
        "         \"text\": \"This is a software update\",\n" +
        "         \"notify\": false\n" +
        "     },\n" +
        "    \"whitelist\": {\n" +
        "        \"1234567890\": {\n" +
        "            \"last use date\": \"2010-10-17T01:23:20\",\n" +
        "            \"create date\": \"2010-10-17T01:23:20\",\n" +
        "            \"name\": \"iPhone Web 1\"\n" +
        "        }\n" +
        "    },\n" +
        "    \"swversion\": \"01003542\",\n" +
        "    \"proxyaddress\": \"none\",\n" +
        "    \"mac\": \"00:17:88:00:00:00\",        \n" +
        "    \"linkbutton\": false,\n" +
        "    \"ipaddress\": \"192.168.1.100\",\n" +
        "    \"netmask\": \"255.255.0.0\",\n" +
        "    \"gateway\": \"192.168.0.1\",\n" +
        "    \"dhcp\": false\n" +
        "}";
        BulbBridge expResult = null;
        BulbBridgeId bridgeId = new BulbBridgeId(UUID.randomUUID().toString());
        BulbsContextUserId contextUserId = new BulbsContextUserId("userUuid");
        BulbBridge result = instance.bridgeFromPayload(json, bridgeId, T_BRIDGE_ADDRESS, contextUserId);
        
        assertEquals(bridgeId, result.getId());
        assertEquals("00:17:88:00:00:00", result.getMacAddress());
        assertEquals("Smartbridge 1", result.getName());
        assertEquals(T_BRIDGE_ADDRESS, result.getLocalAddress());
        assertEquals(contextUserId, result.getOwner());
        assertEquals("192.168.1.100", result.getBridgeAttributes().get("ipaddress"));
        assertEquals("01003542", result.getBridgeAttributes().get("swversion"));
    }
    @Test
    public void testBulbIdsFromJson() {
        System.out.println("bulbsFromJson");
        String json = "{\n" +
            "    \"1\": {\n" +
            "        \"name\": \"Bedroom\"\n" +
            "    },\n" +
            "    \"2\": {\n" +
            "        \"name\": \"Kitchen\"\n" +
            "    }\n" +
            "}";
        BulbBridgeId testBridgeId = new BulbBridgeId("testBridgeUUID");
        BulbId[] expResult = new BulbId[]{new BulbId(testBridgeId, "1"), new BulbId(testBridgeId, "2") };
        BulbId[] result = instance.bulbIdsFromPayload(json, testBridgeId);
        assertArrayEquals(expResult, result);
    }
    @Test
    public void testBulbFromJson() {
        System.out.println("bulbFromPayload");
        String json = "{\n" +
        "    \"state\": {\n" +
        "        \"hue\": 50000,\n" +
        "        \"on\": true,\n" +
        "        \"effect\": \"none\",\n" +
        "        \"alert\": \"none\",\n" +
        "        \"bri\": 200,\n" +
        "        \"sat\": 200,\n" +
        "        \"ct\": 500,\n" +
        "        \"xy\": [0.5, 0.5],\n" +
        "        \"reachable\": true,\n" +
        "        \"colormode\": \"hs\"\n" +
        "    },\n" +
        "    \"type\": \"Living Colors\",\n" +
        "    \"name\": \"LC 1\",\n" +
        "    \"modelid\": \"LC0015\",\n" +
        "    \"swversion\": \"1.0.3\",     \n" +
        "    \"pointsymbol\": {\n" +
        "        \"1\": \"none\",\n" +
        "        \"2\": \"none\",\n" +
        "        \"3\": \"none\",\n" +
        "        \"4\": \"none\",\n" +
        "        \"5\": \"none\",\n" +
        "        \"6\": \"none\",\n" +
        "        \"7\": \"none\",\n" +
        "        \"8\": \"none\"\n" +
        "    }\n" +
        "}";
        BulbBridge parentBridge = new BulbBridge(
                new BulbBridgeId("testBridgeId"), 
                "testMacAddress", 
                BulbsPlatform.PHILIPS_HUE, T_BRIDGE_ADDRESS, 
                null, null, null);
        Bulb expResult = new Bulb(
                new BulbId(parentBridge.getId(), "1"),
                BulbsPlatform.PHILIPS_HUE, "LC 1", parentBridge, 
                new BulbState(new ColorHSB(1f, 0f, 1f), true),
                true,
                new HashMap<String, Object>(){{
                    put("swversion", "1.0.3");
                    put("type", "Living Colors");
                }});
        Bulb result = instance.bulbFromPayload(json, parentBridge, new BulbId(parentBridge.getId(), "1"));
        assertEquals(expResult, result);
        assertEquals(expResult.getName(), result.getName());
        
    }
    @Test
    public void testStateFromJson() {
        System.out.println("stateFromPayload");
        String json = "{\n" +
        "        \"hue\": 65535,\n" +
        "        \"on\": true,\n" +
        "        \"effect\": \"none\",\n" +
        "        \"alert\": \"none\",\n" +
        "        \"bri\": 255,\n" +
        "        \"sat\": 0,\n" +
        "        \"ct\": 500,\n" +
        "        \"xy\": [0.5, 0.5],\n" +
        "        \"reachable\": true,\n" +
        "        \"colormode\": \"hs\"\n" +
        "    }";
        BulbState expResult = new BulbState(new ColorHSB(360f, 0f, 255f), true);
        BulbState result = instance.stateFromPayload(json);
        assertEquals(expResult, result);
    }
    @Test
    public void testBulbsPrincipalFromJson() {
        System.out.println("bulbsPrincipalFromJson");
        String json = " {\n" +
        "        \"1234567890\": {\n" +
        "            \"last use date\": \"2010-10-17T01:23:20\",\n" +
        "            \"create date\": \"2010-10-17T01:23:20\",\n" +
        "            \"name\": \"fridge\"\n" +
        "        }\n" +
        "    }";
        Set<BulbsPrincipal> expResult = new HashSet<BulbsPrincipal>(){{
            add(new BulbsPrincipal(
                "1234567890", new AppId("fridge"), "testBridgeId",  BulbsPrincipalState.REGISTERED));
        }};
        Set<BulbsPrincipal> result = instance.bulbsPrincipalsFromJson(
                json, new BulbBridgeId("testBridgeId"));
        assertEquals(expResult, result);
    }
    @Test
    public void testResponseFromJson() {
        System.out.println("responseFromJson");
        String json = "[\n" +
                "    {\"success\":{\"/lights/1/state/bri\":200}},\n" +
                "    {\"success\":{\"/lights/1/state/on\":true}},\n" +
                "]";
        HwResponse expResult = new HwResponse(
                new LinkedList<Map<String, Object>>(){{
                    add(new HashMap<String, Object>(){{ 
                        put("success", new HashMap<String, Object>(){{
                            put("/lights/1/state/bri", 200d);
                        }});
                    }});
                    add(new HashMap<String, Object>(){{ 
                        put("success", new HashMap<String, Object>(){{
                            put("/lights/1/state/on", true);
                        }});
                    }});
                }}, 
                HttpStatus.OK);
        HwResponse result = instance.responseFromJson(json, HttpStatus.OK);
        assertEquals(200d, ((Map)result.getResponseBody().get(0).get("success")).get("/lights/1/state/bri"));
        assertEquals(Boolean.TRUE, ((Map)result.getResponseBody().get(1).get("success")).get("/lights/1/state/on"));
        log.debug(result.toString());
    }
    
    ///////////////////////////////////////////////////////////////////////
    //@Test
    public void testToBridgeFromHwInterfaceCmd() {
        System.out.println("toBridgeFromHwInterfaceCmd");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbCmdTranslator_PhilipsHue instance = new BulbCmdTranslator_PhilipsHue();
        HttpCommand expResult = null;
        HttpCommand result = instance.toBridgeFromHwInterfaceCmd(address, principal);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    @Test
    public void testToCreateBulbsPrincipalCmd() {
        System.out.println("toCreateBulbsPrincipalCmd");
        
        HttpCommand expResult = new HttpCommand(
                "http://localhost:0/api", 
                HttpMethod.POST, 
                new HttpEntity<>("{\"devicetype\":\"test_deviceType#a_device\"}"),
                new HashMap<String, Object>());
        HttpCommand result = instance.toCreateBulbsPrincipalCmd(T_BRIDGE_ADDRESS, T_PRINCIPAL);
        assertEquals(expResult, result);
    }
    //@Test
    public void testToRemoveBulbsPrincipalCmd() {
        System.out.println("toRemoveBulbsPrincipalCmd");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbsPrincipal principal2Remove = null;
        BulbCmdTranslator_PhilipsHue instance = new BulbCmdTranslator_PhilipsHue();
        HttpCommand expResult = null;
        HttpCommand result = instance.toRemoveBulbsPrincipalCmd(address, principal, principal2Remove);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testToDiscoverNewBulbsCmd() {
        System.out.println("toDiscoverNewBulbsCmd");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbCmdTranslator_PhilipsHue instance = new BulbCmdTranslator_PhilipsHue();
        HttpCommand expResult = null;
        HttpCommand result = instance.toDiscoverNewBulbsCmd(address, principal);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testToModifyBridgeAttributesCmd() {
        System.out.println("toModifyBridgeAttributesCmd");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        Map<String, Object> attributes = null;
        BulbCmdTranslator_PhilipsHue instance = new BulbCmdTranslator_PhilipsHue();
        HttpCommand expResult = null;
        HttpCommand result = instance.toModifyBridgeAttributesCmd(address, principal, attributes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testToBulbsFromHwInterfaceCmd() {
        System.out.println("toBulbsFromHwInterfaceCmd");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbCmdTranslator_PhilipsHue instance = new BulbCmdTranslator_PhilipsHue();
        HttpCommand expResult = null;
        HttpCommand result = instance.toBulbsFromHwInterfaceCmd(address, principal);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testToBulbFromHwInterfaceCmd() {
        System.out.println("toBulbFromHwInterfaceCmd");
        BulbId bulbId = null;
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbCmdTranslator_PhilipsHue instance = new BulbCmdTranslator_PhilipsHue();
        HttpCommand expResult = null;
        HttpCommand result = instance.toBulbFromHwInterfaceCmd(bulbId, address, principal);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testToModifyBulbAttributesCmd() {
        System.out.println("toModifyBulbAttributesCmd");
        BulbId bulbId = null;
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        Map<String, Object> attributes = null;
        BulbCmdTranslator_PhilipsHue instance = new BulbCmdTranslator_PhilipsHue();
        HttpCommand expResult = null;
        HttpCommand result = instance.toModifyBulbAttributesCmd(bulbId, address, principal, attributes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    @Test
    public void testToApplyBulbStateCmd() {
        System.out.println("toApplyBulbStateCmd");
        String bulbId = "17";
        BulbState state = new BulbState(new ColorRGB(0, 255, 0), true);
        HttpCommand expResult = new HttpCommand(
                "http://localhost:0/api/{username}/lights/{bulbIdLocal}/state", 
                HttpMethod.PUT, 
                new HttpEntity<>("{\"transitiontime\":3,\"sat\":1,\"bri\":1,\"hue\":60,\"on\":true}"), 
                new HashMap<String, Object>(){{
                    put("username", "test_username");
                    put("bulbIdLocal", "17");
                }});
        HttpCommand result = instance.toApplyBulbStateCmd(
                new BulbId(new BulbBridgeId(""), bulbId), 
                T_BRIDGE_ADDRESS, 
                T_PRINCIPAL, 
                state);
        log.debug(result.toString());
        assertEquals(expResult, result);
    }
    @Test
    public void testToApplyBulbStateCmd__EnabledOnly() {
        System.out.println("toApplyBulbStateCmd");
        String bulbId = "17";
        BulbState state = new BulbState(true);
        HttpCommand expResult = new HttpCommand(
                "http://localhost:0/api/{username}/lights/{bulbIdLocal}/state", 
                HttpMethod.PUT, 
                new HttpEntity<>("{\"transitiontime\":3,\"on\":true}"), 
                new HashMap<String, Object>(){{
                    put("username", "test_username");
                    put("bulbIdLocal", "17");
                }});
        HttpCommand result = instance.toApplyBulbStateCmd(
                new BulbId(new BulbBridgeId(""), bulbId), 
                T_BRIDGE_ADDRESS, 
                T_PRINCIPAL, 
                state);
        log.debug(result.toString());
        assertEquals(expResult, result);
    }
}