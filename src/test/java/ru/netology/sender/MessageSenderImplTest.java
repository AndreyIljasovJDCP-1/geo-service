package ru.netology.sender;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class MessageSenderImplTest {

    private static final String IP_ADDRESS_HEADER = "x-real-ip";

    @Mock
    private GeoServiceImpl geoService;

    @Mock
    private LocalizationServiceImpl localizationService;

//    @Mock
//    private Location location;

    private MessageSenderImpl messageSender;

    @BeforeEach
    void setUp() {
        localizationService = Mockito.mock(LocalizationServiceImpl.class);
        geoService=Mockito.mock(GeoServiceImpl.class);
        //location=Mockito.mock(Location.class);
        messageSender = new MessageSenderImpl(geoService, localizationService);
    }

    @ParameterizedTest
    @EnumSource(names = {"RUSSIA","USA","BRAZIL","GERMANY"})
    void send(Country country){

        Mockito.when(localizationService.locale(country)).thenReturn("Добро пожаловать");
        Assertions.assertEquals("Добро пожаловать",localizationService.locale(country));
    }
    @Test
    void sendToMoscowIP(){

        String expectedPhrase="Добро пожаловать";

        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.123.12.19");
        String ipAddress = String.valueOf(headers.get(IP_ADDRESS_HEADER));

        Mockito.when(geoService.byIp(Mockito.eq(ipAddress)))
                .thenReturn(new Location("Moscow", Country.RUSSIA, "Lenina", 15));
        Mockito.when(localizationService.locale(Mockito.eq(Country.RUSSIA)))
                .thenReturn(expectedPhrase);
        String sentPhrase= messageSender.send(headers);
        Assertions.assertEquals(expectedPhrase,sentPhrase);
    }
    @Test
    void sendToNYIP(){

        String expectedPhrase="Welcome";

        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.44.183.149");
        String ipAddress = String.valueOf(headers.get(IP_ADDRESS_HEADER));

        Mockito.when(geoService.byIp(Mockito.eq(ipAddress)))
                .thenReturn(new Location("New York", Country.USA, " 10th Avenue", 32));
        Mockito.when(localizationService.locale(Mockito.eq(Country.USA)))
                .thenReturn(expectedPhrase);
        String sentPhrase= messageSender.send(headers);
        Assertions.assertEquals(expectedPhrase,sentPhrase);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    void sendExpectedNull(String emptyIP){
        Map<String, String> headers = new HashMap<>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, emptyIP);
        String ipAddress = String.valueOf(headers.get(IP_ADDRESS_HEADER));
        /*Mockito.when(geoService.byIp(Mockito.eq(ipAddress)))
                .thenReturn(null);*/
        Location location=geoService.byIp(ipAddress);

        Assertions.assertNull(location);
    }

}
