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
@Document(collection = "flights")
public class Flight {

    @Id
    private String id;

    @Field("checkPoints")
    private List<WayPoint> checkPoints;

    @Field("passedPoints")
    private List<TemporaryPoint> passedPoints;
}
