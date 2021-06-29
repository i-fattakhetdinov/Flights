package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ReturnFlightsStartingNowOrInFutureFilter extends FlightsFilter {
    @Override
    public List<Flight> filter(List<Flight> filteringData) {
        return this.filterNext(filteringData.stream()
                .filter(x -> x.getSegments()
                        .stream()
                        .noneMatch(y -> y.getDepartureDate().isBefore(LocalDateTime.now().minusSeconds(1))))
                .collect(toList()));
    }
}