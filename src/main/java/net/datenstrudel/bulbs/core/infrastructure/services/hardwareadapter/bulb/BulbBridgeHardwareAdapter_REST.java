package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.philipshue.BulbCmdTranslator_PhilipsHue;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Thomas Wendzinski
 */
@Service(value="bulbBridgeHardwareAdapter_REST")
public class BulbBridgeHardwareAdapter_REST implements BulbBridgeHardwareAdapter{
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    private static final Logger log = LoggerFactory.getLogger(BulbBridgeHardwareAdapter_REST.class);
    private RestTemplate restService;
    
    //~ Connection Params ~~~~~~~~~~~
    int CONNECTIONS_MAX = 2;
    int CONNECTIONS__ROUTE_MAX = 100;
//    int CONNECTION_TIMEOUT_MS = 40000;
//    int KEEP_ALIVE_MS = 2000;
    
    //~ Construction ///////////////////////////////////////////////////////////
    public BulbBridgeHardwareAdapter_REST() {
    
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    @PostConstruct
    public void init(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(CONNECTIONS_MAX);
        connectionManager.setDefaultMaxPerRoute(CONNECTIONS__ROUTE_MAX);
//        connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost(
//                "google.com")), 20);

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();
                
        // TODO: Apply new config!!
//        httpClient.getParams().setIntParameter(
//                CoreConnectionPNames.CONNECTION_TIMEOUT,
//                CONNECTION_TIMEOUT_MS
//        );
//        httpClient.getParams().setIntParameter(
//                CoreConnectionPNames.CONNECTION_TIMEOUT,
//                KEEP_ALIVE_MS
//        );
        
        HttpComponentsClientHttpRequestFactory reFactory = new
            HttpComponentsClientHttpRequestFactory(httpClient);
        
        restService = new RestTemplate(reFactory);
    }

    //~ BRIDGE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public BulbBridge toBridge(
            BulbBridgeAddress address,
            BulbBridgeId bridgeId,
            BulbsPrincipal principal,
            BulbsContextUserId contextUserId,
            BulbsPlatform platform) throws BulbBridgeHwException {

        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toBridgeFromHwInterfaceCmd(address, principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.bridgeFromPayload(
                resp.getBody(), bridgeId, address, contextUserId);
    }

    @Override
    public InvocationResponse discoverNewBulbs(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toDiscoverNewBulbsCmd(address, principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromHardwareInvocation(resp.getBody());
    }
    
    @Override
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException {
        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toModifyBridgeAttributesCmd(address, principal, attributes);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromJson(resp.getBody(), resp.getStatusCode());
    }

    @Override
    public Set<BulbsPrincipal> toBulbsPrincipals(
            BulbBridge bridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toToBulbsPrincipalsCmd(bridge, principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.bulbsPrincipalsFromJson(resp.getBody(), bridge.getId());
    }
    
    @Override
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address, BulbsPrincipal principal, BulbsPlatform platform) throws BulbBridgeHwException {
        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toCreateBulbsPrincipalCmd(address, principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromHardwareInvocation(resp.getBody());
    }
    @Override
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbsPrincipal principal2Remove,
            BulbsPlatform platform) throws BulbBridgeHwException {
        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toRemoveBulbsPrincipalCmd(address, principal, principal2Remove);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromJson(resp.getBody(), resp.getStatusCode());
    }
    
    //~ BULB ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    @Override
    public BulbId[] toBulbIds(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {
        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toBulbsFromHwInterfaceCmd(
                parentBridge.getLocalAddress(), principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        BulbId[] bulbIds = cmdTranslator.bulbIdsFromPayload(resp.getBody(), parentBridge.getId());
        return bulbIds;
    }
    
    @Override
    public Bulb[] toBulbs(
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {

        BulbId[] bulbIds = toBulbIds(parentBridge, principal, platform);
        if(bulbIds.length == 0)return new Bulb[0];
        Bulb[] res = new Bulb[bulbIds.length];
        for (int i = 0; i < bulbIds.length; i++) {
            res[i] = toBulb(bulbIds[i], parentBridge, principal, platform);
        }
        return res;
    }
    @Override
    public Bulb toBulb(
            BulbId bulbId,
            BulbBridge parentBridge,
            BulbsPrincipal principal,
            BulbsPlatform platform) throws BulbBridgeHwException {

        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toBulbFromHwInterfaceCmd(
                bulbId, parentBridge.getLocalAddress(), principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.bulbFromPayload(resp.getBody(), parentBridge, bulbId);
    }
    
    @Override
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            Map<String, Object> attributes,
            BulbsPlatform platform) throws BulbBridgeHwException {

        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toModifyBulbAttributesCmd(
                bulbId, address, principal, attributes);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromHardwareInvocation(resp.getBody());
    }
    
    @Override
    public void applyBulbState(
            BulbId bulbId,
            BulbBridgeAddress address,
            BulbsPrincipal principal,
            BulbState state,
            BulbsPlatform platform, BulbState previousState) throws BulbBridgeHwException {

        BulbCmdTranslator_HTTP cmdTranslator = translatorForPlatform(platform);
        HttpCommand cmd = cmdTranslator.toApplyBulbStateCmd(bulbId, address, principal, state);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        if(!resp.getStatusCode().is2xxSuccessful()) {
            throw new BulbBridgeHwException(resp.getStatusCode().value(), resp.getStatusCode().name(), resp.getBody());
        }
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private ResponseEntity<String> executeHttpCmd(
            HttpCommand cmd,
            BulbCmdTranslator_HTTP cmdTranslator)throws BulbBridgeHwException{
        if(log.isDebugEnabled()){
            log.debug("|- Invocing HTTP Cmd: " + cmd);
        }
        try{
            ResponseEntity<String> resp = restService.exchange(
                    cmd.getUrl(), 
                    cmd.getHttpMethod(), 
                    cmd.getEntity(), String.class, cmd.getUrlVariables());
            
            if(HttpStatus.OK != resp.getStatusCode()){
                throw new BulbBridgeHwException(
                        resp.getStatusCode().value(), 
                        resp.getStatusCode().getReasonPhrase(), 
                        resp.getBody());
            }
            InvocationResponse invocationResp;
            if( (invocationResp = cmdTranslator.checkResponseForError(resp.getBody())) != null){
                throw new BulbBridgeHwException(invocationResp.getMessage(), null);
            }
            return resp;
        }catch(HttpStatusCodeException bbex){
            throw new BulbBridgeHwException(
                    bbex.getStatusCode().value(), bbex.getStatusText(), bbex.getResponseBodyAsString());
        }catch(RestClientException rcex){
            throw new BulbBridgeHwException(rcex.getMessage(), rcex);
        }
    }

    private BulbCmdTranslator_HTTP translatorForPlatform(BulbsPlatform platform){
        switch(platform){
            case PHILIPS_HUE:
                return new BulbCmdTranslator_PhilipsHue();
            default:
                throw new UnsupportedOperationException("Platform not supported: " + platform);
        }
    }
}
