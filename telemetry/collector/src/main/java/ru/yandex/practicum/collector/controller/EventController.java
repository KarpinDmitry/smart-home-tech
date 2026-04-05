package ru.yandex.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.service.HubService;
import ru.yandex.practicum.collector.service.SensorService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final SensorService sensorService;
    private final HubService hubService;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received event: {}", request.getPayloadCase());

            sensorService.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request,
                                StreamObserver<Empty> responseObserver) {

        try {
            hubService.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    new StatusRuntimeException(Status.fromThrowable(e))
            );
        }
    }
}
