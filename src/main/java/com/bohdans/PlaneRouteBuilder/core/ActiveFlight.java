package com.bohdans.PlaneRouteBuilder.core;

import com.bohdans.PlaneRouteBuilder.document.Flight;
import com.bohdans.PlaneRouteBuilder.document.TemporaryPoint;
import com.bohdans.PlaneRouteBuilder.document.WayPoint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Getter
public class ActiveFlight {

    private final Flight flight;
    private int passedPointsCount = 0;
    private int passedCheckPointsCount = 0;

    public Optional<WayPoint> continueFlight(TemporaryPoint currentPoint, TemporaryPoint nextPoint) {
        if (flight.getCheckPoints().size()==passedCheckPointsCount){
            return Optional.empty();
        }
        if (passedPointsCount == 0) {
            passedPointsCount++;
            var firstDestination = getCurrentDestination();
            passedCheckPointsCount++;
            return Optional.of(firstDestination);
        }
        var destination = getCurrentDestination();
        passedPointsCount++;
        return updateDestination(destination, currentPoint, nextPoint);
    }

    public WayPoint getCurrentDestination(){
        return flight.getCheckPoints().get(passedCheckPointsCount);
    }

    private Optional<WayPoint> updateDestination(WayPoint currentDestination, TemporaryPoint previousPoint, TemporaryPoint currentPoint) {
        double minLongitude = Math.min(previousPoint.getLongitude(), currentPoint.getLongitude());
        double maxLongitude = Math.max(previousPoint.getLongitude(), currentPoint.getLongitude());
        double minLatitude = Math.min(previousPoint.getLatitude(), currentPoint.getLatitude());
        double maxLatitude = Math.max(previousPoint.getLatitude(), currentPoint.getLatitude());

        boolean flightConditionsAreMet = previousPoint.getAltitude() == currentDestination.getAltitude()
                && previousPoint.getFlySpeed() == currentDestination.getFlySpeed();
        boolean waypointBetweenLastPositions =  (minLongitude <= currentDestination.getLongitude() && currentDestination.getLongitude() <= maxLongitude)
                && (minLatitude <= currentDestination.getLatitude() && currentDestination.getLatitude() <= maxLatitude);

        if (flightConditionsAreMet && waypointBetweenLastPositions){
            passedCheckPointsCount++;
            return passedCheckPointsCount < flight.getCheckPoints().size()
                    ? Optional.of(getCurrentDestination())
                    : Optional.empty();
        }
        return Optional.of(currentDestination);
    }
}
