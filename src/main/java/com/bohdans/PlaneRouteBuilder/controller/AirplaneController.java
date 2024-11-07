package com.bohdans.PlaneRouteBuilder.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/airplanes")
@RequiredArgsConstructor
public class AirplaneController {

//    private final AirplaneService airplaneService;
//
//    @GetMapping(produces = "application/json")
//    public ResponseEntity<List<Airplane>> fetchAirplanesList() {
//        return new ResponseEntity<>(airplaneService.fetchAllAirplanes(), HttpStatus.OK);
//    }
//
//    @PostMapping(consumes = "application/json", produces = "application/json")
//    public ResponseEntity<AirplaneView> upsertAirplane(@RequestBody AirplaneDto dto) {
//        return new ResponseEntity<>(airplaneService.upsertAirplaneInfo(dto), HttpStatus.OK);
//    }
}