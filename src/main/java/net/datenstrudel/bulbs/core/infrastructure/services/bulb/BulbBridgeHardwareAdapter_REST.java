package net.datenstrudel.bulbs.core.infrastructure.services.bulb;

import net.datenstrudel.bulbs.core.domain.model.bulb.*;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.http.HttpStatus;
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
    int CONNECTION_TIMEOUT_MS = 40000;
    int KEEP_ALIVE_MS = 2000;
    
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
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        HttpCommand cmd = cmdTranslator.toBridgeFromHwInterfaceCmd(address, principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.bridgeFromJson(
                resp.getBody(), bridgeId, address, contextUserId);
    }

    @Override
    public InvocationResponse discoverNewBulbs(
            BulbBridgeAddress address, BulbsPrincipal principal, BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        HttpCommand cmd = cmdTranslator.toDiscoverNewBulbsCmd(address, principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromHardwareInvocation(resp.getBody());
    }
    
    @Override
    public HwResponse modifyBridgeAttributes(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            Map<String, Object> attributes, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        HttpCommand cmd = cmdTranslator.toModifyBridgeAttributesCmd(address, principal, attributes);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromJson(resp.getBody(), resp.getStatusCode());
    }

    @Override
    public Set<BulbsPrincipal> toBulbsPrincipals(
            BulbBridge bridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        HttpCommand cmd = cmdTranslator.toToBulbsPrincipalsCmd(bridge, principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.bulbsPrincipalsFromJson(resp.getBody(), bridge.getBridgeId());
    }
    
    @Override
    public InvocationResponse createBulbsPrincipal(
            BulbBridgeAddress address, BulbsPrincipal principal, BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        HttpCommand cmd = cmdTranslator.toCreateBulbsPrincipalCmd(address, principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromHardwareInvocation(resp.getBody());
    }
    @Override
    public HwResponse removeBulbsPrincipal(
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbsPrincipal principal2Remove, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        HttpCommand cmd = cmdTranslator.toRemoveBulbsPrincipalCmd(address, principal, principal2Remove);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromJson(resp.getBody(), resp.getStatusCode());
    }
    
    //~ BULB ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    @Override
    public BulbId[] toBulbIds(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        HttpCommand cmd = cmdTranslator.toBulbsFromHwInterfaceCmd(
                parentBridge.getLocalAddress(), principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        BulbId[] bulbIds = cmdTranslator.bulbIdsFromJson(resp.getBody(), parentBridge.getBridgeId());
        return bulbIds;
    }
    
    @Override
    public Bulb[] toBulbs(
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        
        BulbId[] bulbIds = toBulbIds(parentBridge, principal, cmdTranslator);
        if(bulbIds.length == 0)return new Bulb[0];
        Bulb[] res = new Bulb[bulbIds.length];
        for (int i = 0; i < bulbIds.length; i++) {
            res[i] = toBulb(bulbIds[i], parentBridge, principal, cmdTranslator);
        }
        return res;
    }
    @Override
    public Bulb toBulb(
            BulbId bulbId, 
            BulbBridge parentBridge, 
            BulbsPrincipal principal, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        
        HttpCommand cmd = cmdTranslator.toBulbFromHwInterfaceCmd(
                bulbId, parentBridge.getLocalAddress(), principal);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.bulbFromJson(resp.getBody(), parentBridge, bulbId);
    }
    
    @Override
    public InvocationResponse modifyBulbAttributes(
            BulbId bulbId, 
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            Map<String, Object> attributes,
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        HttpCommand cmd = cmdTranslator.toModifyBulbAttributesCmd(
                bulbId, address, principal, attributes);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        return cmdTranslator.responseFromHardwareInvocation(resp.getBody());
    }
    
    @Override
    public InvocationResponse applyBulbState(
            BulbId bulbId, 
            BulbBridgeAddress address, 
            BulbsPrincipal principal, 
            BulbState state, 
            BulbCmdTranslator cmdTranslator) throws BulbBridgeHwException {
        
        HttpCommand cmd = cmdTranslator.toApplyBulbStateCmd(bulbId, address, principal, state);
        ResponseEntity<String> resp = executeHttpCmd(cmd, cmdTranslator);
        
        return cmdTranslator.responseFromHardwareInvocation(resp.getBody());
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////
    private ResponseEntity<String> executeHttpCmd(HttpCommand cmd, BulbCmdTranslator cmdTranslator)throws BulbBridgeHwException{
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
}
