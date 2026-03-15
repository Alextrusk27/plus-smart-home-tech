package ru.practicum.analyzer.controller;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Component
@Slf4j
public class HubRouterClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub client;

    public HubRouterClient(@GrpcClient("hub-router")
                           HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.client = hubRouterClient;
    }

    public void send(DeviceActionRequest request) {
        try {
            client.handleDeviceAction(request);
            log.debug("Device action sent successfully");
        } catch (Exception e) {
            log.error("Device action sent failed", e);
        }
    }
}