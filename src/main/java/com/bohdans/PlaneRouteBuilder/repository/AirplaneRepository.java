package com.bohdans.PlaneRouteBuilder.repository;

import com.bohdans.PlaneRouteBuilder.document.Airplane;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirplaneRepository extends MongoRepository<Airplane, String> {
}

