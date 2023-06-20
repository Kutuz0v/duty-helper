package scpc.dutyhelper.arbor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scpc.dutyhelper.arbor.model.ArborAlert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Profile("dev")
@RequestMapping("/arborws/alerts")
@RequiredArgsConstructor
public class ArborMockController {

    @GetMapping()
    public ResponseEntity<?> status() {
        List<ArborAlert> alerts = List.of(
                ArborAlert.builder()
                        .id(555L)
                        .direction("Incoming")
                        .start(LocalDateTime.of(
                                        LocalDate.of(2022, 2, 24),
                                        LocalTime.of(4, 0))
                                .minusHours(3))
                        .ongoing(true)
                        .stop(null)
                        .resource(ArborAlert.ArborResource.builder()
                                .cidr("123.123.123.123")
                                .managedObjects(new ArborAlert.ArborResource.ManagedObject[]{
                                        ArborAlert.ArborResource.ManagedObject.builder()
                                                .name("Ukraine")
                                                .build()})
                                .build())
                        .maxImpactBps(999156789000L)
                        .build(),

                ArborAlert.builder()
                        .id(444L)
                        .direction("Outgoing")
                        .start(LocalDateTime.of(
                                        LocalDate.of(2021, 1, 23),
                                        LocalTime.of(3, 55))
                                .minusHours(3))
                        .ongoing(false)
                        .stop(LocalDateTime.of(
                                        LocalDate.of(2021, 1, 23),
                                        LocalTime.of(4, 50))
                                .minusHours(3))
                        .resource(ArborAlert.ArborResource.builder()
                                .cidr("124.124.124.124")
                                .managedObjects(new ArborAlert.ArborResource.ManagedObject[]{
                                        ArborAlert.ArborResource.ManagedObject.builder()
                                                .name("Ukraine")
                                                .build()})
                                .build())
                        .maxImpactBps(432156789000L)
                        .build(),

                ArborAlert.builder()
                        .id(333L)
                        .direction("Incoming")
                        .start(LocalDateTime.of(
                                        LocalDate.of(2021, 9, 20),
                                        LocalTime.of(13, 0))
                                .minusHours(3))
                        .ongoing(false)
                        .stop(LocalDateTime.of(
                                        LocalDate.of(2021, 9, 20),
                                        LocalTime.of(22, 13))
                                .minusHours(3))
                        .resource(ArborAlert.ArborResource.builder()
                                .cidr("125.125.125.125")
                                .managedObjects(new ArborAlert.ArborResource.ManagedObject[]{
                                        ArborAlert.ArborResource.ManagedObject.builder()
                                                .name("Test")
                                                .build()})
                                .build())
                        .maxImpactBps(123456789000L)
                        .build(),

                ArborAlert.builder()
                        .id(222L)
                        .direction("Incoming")
                        .start(LocalDateTime.of(
                                        LocalDate.of(2020, 2, 10),
                                        LocalTime.of(13, 0))
                                .minusHours(3)
                        )
                        .ongoing(false)
                        .stop(LocalDateTime.of(
                                        LocalDate.of(2021, 9, 20),
                                        LocalTime.of(22, 13))
                                .minusHours(3))
                        .resource(ArborAlert.ArborResource.builder()
                                .cidr("126.126.126.126")
                                .managedObjects(new ArborAlert.ArborResource.ManagedObject[]{
                                        ArborAlert.ArborResource.ManagedObject.builder()
                                                .name("Test")
                                                .build()})
                                .build())
                        .maxImpactBps(123456789000L)
                        .build(),

                ArborAlert.builder()
                        .id(111L)
                        .direction("Incoming")
                        .start(LocalDateTime.of(
                                        LocalDate.of(2021, 3, 11),
                                        LocalTime.of(14, 15))
                                .minusHours(3)
                        )
                        .ongoing(false)
                        .stop(LocalDateTime.of(
                                        LocalDate.of(2021, 4, 20),
                                        LocalTime.of(22, 15))
                                .minusHours(3))
                        .resource(ArborAlert.ArborResource.builder()
                                .cidr("127.127.127.127")
                                .managedObjects(new ArborAlert.ArborResource.ManagedObject[]{
                                        ArborAlert.ArborResource.ManagedObject.builder()
                                                .name("Test low impact")
                                                .build()})
                                .build())
                        .maxImpactBps(123456789L)
                        .build()

        );
        return ResponseEntity.ok(alerts);
    }
}
