package ru.netology.sender;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationServiceImpl;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.startsWith;

@DisplayName("Тест класса MessageSenderImpl.")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageSenderImplTest {

    private static final String IP_ADDRESS_HEADER = "x-real-ip";

    @Mock
    private GeoServiceImpl geoService;

    @Mock
    private LocalizationServiceImpl localizationService;

    private MessageSenderImpl messageSender;

    @BeforeEach
    void setUp() {
        localizationService = Mockito.mock(LocalizationServiceImpl.class);
        geoService = Mockito.mock(GeoServiceImpl.class);
        messageSender = new MessageSenderImpl(geoService, localizationService);
    }

    @Test
    @Order(1)
    @DisplayName("Тест метода send Moscow IP -> Добро пожаловать")
    void sendToMoscowIP() {
        String expectedPhrase = "Добро пожаловать";
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.123.12.19");
        String ipAddress = String.valueOf(headers.get(IP_ADDRESS_HEADER));

        Mockito.when(geoService.byIp(Mockito.eq(ipAddress)))
                .thenReturn(new Location("Moscow", Country.RUSSIA, "Lenina", 15));
        Mockito.when(localizationService.locale(Mockito.eq(Country.RUSSIA)))
                .thenReturn(expectedPhrase);

        Assertions.assertEquals(expectedPhrase, messageSender.send(headers));
    }

    @Test
    @Order(2)
    @DisplayName("Тест метода send New York IP -> Welcome")
    void sendToNY_IP() {
        String expectedPhrase = "Welcome";
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.44.183.149");
        String ipAddress = String.valueOf(headers.get(IP_ADDRESS_HEADER));

        Mockito.when(geoService.byIp(Mockito.eq(ipAddress)))
                .thenReturn(new Location("New York", Country.USA, " 10th Avenue", 32));
        Mockito.when(localizationService.locale(Mockito.eq(Country.USA)))
                .thenReturn(expectedPhrase);

        Assertions.assertEquals(expectedPhrase, messageSender.send(headers));
    }

    @Order(3)
    @DisplayName("Тест метода send IP starts with 172. -> Добро пожаловать")
    @ParameterizedTest
    @ValueSource(strings = {"172.123.12.19", "172.5.0.0.0", "172.0.0.1", "172.0.0.0"})
    void sendToRussianIP(String ip) {
        String expectedPhrase = "Добро пожаловать";
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);

        Mockito.when(geoService.byIp(startsWith("172.")))
                .thenReturn(new Location("Moscow", Country.RUSSIA, null, 0));
        Mockito.when(localizationService.locale(Mockito.eq(Country.RUSSIA)))
                .thenReturn("Добро пожаловать");

        Assertions.assertEquals(expectedPhrase, messageSender.send(headers));
    }

    @Order(4)
    @DisplayName("Тест метода send IP starts with 96. -> Welcome")
    @ParameterizedTest
    @ValueSource(strings = {"96.123.12.19", "96.5.0.0.0", "96.0.0.1", "96.0.0.0"})
    void sendToUSA_IP(String ip) {
        String expectedPhrase = "Welcome";
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);

        Mockito.when(geoService.byIp(startsWith("96.")))
                .thenReturn(new Location("New York", Country.USA, null, 0));
        Mockito.when(localizationService.locale(Mockito.eq(Country.USA)))
                .thenReturn(expectedPhrase);

        Assertions.assertEquals(expectedPhrase, messageSender.send(headers));
    }



}
