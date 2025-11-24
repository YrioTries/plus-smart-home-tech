package ru.yandex.practicum;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Component
public class HubRouterClient {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    public HubRouterControllerGrpc.HubRouterControllerBlockingStub getStub() {
        return hubRouterStub;
    }
}
