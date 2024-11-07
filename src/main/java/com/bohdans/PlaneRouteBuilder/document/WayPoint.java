package com.bohdans.PlaneRouteBuilder.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@Document(collection = "check_points")
public class WayPoint {

    @Field("longitude")
    private double longitude;
    @Field("latitude")
    private double latitude;
    @Field("altitude")
    private double altitude;
    @Field("fly_speed")
    private double flySpeed;
}
