package net.datenstrudel.bulbs.core.websocket;

import net.datenstrudel.bulbs.core.main.Main;
import net.datenstrudel.bulbs.core.util.IdentityUtil;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUser;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.util.IdentityUtlConfig;
import net.datenstrudel.bulbs.core.web.config.WebConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration()
@SpringApplicationConfiguration(
        initializers = {TestConfig.class},
        classes = {
                Main.class,
                IdentityUtlConfig.class,
                SecurityConfig.class,
                BulbsCoreConfig.class,
                WebConfig.class,
                ApplicationLayerConfig.class,
                WebSocketConfig.class,
                IdentityUtlConfig.class,
        }
)
@IntegrationTest("server.port:9092")
public class WebSocketConfigIT {

    @Autowired
    IdentityUtil identityUtil;

    volatile boolean connectionSucessful = false;

    @Test
    public void coreWebsocketsRequestUpdatePossible() throws InterruptedException, URISyntaxException {
        BulbsContextUser testUser = identityUtil.createNewTestUserAndSignIn("test_credentials");
//        TestRestTemplate client = new TestRestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Auth", testUser.getApiKey());
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(httpHeaders );

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
        }, headers, new URI("http://localhost:9092/core/websockets"));

        Thread.sleep(500l);
        assertThat(connectionSucessful, is(Boolean.TRUE));
    }

}