package com.gridnine.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlightsFilterTest {
    LocalDateTime currentTime = LocalDateTime.now();

    @Nested
    class ArrivedBeforeDepartureTests {

        @Test
        void arrivalTimeEqualsDepartureTime() {
            Flight flight = FlightBuilder.createFlight(currentTime, currentTime);

            List<Flight> flights = Arrays.asList(flight);
            FlightsFilter filter = new ReturnFlightsDepartedBeforeArriveFilter();

            Assertions.assertEquals(Arrays.asList(flight), filter.filter(flights));
        }

        @Test
        void onlyRegularFlights() {
            // Перелеты с прилетами после вылета
            Flight flight1 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(2));
            Flight flight2 = FlightBuilder.createFlight(currentTime.minusDays(1), currentTime.minusHours(8),
                    currentTime.minusHours(9), currentTime.minusHours(3));
            Flight flight3 = FlightBuilder.createFlight(currentTime.plusHours(1), currentTime.plusHours(1).plusMinutes(30));

            List<Flight> regularFlights = Arrays.asList(flight1, flight2, flight3);

            FlightsFilter filter = new ReturnFlightsDepartedBeforeArriveFilter();
            Assertions.assertEquals(regularFlights, filter.filter(regularFlights));
        }

        @Test
        void onlyBadFlights() {
            // Перелеты с прилетами до вылета
            Flight flight1 = FlightBuilder.createFlight(currentTime, currentTime.minusHours(2));
            Flight flight2 = FlightBuilder.createFlight(currentTime.minusDays(1), currentTime.minusHours(8),
                    currentTime.minusHours(2), currentTime.minusHours(3));
            Flight flight3 = FlightBuilder.createFlight(currentTime.plusHours(1), currentTime.plusHours(1).minusMinutes(30));

            List<Flight> badFlights = Arrays.asList(flight1, flight2, flight3);

            FlightsFilter filter = new ReturnFlightsDepartedBeforeArriveFilter();
            Assertions.assertEquals(Collections.emptyList(), filter.filter(badFlights));
        }

        @Test
        void mixedFlights() {
            // Перелеты с прилетами после вылета
            Flight flight1 = FlightBuilder.createFlight(currentTime, currentTime.plusDays(1));
            Flight flight2 = FlightBuilder.createFlight(currentTime.minusDays(1), currentTime.minusHours(8),
                    currentTime, currentTime.plusHours(1),
                    currentTime.plusHours(2), currentTime.plusHours(4));

            // Перелеты с прилетами до вылета
            Flight flight3 = FlightBuilder.createFlight(currentTime, currentTime.minusHours(2));
            Flight flight4 = FlightBuilder.createFlight(currentTime, currentTime.minusHours(1),
                    currentTime.minusHours(4), currentTime.minusHours(3),
                    currentTime, currentTime.plusMinutes(15));

            List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3, flight4);
            FlightsFilter filter = new ReturnFlightsDepartedBeforeArriveFilter();
            Assertions.assertEquals(Arrays.asList(flight1, flight2), filter.filter(allFlights));
        }
    }

    @Nested
    class StartingInPastTests {
        @Test
        void hasBadSegmentFlights() {
            // Перелеты с плохим сегментом
            Flight flight1 = FlightBuilder.createFlight(currentTime.minusHours(1), currentTime,
                    currentTime.plusHours(1), currentTime.plusHours(2));
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.plusDays(1),
                    currentTime.minusHours(1), currentTime);

            List<Flight> badFlights = Arrays.asList(flight1, flight2);
            FlightsFilter filter = new ReturnFlightsStartingNowOrInFutureFilter();

            Assertions.assertEquals(Collections.emptyList(), filter.filter(badFlights));
        }

        @Test
        void startingNowFlights() {
            Flight flight1 = FlightBuilder.createFlight(currentTime, currentTime.minusHours(2));
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.minusHours(8),
                    currentTime.plusHours(2), currentTime.minusHours(3));

            List<Flight> regularFlights = Arrays.asList(flight1, flight2);
            FlightsFilter filter = new ReturnFlightsStartingNowOrInFutureFilter();

            Assertions.assertEquals(regularFlights, filter.filter(regularFlights));
        }

        @Test
        void startingInFutureFlights() {
            Flight flight1 = FlightBuilder.createFlight(currentTime.plusDays(1), currentTime.minusHours(2));
            Flight flight2 = FlightBuilder.createFlight(currentTime.plusDays(2), currentTime.minusHours(1));

            List<Flight> regularFlights = Arrays.asList(flight1, flight2);
            FlightsFilter filter = new ReturnFlightsStartingNowOrInFutureFilter();

            Assertions.assertEquals(regularFlights, filter.filter(regularFlights));
        }

        @Test
        void mixedFlights() {
            Flight flight1 = FlightBuilder.createFlight(currentTime.plusDays(1), currentTime.minusHours(2));
            Flight flight2 = FlightBuilder.createFlight(currentTime.minusDays(2), currentTime.minusHours(1));

            List<Flight> allFlights = Arrays.asList(flight1, flight2);
            FlightsFilter filter = new ReturnFlightsStartingNowOrInFutureFilter();

            Assertions.assertEquals(Arrays.asList(flight1), filter.filter(allFlights));
        }
    }

    @Nested
    class MoreThan2HoursOnGroundTests {
        @Test
        void precisely2HoursOnGroundTest() {
            Flight flight1 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1),
                    currentTime.plusHours(3), currentTime.plusHours(5));
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(2),
                    currentTime.plusHours(2).plusMinutes(30), currentTime.plusHours(4),
                    currentTime.plusHours(5).plusMinutes(30), currentTime.plusHours(6));
            List<Flight> allFlights = Arrays.asList(flight1, flight2);
            FlightsFilter filter = new ReturnFlightsWithLessOrEqualThan2HoursOnGround();
            Assertions.assertEquals(allFlights, filter.filter(allFlights));
        }

        @Test
        void aBitMoreThan2HoursOnGroundTest() {
            Flight flight1 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1),
                    currentTime.plusHours(3).plusMinutes(1), currentTime.plusHours(5));
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(2),
                    currentTime.plusHours(2).plusMinutes(30).plusMinutes(1), currentTime.plusHours(4),
                    currentTime.plusHours(5).plusMinutes(30), currentTime.plusHours(6));
            List<Flight> allFlights = Arrays.asList(flight1, flight2);
            FlightsFilter filter = new ReturnFlightsWithLessOrEqualThan2HoursOnGround();
            Assertions.assertEquals(Collections.emptyList(), filter.filter(allFlights));
        }

        @Test
        void aBitLessThan2HoursOnGroundTest() {
            Flight flight1 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1),
                    currentTime.plusHours(3).minusMinutes(1), currentTime.plusHours(5));
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(2),
                    currentTime.plusHours(2).plusMinutes(30).minusMinutes(1), currentTime.plusHours(4),
                    currentTime.plusHours(5).plusMinutes(30), currentTime.plusHours(6));
            List<Flight> allFlights = Arrays.asList(flight1, flight2);
            FlightsFilter filter = new ReturnFlightsWithLessOrEqualThan2HoursOnGround();
            Assertions.assertEquals(allFlights, filter.filter(allFlights));
        }
    }

    @Nested
    class FilterCombinationsTests {
        @Test
        void arrivesBeforeDepartureAndStartingInPastTest() {
            // Полет с вылетом после приземления
            Flight flight1 = FlightBuilder.createFlight(currentTime.plusHours(1), currentTime);

            // Полет со стартом в прошлом
            Flight flight2 = FlightBuilder.createFlight(currentTime.minusHours(1), currentTime);

            // Обычный полет
            Flight flight3 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1));

            List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3);
            FlightsFilter filter = new ReturnFlightsDepartedBeforeArriveFilter();
            filter.setNextFilter(new ReturnFlightsStartingNowOrInFutureFilter());

            Assertions.assertEquals(Arrays.asList(flight3), filter.filter(allFlights));
        }

        @Test
        void arrivesBeforeDepartureAndOnGroundTest() {
            // Полет с вылетом после приземления
            Flight flight1 = FlightBuilder.createFlight(currentTime.plusHours(1), currentTime);

            // Полет со временем на земле больше 2 часов
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1),
                    currentTime.plusHours(4), currentTime.plusHours(5));

            // Обычный полет
            Flight flight3 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1));

            List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3);
            FlightsFilter filter = new ReturnFlightsDepartedBeforeArriveFilter();
            filter.setNextFilter(new ReturnFlightsWithLessOrEqualThan2HoursOnGround());

            Assertions.assertEquals(Arrays.asList(flight3), filter.filter(allFlights));
        }

        @Test
        void startingInPastAndOnGroundTest() {
            // Полет со стартом в прошлом
            Flight flight1 = FlightBuilder.createFlight(currentTime.minusHours(1), currentTime);

            // Полет со временем на земле больше 2 часов
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1),
                    currentTime.plusHours(4), currentTime.plusHours(5));

            // Обычный полет
            Flight flight3 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1));

            List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3);
            FlightsFilter filter = new ReturnFlightsStartingNowOrInFutureFilter();
            filter.setNextFilter(new ReturnFlightsWithLessOrEqualThan2HoursOnGround());

            Assertions.assertEquals(Arrays.asList(flight3), filter.filter(allFlights));
        }

        @Test
        void allFiltersTest() {
            // Полет со стартом в прошлом
            Flight flight1 = FlightBuilder.createFlight(currentTime.minusHours(1), currentTime);

            // Полет со временем на земле больше 2 часов
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1),
                    currentTime.plusHours(4), currentTime.plusHours(5));

            // Полет с вылетом после приземления
            Flight flight3 = FlightBuilder.createFlight(currentTime.plusHours(1), currentTime);

            // Обычный полет
            Flight flight4 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1));

            List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3, flight4);
            FlightsFilter filter = new ReturnFlightsStartingNowOrInFutureFilter();
            filter.setNextFilter(new ReturnFlightsWithLessOrEqualThan2HoursOnGround()).setNextFilter(new ReturnFlightsDepartedBeforeArriveFilter());

            Assertions.assertEquals(Arrays.asList(flight4), filter.filter(allFlights));
        }

        @Test
        void allFiltersDifferentOrderTest() {
            // Полет со стартом в прошлом
            Flight flight1 = FlightBuilder.createFlight(currentTime.minusHours(1), currentTime);

            // Полет со временем на земле больше 2 часов
            Flight flight2 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1),
                    currentTime.plusHours(4), currentTime.plusHours(5));

            // Полет с вылетом после приземления
            Flight flight3 = FlightBuilder.createFlight(currentTime.plusHours(1), currentTime);

            // Обычный полет
            Flight flight4 = FlightBuilder.createFlight(currentTime, currentTime.plusHours(1));

            List<Flight> allFlights = Arrays.asList(flight1, flight2, flight3, flight4);
            FlightsFilter order1filter = new ReturnFlightsStartingNowOrInFutureFilter();
            FlightsFilter temporaryFilter = order1filter.setNextFilter(new ReturnFlightsWithLessOrEqualThan2HoursOnGround());
            temporaryFilter.setNextFilter(new ReturnFlightsDepartedBeforeArriveFilter());

            FlightsFilter order2filter = new ReturnFlightsStartingNowOrInFutureFilter();
            order2filter.setNextFilter(new ReturnFlightsDepartedBeforeArriveFilter())
                    .setNextFilter(new ReturnFlightsWithLessOrEqualThan2HoursOnGround());

            Assertions.assertEquals(order1filter.filter(allFlights), order2filter.filter(allFlights));
        }
    }
}