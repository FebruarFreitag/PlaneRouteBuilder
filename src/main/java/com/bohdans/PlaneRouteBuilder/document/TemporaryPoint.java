package com.bohdans.PlaneRouteBuilder.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@Document(collection = "temporary_points")
public class TemporaryPoint {

    @Field("course")
    private double course;
    @Field("longitude")
    private double longitude;
    @Field("latitude")
    private double latitude;
    @Field("altitude")
    private double altitude;
    @Field("fly_speed")
    private double flySpeed;
}
