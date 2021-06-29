package com.gridnine.testing;

import java.util.List;

public abstract class FlightsFilter {
    private FlightsFilter next = null;

    public FlightsFilter setNextFilter(FlightsFilter next) {
        this.next = next;
        return next;
    }

    public abstract List<Flight> filter(List<Flight> filteringData);

    protected List<Flight> filterNext(List<Flight> filteringData) {
        if (next == null) {
            return filteringData;
        }
        return next.filter(filteringData);
    }
}