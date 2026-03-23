package controller;

import dto.hub.HubEvent;
import dto.sensor.SensorEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.HubService;
import service.SensorService;

@Slf4j
@RestController
@RequestMapping("/events")
@Validated
@RequiredArgsConstructor
public class EventsController {

    private final HubService hubService;
    private final SensorService sensorService;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Request /events/sensors body: {}", event.toString());
        sensorService.handle(event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Request /events/hubs body: {}", event.toString());
        hubService.handle(event);
    }
}
