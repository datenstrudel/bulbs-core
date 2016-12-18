package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.philipshue;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.datenstrudel.bulbs.core.domain.model.bulb.Bulb;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridge;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.bulb.HwResponse;
import net.datenstrudel.bulbs.core.domain.model.bulb.InvocationResponse;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbCmdTranslator_HTTP;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.HttpCommand;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.color.Color;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorHSB;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorScheme;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Thomas Wendzinski
 */
public class BulbCmdTranslator_PhilipsHue implements BulbCmdTranslator_HTTP {

    //~ Member(s) //////////////////////////////////////////////////////////////
    private final Gson gson = new Gson();
    
    private static final Map<String, Object> EMPTY_URL_VARS = new HashMap<>();
    //~ Construction ///////////////////////////////////////////////////////////
    public BulbCmdTranslator_PhilipsHue() {
    }
    //~ Method(s) //////////////////////////////////////////////////////////////
    //~ From JSON ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public BulbBridge bridgeFromPayload(
            String json,
            BulbBridgeId bridgeId,
            BulbBridgeAddress localAddress,
            BulbsContextUserId contextUserId
    ) {
        Map<String, Object> d = gson.fromJson(
                json, 
                new TypeToken<Map<String, Object>>(){}.getType());
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("swversion", d.get("swversion"));
        attributes.put("linkbutton", d.get("linkbutton"));
        attributes.put("proxyaddress", d.get("proxyaddress"));
        attributes.put("ipaddress", d.get("ipaddress"));
        attributes.put("netmask", d.get("netmask"));
        attributes.put("gateway", d.get("gateway"));
        attributes.put("dhcp", d.get("dhcp"));
        attributes.put("utc", d.get("utc"));
        
        BulbBridge res = new BulbBridge(
                bridgeId, 
                (String) d.get("mac"),
                BulbsPlatform.PHILIPS_HUE, 
                localAddress, 
                (String)d.get("name"),
                contextUserId,
                attributes
                );
        return res;
    }
    @Override
    public BulbId[] bulbIdsFromPayload(String json, BulbBridgeId bridgeId) {
        Map<String, Object> d = gson.fromJson(
                json, 
                new TypeToken<Map<String, Object>>(){}.getType());
        BulbId[] res = new BulbId[d.size()];
        int i = 0;
        for (String id : d.keySet()) {
            res[i++] = new BulbId(bridgeId, id);
        }
        return res;
    }
    @Override
    public Bulb bulbFromPayload(String json, BulbBridge parentBridge, BulbId bulbId) {
        Map<String, Object> d = gson.fromJson(
                json, 
                new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("type", d.get("type"));
        attributes.put("modelid", d.get("modelid"));
        attributes.put("swversion", d.get("swversion"));
//        attributes.put("reachable", (String)d.get("reachable"));
        
        Map<String, Object> dState = (Map<String, Object>) d.get("state");
        boolean online;
        if(dState.get("reachable") != null)
            online = (boolean) dState.get("reachable");
        else online = true;
        
        Bulb res = new Bulb(
                bulbId,
                BulbsPlatform.PHILIPS_HUE,
                (String)d.get("name"), 
                parentBridge,
                stateFromJson((Map<String, Object>)d.get("state")), 
                online,
                attributes);
        return res;
    }
    @Override
    public BulbState stateFromPayload(String json) {
        Map<String, Object> d = gson.fromJson(
                json, 
                new TypeToken<Map<String, Object>>(){}.getType());
        return stateFromJson(d);
    }
    @Override
    public Set<BulbsPrincipal> bulbsPrincipalsFromJson(String json, BulbBridgeId bridgeId) {
        Map<String, Object> mJs = gson.fromJson(json, Map.class);
        Set<BulbsPrincipal> res = new HashSet<>();
        for (String el : mJs.keySet()) {
            Map<String, String> principalObj = (Map<String, String>) mJs.get(el);
            res.add(
                new BulbsPrincipal(
                    el,
                    new AppId(principalObj.get("name")), 
                    bridgeId.getUuId(),
                    BulbsPrincipalState.REGISTERED
//                    principalObj.get("last use day"), 
//                    principalObj.get("create date"), 
                    )
            );
        }
        return res;
    }
    @Override
    @Deprecated
    public HwResponse responseFromJson(String json, HttpStatus httpStatuscode) {
        List<Map<String, Object>> d = gson.fromJson(
                json, 
                new TypeToken<List<Map<String, Object>>>(){}.getType());
        return new HwResponse(d, httpStatuscode);
    }
    @Override
    public InvocationResponse responseFromHardwareInvocation(String json){
        List<Map<String, Object>> d = gson.fromJson(
                json, 
                new TypeToken<List<Map<String, Object>>>(){}.getType());
        Map<String, Object> respObj = d.iterator().next();
        if(respObj.containsKey("success")){
            Map<String, String> successObj = (Map<String, String>) respObj.get("success");
            return new InvocationResponse(
                    "" + successObj.keySet().iterator().next() + " : " 
                    + "" + ((Object)successObj.values().iterator().next()), 
                    false );
        }else if(respObj.containsKey("error")){
            return errorMessageJsonToResponse(d);
        }
        throw new IllegalStateException("JSON Response from hardware not supported by BulbCmdTranslator: " + json);
    }
    @Override
    public InvocationResponse checkResponseForError(String json){
        try{
            List<Map<String, Object>> d = gson.fromJson(
                    json, 
                    new TypeToken<List<Map<String, Object>>>(){}.getType());
            if(d.isEmpty())return null;
            Map<String, Object> potErrObj = d.get(0);
            if(potErrObj.containsKey("error"))return errorMessageJsonToResponse(d);
            return null;
        }catch(JsonParseException jpex){
            return null;
        }
    }
    public InvocationResponse errorMessageJsonToResponse(List<Map<String, Object>> data){
        Map<String, String> errObj = (Map<String, String>) data.get(0).get("error");
        return new InvocationResponse(errObj.get("description"), true);
    }

    private BulbState stateFromJson(Map<String, Object> d) {
        String colormode = (String)d.get("colormode");
        Color color;
        switch(colormode){
            case "hs":
                color = new ColorHSB(                     // HUE specific scales
                        ((Number) d.get("hue")).floatValue()/ (65535 / 360f), ((Number) d.get("sat")).floatValue(), ((Number) d.get("bri")).floatValue()
                );
                break;
            case "ct": // TODO: Create correct Color type
            case "xy": // TODO: Create correct Color type
            default:
                color = new ColorHSB(                     // HUE specific scales
                        ((Number) d.get("hue")).floatValue()/ (65535 / 360f), ((Number) d.get("sat")).floatValue(), ((Number) d.get("bri")).floatValue()
                );
        }
        BulbState res = new BulbState(
                color, 
                (Boolean)d.get("on"));
        return res;
    }
    
    //~ To Commands ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public HttpCommand toBridgeFromHwInterfaceCmd(
            BulbBridgeAddress address, final BulbsPrincipal principal) {
        String url = address.toHttpAddress() + "api/{username}/config";
        return new HttpCommand(url, HttpMethod.GET, HttpEntity.EMPTY, 
                new HashMap<String, Object>(1){{
                    put("username", principal.getUsername());
                }} );
    }
    @Override
    public HttpCommand toToBulbsPrincipalsCmd(
            BulbBridge bridge, final BulbsPrincipal principal) {
        String url = bridge.getLocalAddress() + "api/{username}/config";
        return new HttpCommand(url, HttpMethod.GET, HttpEntity.EMPTY, 
                new HashMap<String, Object>(1){{
                    put("username", principal.getUsername());
                }} );
    }
    
    @Override
    public HttpCommand toCreateBulbsPrincipalCmd(
            BulbBridgeAddress address, final BulbsPrincipal principal) {
        String url = address.toHttpAddress() + "api";
        return new HttpCommand(
                url, 
                HttpMethod.POST, 
                new HttpEntity<>( gson.toJson( 
                    new HashMap<String, Object>(2){{
                            put("username",   principal.getUsername());
                            put("devicetype", principal.getAppId().getUniqueAppName());
                    }}, 
                    new TypeToken<Map<String, Object>>(){}.getType())), 
                EMPTY_URL_VARS );
    }
    @Override
    public HttpCommand toRemoveBulbsPrincipalCmd(
            BulbBridgeAddress address, 
            final BulbsPrincipal principal, 
            final BulbsPrincipal principal2Remove) {
        String url = address.toHttpAddress() + "api/{username}/config/whitelist/{username2Del}";
        return new HttpCommand(
                url, 
                HttpMethod.DELETE, 
                HttpEntity.EMPTY,
                new HashMap<String, Object>(2){{
                        put("username",   principal.getUsername());
                        put("username2Del",   principal2Remove.getUsername());
                }}
        );
    }
    @Override
    public HttpCommand toDiscoverNewBulbsCmd(
            final BulbBridgeAddress address, 
            final BulbsPrincipal principal) {
        String url = address.toHttpAddress() + "api/{username}/lights";
        return new HttpCommand(
                url, 
                HttpMethod.POST, 
                HttpEntity.EMPTY,
                new HashMap<String, Object>(1){{
                        put("username", principal.getUsername());
                }}
        );
    }
    @Override
    public HttpCommand toModifyBridgeAttributesCmd(
            final BulbBridgeAddress address, 
            final BulbsPrincipal principal, 
            final Map<String, Object> attributes) {
        String url = address.toHttpAddress() + "api/{username}/config";
        return new HttpCommand(
                url, 
                HttpMethod.PUT, 
                new HttpEntity<>(gson.toJson( 
                    attributes, 
                    new TypeToken<Map<String, Object>>(){}.getType())
                ), 
                new HashMap<String, Object>(1){{
                    put("username", principal.getUsername());
                }});
    }
    @Override
    public HttpCommand toBulbsFromHwInterfaceCmd(
            BulbBridgeAddress address, final BulbsPrincipal principal) {
        String url = address.toHttpAddress() + "api/{username}/lights";
        return new HttpCommand(
                url, 
                HttpMethod.GET, 
                HttpEntity.EMPTY, 
                new HashMap<String, Object>(1){{
                    put("username", principal.getUsername());
                }});
    }
    @Override
    public HttpCommand toBulbFromHwInterfaceCmd(
            final BulbId bulbId, 
            final BulbBridgeAddress address, 
            final BulbsPrincipal principal) {
        String url = address.toHttpAddress() + "api/{username}/lights/{bulbId}";
        return new HttpCommand(
                url, 
                HttpMethod.GET, 
                HttpEntity.EMPTY, 
                new HashMap<String, Object>(2){{
                    put("username", principal.getUsername());
                    put("bulbId", bulbId.getLocalId());
                }});
    }
    @Override
    public HttpCommand toModifyBulbAttributesCmd(
            final BulbId bulbId, 
            final BulbBridgeAddress address, 
            final BulbsPrincipal principal, 
            final Map<String, Object> attributes) {
        String url = address.toHttpAddress() + "api/{username}/lights/{bulbId}";
        return new HttpCommand(
                url, 
                HttpMethod.PUT, 
                new HttpEntity<>( gson.toJson(
                    attributes, 
                    new TypeToken<Map<String, Object>>(){}.getType() 
                )), 
                new HashMap<String, Object>(1){{
                    put("username", principal.getUsername());
                    put("bulbId", bulbId.getLocalId());
                }});
    }
    @Override
    public HttpCommand toApplyBulbStateCmd(
            final BulbId bulbId, 
            final BulbBridgeAddress address, 
            final BulbsPrincipal principal, 
            final BulbState state) {
        String url = address.toHttpAddress() + "api/{username}/lights/{bulbIdLocal}/state";
        final Map<String, Object> stateAttributes = new HashMap<String, Object>(){{
            
            put("on", state.getEnabled() );
            put("transitiontime", 3 );
            if(state.getColor() != null){
                switch(state.getColor().colorScheme()){
                    case RGB:
                        ColorHSB hsbEm = ColorScheme.RGBtoHSB((ColorRGB) state.getColor());
                        //                                           HUE specific scale
                        put("bri", (int)(hsbEm.getBrightness()  ) ); 
                        put("hue", (int)(hsbEm.getHue()         * (65535 / 360f) ));
                        put("sat", (int)(hsbEm.getSaturation()  ));
                        break;
                    case HSB:
                        ColorHSB hsb = (ColorHSB) state.getColor();
                        //                                         HUE specific scale
                        put("bri", (int)(hsb.getBrightness()  )); 
                        put("hue", (int)(hsb.getHue()         * (65535 / 360f) ) );
                        put("sat", (int)(hsb.getSaturation()  ));
                        break;
                    case TEMP:

                    default:
                        throw new UnsupportedOperationException(
                                "ColorScheme[" + state.getColor().colorScheme() 
                                        + "] not supported by platform[" + BulbsPlatform.PHILIPS_HUE + "]");
                }
            }
        }};
        
        return new HttpCommand(
                url, 
                HttpMethod.PUT, 
                new HttpEntity<>( gson.toJson(
                    stateAttributes, 
                    new TypeToken<Map<String, Object>>(){}.getType() 
                )), 
                new HashMap<String, Object>(1){{
                    put("username", principal.getUsername());
                    put("bulbIdLocal", bulbId.getLocalId());
                }});
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
}
