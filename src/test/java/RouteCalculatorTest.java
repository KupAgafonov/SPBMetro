import core.Line;
import core.Station;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;


/**     StationIndexForTest
 *                             line2              line4                         line3
 *                              |                  |                             |
 * line1 _ Аэропорт _ _ _ Динамо _ _ _  Белорусская _ _ _ Маяковская _ _ _ Тверская   _ line1
 *                        Петровский       Белорусская                    Пушкинская
 *                          парк                   \                          /
 *                              \                   \                       /
 *                               \                   \                   Барикадная
 *                                \                   \__ __ __ __ Краснопресенская _ line4
 *                               CSKA                                 /
 *                                  \                               /
 *                                   \                      Улица 1905 года
 *                                    \                         /
 *                                  Хорошевская               /
 *     line3 _ Октябрьское поле _ _ _ Полежаевская _ _ _ Беговая
 *                                       \
 *                                        \
 *                                      Шелепиха
 *                                         |
 *                                       line2
 */

public class RouteCalculatorTest {

    RouteCalculator routeCalculatorForTest;

    List<Station> route;
    StationIndex stationIndexForTest = new StationIndex();


    Line line1 = new Line(1, "Зеленая");
    Line line2 = new Line(2, "Желтая");
    Line line3 = new Line(3, "Фиолетовая");
    Line line4 = new Line(4, "Кольцевая");
    Line line5 = new Line(5, "Тестовая");


    List<Station> allTestStations = new ArrayList<Station>() {{
        add(new Station("Аэропорт", line1));
        add(new Station("Динамо", line1));
        add(new Station("Белорусская", line1));
        add(new Station("Маяковская", line1));
        add(new Station("Тверская", line1));

        add(new Station("Петровский парк", line2));
        add(new Station("ЦСКА", line2));
        add(new Station("Хорошевская", line2));
        add(new Station("Шелепиха", line2));

        add(new Station("Октябрьское поле", line3));
        add(new Station("Полежаевская", line3));
        add(new Station("Беговая", line3));
        add(new Station("Улица 1905 года", line3));
        add(new Station("Баррикадная", line3));
        add(new Station("Пушкинская", line3));

        add(new Station("Краснопресенская", line4));
        add(new Station("Белорусская", line4));

    }};

    @BeforeEach // сборка схемы перед каждым тестом
    @Before
    public void setUp() {

        //Create stationIndex
        allTestStations.forEach(station -> {
            stationIndexForTest.addStation(station);
            station.getLine().addStation(station);
            stationIndexForTest.addLine(station.getLine());
        });

        //Adding Connections
        List<List<Station>> connectedStationList = new ArrayList<List<Station>>() {{
            add(new ArrayList<Station>() {{
                add(stationIndexForTest.getStation("Динамо"));        // ? == null -> Exception
                add(stationIndexForTest.getStation("Петровский парк"));
            }});
            add(new ArrayList<Station>() {{
                add(stationIndexForTest.getStation("Полежаевская"));
                add(stationIndexForTest.getStation("Хорошевская"));
            }});
            add(new ArrayList<Station>() {{
                add(stationIndexForTest.getStation("Белорусская"));
                add(stationIndexForTest.getStation("Белорусская", 4)); // программа не запрашивает линию
            }});
            add(new ArrayList<Station>() {{
                add(stationIndexForTest.getStation("Баррикадная"));
                add(stationIndexForTest.getStation("Краснопресенская"));
            }});
            add(new ArrayList<Station>() {{
                add(stationIndexForTest.getStation("Пушкинская"));
                add(stationIndexForTest.getStation("Тверская"));
            }});
        }};
        connectedStationList.forEach(stationIndexForTest::addConnection);  // ? == null -> Exception

        //Creating RouteCalculator
        routeCalculatorForTest = new RouteCalculator(stationIndexForTest);
    }

    @Test
    public void testDurationOneConnections() {
        String stationFrom = "Полежаевская";
        String stationTo = "Маяковская";

        route = new ArrayList<>();
        route = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        double expected = 2.5 * 5 + 3.5;
        double actual = RouteCalculator
                .calculateDuration(route);
        double delta = 0;

        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void testDuration() {

        String stationFrom = "Улица 1905 года";
        String stationTo = "Аэропорт";

        route = new ArrayList<>();
        route = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        double expected = 2.5 * 4 + 3.5 * 2;
        double actual = RouteCalculator
                .calculateDuration(route);
        double delta = 0;

        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void test_distance_to_same_station() {

        String stationFrom = "Аэропорт";
        String stationTo = "Аэропорт";

        List<Station> expected = new ArrayList<>();
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_duration_to_same_station() {
        String stationFrom = "Аэропорт";
        String stationTo = "Аэропорт";
        double actual = RouteCalculator
                .calculateDuration(routeCalculatorForTest
                        .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                                stationIndexForTest.getStation(stationTo)));
        double expected = 0;
        double delta = 0;

        Assert.assertEquals(expected, actual, delta);
    }

    @Test(expected = NullPointerException.class)
    public void test_distance_to_same_station_exc() {
        Station stationFrom = null;
        String stationTo = "Аэропорт";
        routeCalculatorForTest.getShortestRoute(stationFrom,
                stationIndexForTest.getStation(stationTo));
    }

    @Test
    public void test_route_stations_next_to_each_other_on_single_line() {
        String stationFrom = "Аэропорт";
        String stationTo = "Динамо";

        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_duration_stations_next_to_each_other_on_single_line() {

        String stationFrom = "Аэропорт";
        String stationTo = "Динамо";

        double expected = 2.5;
        double actual = RouteCalculator
                .calculateDuration(routeCalculatorForTest
                        .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                                stationIndexForTest.getStation(stationTo)));
        double delta = 0;


        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void test_opposite_stations_on_single_line() {

        String stationFrom = "Аэропорт";
        String stationTo = "Тверская";

        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Динамо"));
            add(stationIndexForTest.getStation("Белорусская"));
            add(stationIndexForTest.getStation("Маяковская"));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_duration_opposite_stations_on_single_line() {

        String stationFrom = "Аэропорт";
        String stationTo = "Тверская";

        double expected = 2.5 * 4;
        double actual = RouteCalculator
                .calculateDuration(routeCalculatorForTest
                        .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                                stationIndexForTest.getStation(stationTo)));
        double delta = 0;

        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void test_opposite_stations_with_one_transfer() {

        String stationFrom = "Аэропорт";
        String stationTo = "Шелепиха";

        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Динамо"));
            add(stationIndexForTest.getStation("Петровский парк"));
            add(stationIndexForTest.getStation("ЦСКА"));
            add(stationIndexForTest.getStation("Хорошевская"));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_duration_opposite_stations_with_one_transfer() {
        String stationFrom = "Аэропорт";
        String stationTo = "Шелепиха";

        double expected = 2.5 * 4 + 3.5;
        double actual = RouteCalculator
                .calculateDuration(routeCalculatorForTest
                        .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                                stationIndexForTest.getStation(stationTo)));
        double delta = 0;

        Assert.assertEquals(expected, actual, delta);
    }

    @Test
    public void test_opposite_stations_with_two_transfers() {

        String stationFrom = "Аэропорт";
        String stationTo = "Октябрьское поле";

        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Динамо"));
            add(stationIndexForTest.getStation("Петровский парк"));
            add(stationIndexForTest.getStation("ЦСКА"));
            add(stationIndexForTest.getStation("Хорошевская"));
            add(stationIndexForTest.getStation("Полежаевская"));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_distance_from_connected_station_two() {

        String stationFrom = "Хорошевская";
        String stationTo = "Краснопресенская";
        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Полежаевская"));
            add(stationIndexForTest.getStation("Беговая"));
            add(stationIndexForTest.getStation("Улица 1905 года"));
            add(stationIndexForTest.getStation("Баррикадная"));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_distance_from_connected_station_two_connected() {

        String stationFrom = "Динамо";
        String stationTo = "Краснопресенская";
        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Белорусская", 1));
            add(stationIndexForTest.getStation("Белорусская", 4));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_distance_from_connected_station_with_two_connected() {

        String stationFrom = "Петровский парк";
        String stationTo = "Краснопресенская";
        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Динамо"));
            add(stationIndexForTest.getStation("Белорусская", 1));
            add(stationIndexForTest.getStation("Белорусская", 4));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_distance_from_connected_station_two_connected_req1() {

        String stationFrom = "Динамо";
        String stationTo = "Баррикадная";
        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Белорусская", 1));
            add(stationIndexForTest.getStation("Белорусская", 4));
            add(stationIndexForTest.getStation("Краснопресенская"));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_distance_from_connected_station_two_connected_req2() {

        String stationFrom = "Тверская";
        String stationTo = "Полежаевская";
        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Пушкинская"));
            add(stationIndexForTest.getStation("Баррикадная"));
            add(stationIndexForTest.getStation("Улица 1905 года"));
            add(stationIndexForTest.getStation("Беговая"));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_distance_from_connected_station_one_connected() {

        String stationFrom = "Хорошевская";
        String stationTo = "Баррикадная";
        List<Station> expected = new ArrayList<Station>() {{
            add(stationIndexForTest.getStation(stationFrom));
            add(stationIndexForTest.getStation("Полежаевская"));
            add(stationIndexForTest.getStation("Беговая"));
            add(stationIndexForTest.getStation("Улица 1905 года"));
            add(stationIndexForTest.getStation(stationTo));
        }};
        List<Station> actual = routeCalculatorForTest
                .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                        stationIndexForTest.getStation(stationTo));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test_duration_opposite_stations_with_two_transfers() {

        String stationFrom = "Аэропорт";
        String stationTo = "Октябрьское поле";

        double expected = 2.5 * 4 + 3.5 * 2;
        double actual = RouteCalculator
                .calculateDuration(routeCalculatorForTest
                        .getShortestRoute(stationIndexForTest.getStation(stationFrom),
                                stationIndexForTest.getStation(stationTo)));
        double delta = 0;

        Assert.assertEquals(expected, actual, delta);
    }


    @After
    public void tearDown() {
    }
}