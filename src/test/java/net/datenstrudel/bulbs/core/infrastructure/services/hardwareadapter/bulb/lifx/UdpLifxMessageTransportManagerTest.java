package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx;

import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.LifxMessagePayload;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.RespGetPanGateway;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.payload.RespPowerState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeHwException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Note that this test puts some expectations against the {@link net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.lifx.UdpLifxMessageTransportManager}
 * which can only be fulfilled in asynchronous task execution mod as of the usage of an AsyncTaskExecutor. This specifically applies to
 * cases where messages from network arrive in unexpected order.
 */
@RunWith(MockitoJUnitRunner.class)
public class UdpLifxMessageTransportManagerTest {

    private static final Logger log = LoggerFactory.getLogger(UdpLifxMessageTransportManagerTest.class);
    private AsyncTaskExecutor taskExec = new SimpleAsyncTaskExecutor();
//    private TaskExecutor taskExec = new SyncTaskExecutor(); // <-- Usage is causing this test to fail!

    @InjectMocks
    private UdpLifxMessageTransportManager transport_init = new UdpLifxMessageTransportManager(taskExec);
    private UdpLifxMessageTransportManager transport;

    @Mock
    DatagramSocket udpSocket;

    @Before
    public void init() throws BulbBridgeHwException {
        transport = spy(transport_init);
        doReturn(udpSocket).when(transport).provideNewUdpSocket(isA(Integer.class));
        transport.init();
    }

    @Test
    public void sendAndReceiveAggregated_msgTypeYieldsMany_networkDeliversSameMessage() throws Exception {
        LifxMessage answer = new LifxMessage(LifxPacketType.RESP_PAN_GATEWAY, new RespGetPanGateway(1, RespGetPanGateway.Service.UDP));
        final int[] count = new int[]{1};
        doAnswer(inv -> {
            if (count[0] > 2) {
                throw new SocketTimeoutException("Intended timeout");
            }
            DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
            destPacket.setData(answer.toBytes());
            log.debug("Return mocked network package Nr. {}", count[0]++);
            return destPacket;
        }).when(udpSocket).receive(any(DatagramPacket.class));

        CompletableFuture<LifxMessage[]> res = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_PAN_GATEWAY,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_PAN_GATEWAY
        );

        assertThat(res.get().length, is(1));
        assertThat(res.get()[0], is(answer));
    }
    @Test
    public void sendAndReceiveAggregated_msgTypeYieldsMany_networkDeliversDifferentMessages() throws Exception {
        LifxMessage[] answer = new LifxMessage[]{
                new LifxMessage(LifxPacketType.RESP_PAN_GATEWAY, new RespGetPanGateway(1, RespGetPanGateway.Service.UDP)),
                new LifxMessage(LifxPacketType.RESP_PAN_GATEWAY, new RespGetPanGateway(2, RespGetPanGateway.Service.UDP))
        };
        final int[] count = new int[]{1};
        doAnswer(inv -> {
            if (count[0] > 2) {
                throw new SocketTimeoutException("Intended timeout");
            }
            DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
            destPacket.setData(answer[count[0]-1].toBytes());
            log.debug("Return mocked network package Nr. {}", count[0]++);
            return destPacket;
        }).when(udpSocket).receive(any(DatagramPacket.class));

        CompletableFuture<LifxMessage[]> res = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_PAN_GATEWAY,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_PAN_GATEWAY
        );

        assertThat(res.get().length, is(2));
        assertThat("Result contains answer[1]", Arrays.asList(res.get()).contains(answer[0]));
        assertThat("Result contains answer[1]", Arrays.asList(res.get()).contains(answer[1]));
    }
    @Test
    public void sendAndReceiveAggregated_msgTypeYieldsOne_networkDeliversDifferentMessages_firstGetsResolved() throws Exception {
        LifxMessage[] answer = new LifxMessage[]{
                new LifxMessage(LifxPacketType.RESP_POWER_STATE, new RespPowerState(RespPowerState.Power.ON)),
                new LifxMessage(LifxPacketType.RESP_POWER_STATE, new RespPowerState(RespPowerState.Power.OFF))
        };
        final int[] count = new int[]{1};
        doAnswer(inv -> {
            if (count[0] > 2) {
                throw new SocketTimeoutException("Intended timeout");
            }
            DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
            destPacket.setData(answer[count[0]-1].toBytes());
            log.debug("Return mocked network package Nr. {}", count[0]++);
            return destPacket;
        }).when(udpSocket).receive(any(DatagramPacket.class));

        CompletableFuture<LifxMessage[]> res = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_POWER_STATE,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_POWER_STATE
        );

        assertThat(res.get().length, is(1));
        assertThat("Result contains answer[1]", Arrays.asList(res.get()).contains(answer[0]));
    }
    @Test
    public void sendAndReceiveAggregated_manyAccumulated() throws Exception {
        LifxMessage answer_0 = new LifxMessage(LifxPacketType.RESP_PAN_GATEWAY, new RespGetPanGateway(1, RespGetPanGateway.Service.UDP));
        LifxMessage answer_1 = new LifxMessage(LifxPacketType.RESP_POWER_STATE, new RespPowerState(RespPowerState.Power.ON));
        final int[] countMockInv = new int[]{0};
        doAnswer(inv -> {
            countMockInv[0] += 1;
            if(countMockInv[0] < 2){ // Provided once
                DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
                destPacket.setData(answer_1.toBytes()); // Note, we return 2nd answer first!
                log.info("- [M] Serving mocked answer [{}].", answer_1.getType());
                return destPacket;
            }else if (countMockInv[0] < 4){ // Provided many times
                DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
                destPacket.setData(answer_0.toBytes());
                log.info("- [M] Serving mocked answer [{}].", answer_0.getType());
                return destPacket;
            }else{
                throw new SocketTimeoutException("Intended timeout");
            }
        }).when(udpSocket).receive(any(DatagramPacket.class));

        CompletableFuture<LifxMessage[]> res_0 = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_PAN_GATEWAY,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_PAN_GATEWAY
        );
        CompletableFuture<LifxMessage[]> res_1 = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_POWER_STATE,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_POWER_STATE
        );

        LifxMessage actualRes_1 = res_0.get()[0];
        log.info("Received actual result 0: " + actualRes_1);
        log.info("Received actual result 0 size: " + res_0.get().length);
        LifxMessage actualRes_0 = res_1.get(1800, TimeUnit.SECONDS)[0];
        log.info("Received actual result 1: " + actualRes_0);
        log.info("Received actual result 1 size: " + res_1.get().length);

        assertThat(actualRes_0, is(answer_1));

        assertThat(res_0.get().length, is(1));
        assertThat(actualRes_1, is(answer_0));
        assertThat(res_1.get().length, is(1));
    }
    @Test
    public void sendAndReceiveAggregated_manyArriveDeferredWithinTimeout() throws Exception {
        LifxMessage answer_0 = new LifxMessage(LifxPacketType.RESP_PAN_GATEWAY, new RespGetPanGateway(1, RespGetPanGateway.Service.UDP));
        LifxMessage answer_1 = new LifxMessage(LifxPacketType.RESP_POWER_STATE, new RespPowerState(RespPowerState.Power.ON));
        final int[] countMockInv = new int[]{0};
        int maxTimeoutEach_ms = UdpLifxMessageTransportManager.SOCKET_TIMEOUT / 2 - 10;
        doAnswer(inv -> {
            countMockInv[0] += 1;
            if(countMockInv[0] < 2){ // Provided once
                DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
                destPacket.setData(answer_1.toBytes());
                log.info("- [M] Serving mocked answer [{}].", answer_1.getType());
                Thread.sleep(maxTimeoutEach_ms);
                return destPacket;
            }else if (countMockInv[0] < 4){ // Provided many times
                DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
                destPacket.setData(answer_0.toBytes());
                log.info("- [M] Serving mocked answer [{}].", answer_0.getType());
                Thread.sleep(maxTimeoutEach_ms);
                return destPacket;
            }else{
                throw new SocketTimeoutException("Intended timeout");
            }
        }).when(udpSocket).receive(any(DatagramPacket.class));

        CompletableFuture<LifxMessage[]> res_0 = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_PAN_GATEWAY,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_PAN_GATEWAY
        );
        CompletableFuture<LifxMessage[]> res_1 = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_POWER_STATE,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_POWER_STATE
        );

        LifxMessage actualRes_1 = res_0.get(2, TimeUnit.SECONDS)[0];
        log.info("Received actual result 0: " + actualRes_1);
        log.info("Received actual result 0 size: " + res_0.get().length);
        LifxMessage actualRes_0 = res_1.get(2, TimeUnit.SECONDS)[0];
        log.info("Received actual result 1: " + actualRes_0);
        log.info("Received actual result 1 size: " + res_1.get().length);

        assertThat(actualRes_0, is(answer_1));
        assertThat(res_0.get().length, is(greaterThan(0)));
        assertThat(actualRes_1, is(answer_0));
        assertThat(res_1.get().length, is(1));

    }
    @Test
    public void sendAndReceiveAggregated_manyArriveDeferredOutsideTimeout() throws Exception {
//        LifxMessage answer_0 = new LifxMessage(LifxPacketType.RESP_PAN_GATEWAY, new RespGetPanGateway(1, RespGetPanGateway.Service.UDP));
        LifxMessage answer_1 = new LifxMessage(LifxPacketType.RESP_POWER_STATE, new RespPowerState(RespPowerState.Power.ON));
        final int[] countMockInv = new int[]{0};
        doAnswer(inv -> {
            countMockInv[0] += 1;
            if(countMockInv[0] < 2){ // Provided once
                DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
                log.info("- [M] Serving mocked answer [{}].", answer_1.getType());
                destPacket.setData(answer_1.toBytes()); // Note, we return 2nd answer first!
                Thread.sleep(UdpLifxMessageTransportManager.SOCKET_TIMEOUT + 1000); // Provoke transport's timeout
                return destPacket;
            }else if (countMockInv[0] < 10){ // Provided many times
                DatagramPacket destPacket = (DatagramPacket) inv.getArguments()[0];
                LifxMessage answer_0 = new LifxMessage(LifxPacketType.RESP_PAN_GATEWAY, new RespGetPanGateway(countMockInv[0], RespGetPanGateway.Service.UDP));
                destPacket.setData(answer_0.toBytes());
                log.info("- [M] Serving mocked answer [{}].", answer_0.getType());
                Thread.sleep(UdpLifxMessageTransportManager.SOCKET_TIMEOUT + 1000);
                return destPacket;
            }else{
                throw new SocketTimeoutException("Intended timeout");
            }
        }).when(udpSocket).receive(any(DatagramPacket.class));

        CompletableFuture<LifxMessage[]> res_0 = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_PAN_GATEWAY,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_PAN_GATEWAY
        );
        CompletableFuture<LifxMessage[]> res_1 = transport.sendAndReceiveAggregated(new LifxMessage<>(
                        LifxPacketType.REQ_POWER_STATE,
                        LifxMessagePayload.emptyPayload(),
                        Inet4Address.getByName("192.168.1.255"), 56700, new byte[6]
                ), LifxPacketType.RESP_POWER_STATE
        );

        boolean success_0 = false;
        boolean success_1 = false;
        try{
            res_0.get();
        }catch (ExecutionException e){
            success_0 = true;
        }
        try{
            res_1.get();
        }catch (ExecutionException e){
            success_1 = true;
        }
        assertThat(success_0, is(true));
        assertThat(success_1, is(true));

    }
}
