package com.bohdans.PlaneRouteBuilder.service;

import com.bohdans.PlaneRouteBuilder.core.ActiveFlight;
import com.bohdans.PlaneRouteBuilder.core.FlightCalculation;
import com.bohdans.PlaneRouteBuilder.document.Airplane;
import com.bohdans.PlaneRouteBuilder.document.Flight;
import com.bohdans.PlaneRouteBuilder.document.WayPoint;
import com.bohdans.PlaneRouteBuilder.dto.PreFlightInfoDto;
import com.bohdans.PlaneRouteBuilder.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newScheduledThreadPool;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final AirplaneService airplaneService;
    private final FlightRepository flightRepository;

    public List<PreFlightInfoDto> startFlight(List<WayPoint> checkPoints){
        return startFlightInternal(checkPoints, airplaneService.findAnyPlanesForFlight());
    }

    public List<PreFlightInfoDto> startFlight(List<WayPoint> checkPoints, List<String> airplanes){
        return startFlightInternal(checkPoints, airplaneService.findPlanesByIds(airplanes));
    }

    private List<PreFlightInfoDto> startFlightInternal(List<WayPoint> checkPoints, List<Airplane> airplanes){
        var airplanesCount = airplanes.size();
        var planesQueue = new ArrayBlockingQueue<>(airplanes.size(), true, airplanes);
        var executorService = newFixedThreadPool(airplanesCount+1);
        var scheduledExecutor = newScheduledThreadPool(airplanesCount);
        while (!planesQueue.isEmpty()){
            executorService.execute(()->{
                try {
                    var plane = planesQueue.take();
                    var flight = new Flight();
                    flight.setCheckPoints(checkPoints);
                    flight = flightRepository.save(flight);
                    var flightCalculation = new FlightCalculation(airplaneService, scheduledExecutor, plane, new ActiveFlight(flight));
                    var results = flightCalculation.calculateRoute(plane.getCharacteristics(), checkPoints);
                    flight.setPassedPoints(results);
                    plane.getFlights().add(flightRepository.save(flight));
                    airplaneService.upsertPlane(plane);
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
            });
        }
        return airplanes.stream()
                .map(plane -> new PreFlightInfoDto(plane.getId(), plane.getFlights().size(),
                        plane.getFlights().stream()
                                .reduce(0L, (a,b)->a+b.getPassedPoints().size(), Long::sum)))
                .toList();
    }
}
