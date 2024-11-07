package com.bohdans.PlaneRouteBuilder.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "airplanes")
public class Airplane {

    @Id
    private String id;

    @Field("characteristics")
    private AirplaneCharacteristics characteristics;

    @Field("current_position")
    private TemporaryPoint position;

    @Field("flights")
    private List<Flight> flights;
}
