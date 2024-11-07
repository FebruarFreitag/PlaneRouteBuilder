package com.bohdans.PlaneRouteBuilder.repository;

import com.bohdans.PlaneRouteBuilder.document.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends MongoRepository<Flight, String> {
}
