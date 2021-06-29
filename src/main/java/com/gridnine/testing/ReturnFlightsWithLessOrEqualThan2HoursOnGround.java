package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

public class ReturnFlightsWithLessOrEqualThan2HoursOnGround extends FlightsFilter {
    @Override
    public List<Flight> filter(List<Flight> filteringData) {
        List<Flight> resultList = new LinkedList<>();
        for (Flight flight : filteringData) {
            long timeOnGround = 0;
            for (int i = 1; i < flight.getSegments().size(); i++) {
                LocalDateTime arrive = flight.getSegments().get(i - 1).getArrivalDate();
                LocalDateTime depart = flight.getSegments().get(i).getDepartureDate();
                timeOnGround += ChronoUnit.MINUTES.between(arrive, depart);
            }
            if (timeOnGround <= 2 * 60) {
                resultList.add(flight);
            }
        }
        return this.filterNext(resultList);
    }
}