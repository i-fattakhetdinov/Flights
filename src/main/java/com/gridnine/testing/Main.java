package com.gridnine.testing;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        System.out.println("Исключены вылеты по правилу: \n" +
                "1. Вылет до текущего момента времени \n" +
                "Список оставшихся полетов:");
        FlightsFilter filter = new ReturnFlightsStartingNowOrInFutureFilter();
        filter.filter(flights).forEach(System.out::println);


        System.out.println("\nИсключены вылеты по правилу: \n" +
                "2. Имеются сегменты с датой прилёта раньше даты вылета \n" +
                "Список оставшихся полетов:");
        filter = new ReturnFlightsDepartedBeforeArriveFilter();
        filter.filter(flights).forEach(System.out::println);

        System.out.println("\nИсключены вылеты по правилу: \n" +
                "3. Общее время, проведённое на земле превышает два часа  \n" +
                "Список оставшихся полетов:");
        filter = new ReturnFlightsWithLessOrEqualThan2HoursOnGround();
        filter.filter(flights).forEach(System.out::println);

        System.out.println("1 and 2");
        filter = new ReturnFlightsStartingNowOrInFutureFilter();
        filter.setNextFilter(new ReturnFlightsDepartedBeforeArriveFilter());
        filter.filter(flights).forEach(System.out::println);

        System.out.println("All 3");
        filter = new ReturnFlightsWithLessOrEqualThan2HoursOnGround();
        filter.setNextFilter(new ReturnFlightsDepartedBeforeArriveFilter())
                .setNextFilter(new ReturnFlightsStartingNowOrInFutureFilter());
        filter.filter(flights).forEach(System.out::println);
    }
}