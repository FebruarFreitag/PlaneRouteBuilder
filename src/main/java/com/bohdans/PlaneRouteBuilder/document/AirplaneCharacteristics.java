package com.bohdans.PlaneRouteBuilder.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@Document(collection = "airplanes")
public class AirplaneCharacteristics {

    @Field("max_speed")
    private double maxSpeed;
    @Field("max_acceleration")
    private double maxAcceleration;
    @Field("height_change_speed")
    private double maxHeightChangeSpeed;
    @Field("course_change_speed")
    private double maxCourseChangeSpeed;
}
