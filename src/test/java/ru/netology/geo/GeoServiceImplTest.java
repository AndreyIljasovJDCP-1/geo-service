package ru.netology.geo;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.sender.MessageSenderImpl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
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
    @DisplayName("Тест метода Location byIp IP = 127.0.0.1 -> LOCALHOST")
    void send_LocalHost_IP_LOCALHOST() {
        Location location = new Location(null, null, null, 0);
        int comparing = myComparator.compare(location, geoService.byIp("127.0.0.1"));
        Assertions.assertEquals(0, comparing);
    }

    @Test
    @Order(2)
    @DisplayName("Тест метода Location byIp IP = 172.0.32.11 -> MOSCOW")
    void send_Moscow_IP_MOSCOW() {
        Location location = new Location("Moscow", Country.RUSSIA, "Lenina", 15);
        int comparing = myComparator.compare(location, geoService.byIp("172.0.32.11"));
        Assertions.assertEquals(0, comparing);
    }

    @Test
    @Order(3)
    @DisplayName("Тест метода Location byIp IP = 96.44.183.149 -> NEW YORK")
    void send_NY_IP_NEW_YORK() {
        Location location = new Location("New York", Country.USA, " 10th Avenue", 32);
        int comparing = myComparator.compare(location, geoService.byIp("96.44.183.149"));
        Assertions.assertEquals(0, comparing);
    }

    @Test
    @Order(4)
    @DisplayName("Тест метода Location byIp IP starts with 172. -> MOSCOW_RUSSIA")
    void send_172_IP_MOSCOW_RUSSIA() {
        Location location = new Location("Moscow", Country.RUSSIA, null, 0);
        int comparing = myComparator.compare(location, geoService.byIp("172.0.0.0"));
        Assertions.assertEquals(0, comparing);
    }

    @Test
    @Order(5)
    @DisplayName("Тест метода Location byIp IP starts with 96. -> NEW_YORK_USA")
    void send_96_IP_NY_USA() {
        Location location = new Location("New York", Country.USA, null, 0);
        int comparing = myComparator.compare(location, geoService.byIp("96.0.00.00"));
        Assertions.assertEquals(0, comparing);
    }

    @Order(6)
    @ParameterizedTest
    @DisplayName("Тест метода Location byIp Wrong IP -> null")
    @ValueSource(strings = {"127.0.0.0", "95.5.0.0.0", "12.7.0.0.0", "17.2.0.0.0", "9 6.2.0.0.0"})
    void byIP_WrongIP_ReturnNull(String ip) {
        Assertions.assertNull(geoService.byIp(ip));
    }

    @Order(7)
    @DisplayName("Тест метода Location byIp Empty or null IP -> null")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    void sendEmptyIPReturnNull(String emptyIP) {
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, emptyIP);
        String ipAddress = String.valueOf(headers.get(MessageSenderImpl.IP_ADDRESS_HEADER));
        Assertions.assertNull(geoService.byIp(ipAddress), "null");
    }
    @Order(8)
    @DisplayName("Тест метода byCoordinates not implemented")
    @ParameterizedTest
    @CsvSource(value = {"1.0:100.0", "0.0:0.1", "0:0"}, delimiter = ':')
    void locationByCoordinatesThrowRuntimeException(double latitude, double longitude) {
        Throwable exception = Assertions.assertThrows(RuntimeException.class,
                () -> geoService.byCoordinates(latitude, longitude), "not_implemented");
        Assertions.assertEquals("Not implemented", exception.getMessage());
    }
}

