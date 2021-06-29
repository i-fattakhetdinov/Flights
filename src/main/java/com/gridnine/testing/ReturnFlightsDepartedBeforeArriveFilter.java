package com.gridnine.testing;

import java.util.List;
import java.util.stream.Collectors;

public class ReturnFlightsDepartedBeforeArriveFilter extends FlightsFilter {
    @Override
    public List<Flight> filter(List<Flight> filteringData) {
        return this.filterNext(filteringData.stream()
                .filter(x -> x.getSegments()
                        .stream()
                        .noneMatch(y -> y.getArrivalDate().isBefore(y.getDepartureDate())))
                .collect(Collectors.toList()));
    }
}