package net.datenstrudel.bulbs.core.websocket;

import net.datenstrudel.bulbs.core.AbstractBulbsWebIT;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.util.IdentityUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

@DirtiesContext
public class WebSocketConfigIT extends AbstractBulbsWebIT {

    @Autowired
    IdentityUtil identityUtil;

    volatile boolean connectionSucessful = false;

    @LocalServerPort
    int port;

    @Test
    public void coreWebsocketsRequestUpdatePossible() throws InterruptedException, URISyntaxException {
        BulbsContextUser testUser = identityUtil.createNewTestUser("test_credentials");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Auth", testUser.getApiKey());
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(httpHeaders);

        List<Transport> transports = new ArrayList<>(2);
        // Due to we just provide a transport containing a StandardWebSocketClient, we make sure whether
        // 'a real' websocket can be established
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.doHandshake(new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                connectionSucessful = true;
            }
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            }
            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                fail(exception.getMessage());
            }
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

            }
            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        }, headers, new URI("http://localhost:" + port + "/core/websockets"));

        Thread.sleep(3000l);
        assertThat(connectionSucessful, is(Boolean.TRUE));
    }

}