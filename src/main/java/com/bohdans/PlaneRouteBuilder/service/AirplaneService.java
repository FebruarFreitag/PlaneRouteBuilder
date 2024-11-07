package com.bohdans.PlaneRouteBuilder.service;

import com.bohdans.PlaneRouteBuilder.document.Airplane;
import com.bohdans.PlaneRouteBuilder.repository.AirplaneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;

    public List<Airplane> findAnyPlanesForFlight(){
        return airplaneRepository.findAll()
                .stream()
                .limit(3)
                .toList();
    }

    public List<Airplane> findPlanesByIds(List<String> ids){
        return airplaneRepository.findAllById(ids);
    }

    public List<Airplane> upsertPlanes(List<Airplane> planes){
        return airplaneRepository.saveAll(planes);
    }

    public Airplane upsertPlane(Airplane plane){
        return airplaneRepository.save(plane);
    }
}
