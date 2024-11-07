package com.bohdans.PlaneRouteBuilder.core;

import com.bohdans.PlaneRouteBuilder.document.Airplane;
import com.bohdans.PlaneRouteBuilder.document.AirplaneCharacteristics;
import com.bohdans.PlaneRouteBuilder.document.TemporaryPoint;
import com.bohdans.PlaneRouteBuilder.document.WayPoint;
import com.bohdans.PlaneRouteBuilder.service.AirplaneService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
@Slf4j
public class FlightCalculation {

    private final AirplaneService airplaneService;
    private final ScheduledExecutorService executorService;
    private final AtomicBoolean flightContinues = new AtomicBoolean(true);
    private Airplane airplane;
    private ActiveFlight activeFlight;

    public List<TemporaryPoint> calculateRoute(AirplaneCharacteristics characteristics, List<WayPoint> wayPoints) throws ExecutionException, InterruptedException {
        log.info(String.format("Starting flight for the plane %s", airplane.getId()));

        List<TemporaryPoint> route = new LinkedList<>();

        var future = executorService.scheduleAtFixedRate(()->{
            if (flightContinues.get()) {
                var currentDestination = activeFlight.getCurrentDestination();
                var currentPoint = Optional.ofNullable(airplane.getPosition()).orElseGet(
                        () -> createInitialPoint(currentDestination, airplane.getCharacteristics().getMaxAcceleration()));
                var next = calculateNextPoint(currentPoint, currentDestination, airplane.getCharacteristics());
                var updatedDestination = activeFlight.continueFlight(currentPoint, next);
                log.info(String.format("Airplane %s moved to new position (long %s, lat %s, course %s, speed %s)",
                        airplane.getId(), next.getLongitude(), next.getLatitude(), next.getCourse(), next.getFlySpeed()));
                route.add(next);
                flightContinues.compareAndSet(updatedDestination.isEmpty(), false);
                airplane.setPosition(next);
                airplaneService.upsertPlane(airplane);
            }
        }, 0, 1, TimeUnit.SECONDS);

        while (flightContinues.get()){
            Thread.sleep(1000);
        }

        future.cancel(true);

        log.info(String.format("Ending flight for the plane %s after %s points visited", airplane.getId(), route.size()));
        return List.copyOf(route);
    }

    private TemporaryPoint createInitialPoint(WayPoint wayPoint, double maxAcceleration) {
        double initialSpeed = wayPoint.getFlySpeed() > 0 ? wayPoint.getFlySpeed() : maxAcceleration;
        return new TemporaryPoint(0F, wayPoint.getLongitude(), wayPoint.getLatitude(), wayPoint.getAltitude(), initialSpeed);
    }

    public TemporaryPoint calculateNextPoint(TemporaryPoint currentPoint, WayPoint targetWayPoint, AirplaneCharacteristics characteristics) {
        double targetCourse = calculateAngle(currentPoint, targetWayPoint);
        double newCourse = getNewCourse(currentPoint, characteristics, targetCourse);

        double heightDiff = targetWayPoint.getAltitude() - currentPoint.getAltitude();
        double newHeight;
        if (Math.abs(heightDiff) > characteristics.getMaxHeightChangeSpeed()) {
            newHeight = currentPoint.getAltitude() + Math.signum(heightDiff) * characteristics.getMaxHeightChangeSpeed();
        } else {
            newHeight = targetWayPoint.getAltitude();
        }

        double distanceLeft = calculateDistanceBetweenPointsWithHypot(currentPoint.getLongitude(), currentPoint.getLatitude(),
                targetWayPoint.getLongitude(), targetWayPoint.getLatitude());
        double timeToSlow = 1+(characteristics.getMaxSpeed()-targetWayPoint.getFlySpeed())/characteristics.getMaxAcceleration();
        boolean canFlyMaxSpeed = distanceLeft/characteristics.getMaxSpeed()>timeToSlow;
        double targetSpeed = canFlyMaxSpeed ? characteristics.getMaxSpeed() : targetWayPoint.getFlySpeed();
        double newSpeed = getNewSpeed(currentPoint.getFlySpeed(), targetSpeed, characteristics);

        Point newCoordinates = calculateNewCoordinates(
                currentPoint.getLatitude(), currentPoint.getLongitude(), newCourse, newSpeed
        );
        double nextLatitude = newCoordinates.getY();
        double nextLongitude = newCoordinates.getX();

        return new TemporaryPoint(newCourse, nextLongitude, nextLatitude, newHeight, newSpeed);
    }

    private double calculateDistanceBetweenPointsWithHypot(
            double x1,
            double y1,
            double x2,
            double y2) {

        double ac = Math.abs(y2 - y1);
        double cb = Math.abs(x2 - x1);

        return Math.hypot(ac, cb);
    }

    private double getNewCourse(TemporaryPoint currentPoint, AirplaneCharacteristics characteristics, double targetCourse) {
        double currentCourse = currentPoint.getCourse();
        double courseDiff = targetCourse - currentCourse;
        if (Math.abs(courseDiff)>180){
            courseDiff = Math.signum(courseDiff*-1)*(360 - Math.abs(courseDiff));
        }
        double newCourse;
        if (Math.abs(courseDiff) > characteristics.getMaxCourseChangeSpeed()) {
            newCourse = currentCourse + Math.signum(courseDiff) * characteristics.getMaxCourseChangeSpeed();
            if (newCourse>=360){
                newCourse-=360;
            }
        } else {
            newCourse = targetCourse;
        }
        return newCourse;
    }

    private double getNewSpeed(double currentSpeed, double targetSpeed, AirplaneCharacteristics characteristics){
        double speedDiff = targetSpeed - currentSpeed;
        double newSpeed;
        if (Math.abs(speedDiff) > characteristics.getMaxAcceleration()) {
            newSpeed = currentSpeed + Math.signum(speedDiff) * characteristics.getMaxAcceleration();
        } else {
            newSpeed = targetSpeed;
        }
        return newSpeed;
    }

    private double calculateAngle(TemporaryPoint from, WayPoint to) {
        double deltaX = to.getLongitude() - from.getLongitude();
        double deltaY = to.getLatitude() - from.getLatitude();
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    private Point calculateNewCoordinates(double latitude, double longitude, double course, double speed) {
        double courseRad = Math.toRadians(course);
        double deltaX = speed * Math.cos(courseRad);
        double deltaY = speed * Math.sin(courseRad);

        double nextLatitude = latitude + deltaY;
        double nextLongitude = longitude + deltaX;

        return new Point(nextLongitude, nextLatitude);
    }
}
