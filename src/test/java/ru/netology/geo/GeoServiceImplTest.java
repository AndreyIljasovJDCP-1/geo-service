package ru.netology.geo;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;

import java.util.Comparator;
import java.util.Objects;


@DisplayName("Тест класса GeoServiceImpl")
@TestMethodOrder(OrderAnnotation.class)
public class GeoServiceImplTest {

    private static GeoServiceImpl geoService;
    private final Comparator<Location> myComparator = (o1, o2) ->
            Objects.equals(o1.getCity(), o2.getCity())
                    && Objects.equals(o1.getCountry(), o2.getCountry())
                    && Objects.equals(o1.getStreet(), o2.getStreet())
                    && (o1.getBuiling() == o2.getBuiling())
                    ? 0 : -1;

    @BeforeAll
    static void setUp() {
        geoService = new GeoServiceImpl();
    }

    @Test
    @Order(1)
    @DisplayName("Тест метода Location byIp. LOCALHOST = 127.0.0.1")
    void test_LocalHost_IP_Location() {
        Location location = new Location(null, null, null, 0);
        int comparing = myComparator.compare(location, geoService.byIp("127.0.0.1"));
        Assertions.assertEquals(0, comparing);
    }

    @Test
    @Order(2)
    @DisplayName("Тест метода Location byIp. MOSCOW_IP = 172.0.32.11")
    void test_Moscow_IP_Location() {
        Location location = new Location("Moscow", Country.RUSSIA, "Lenina", 15);
        int comparing = myComparator.compare(location, geoService.byIp("172.0.32.11"));
        Assertions.assertEquals(0, comparing);
    }

    @Test
    @Order(3)
    @DisplayName("Тест метода Location byIp. NEW_YORK_IP = 96.44.183.149")
    void test_NY_IP_Location() {
        Location location = new Location("New York", Country.USA, " 10th Avenue", 32);
        int comparing = myComparator.compare(location, geoService.byIp("96.44.183.149"));
        Assertions.assertEquals(0, comparing);
    }

    @Test
    @Order(4)
    @DisplayName("Тест метода Location byIp. MOSCOW_IP starts with 172.*")
    void test_172_IP_Location() {
        Location location = new Location("Moscow", Country.RUSSIA, null, 0);
        int comparing = myComparator.compare(location, geoService.byIp("172.0.0.0"));
        Assertions.assertEquals(0, comparing);
    }

    @Test
    @Order(5)
    @DisplayName("Тест метода Location byIp. NEW_YORK_IP starts with 96.*")
    void test_96_IP_Location() {
        Location location = new Location("New York", Country.USA, null, 0);
        int comparing = myComparator.compare(location, geoService.byIp("96.0.00.00"));
        Assertions.assertEquals(0, comparing);
    }

    @Order(6)
    @ParameterizedTest
    @DisplayName("Тест метода Location byIp. Wrong IP -> return null.")
    @ValueSource(strings = {"127.0.0.0", "95.5.0.0.0", "12.7.0.0.0", "17.2.0.0.0", "9 6.2.0.0.0"})
    void testNullExpected(String ip) {
        Assertions.assertNull(geoService.byIp(ip));
    }
}

