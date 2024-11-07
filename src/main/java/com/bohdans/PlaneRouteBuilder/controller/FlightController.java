package com.bohdans.PlaneRouteBuilder.controller;

import com.bohdans.PlaneRouteBuilder.document.WayPoint;
import com.bohdans.PlaneRouteBuilder.dto.PreFlightInfoDto;
import com.bohdans.PlaneRouteBuilder.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<List<PreFlightInfoDto>> startFlights(@RequestParam(name = "airplanes") List<String> airplaneIdList,
                                                               @RequestBody List<WayPoint> checkPoints){
        return ResponseEntity.ok(flightService.startFlight(checkPoints, airplaneIdList));
    }

    @PostMapping(path = "/random", produces = "application/json")
    public ResponseEntity<List<PreFlightInfoDto>> startFlights(@RequestBody List<WayPoint> checkPoints){
        return ResponseEntity.ok(flightService.startFlight(checkPoints));
    }
}