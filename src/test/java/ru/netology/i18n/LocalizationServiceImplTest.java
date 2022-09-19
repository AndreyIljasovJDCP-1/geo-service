package ru.netology.i18n;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.entity.Country;

import java.util.stream.Stream;

@DisplayName("Тест класса LocalService")
@TestMethodOrder(OrderAnnotation.class)
class LocalizationServiceImplTest {
    private static LocalizationServiceImpl localizationService;

    @BeforeAll
    static void setUp() {
        localizationService = new LocalizationServiceImpl();
    }

    @Test
    @Order(1)
    @DisplayName("Тест метода locale Россия -> Добро пожаловать")
    void localeReturnRightMessageRussia() {
        Assertions.assertEquals("Добро пожаловать",
                localizationService.locale(Country.RUSSIA));
    }

    @Order(2)
    @ParameterizedTest
    @DisplayName("Тест метода locale. All countries")
    @MethodSource("getArguments")
    void localeReturnRightMessageEachCountry(Country country, String expected) {
        Assertions.assertEquals(expected, localizationService.locale(country));
    }

    private static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(Country.RUSSIA, "Добро пожаловать"),
                Arguments.of(Country.USA, "Welcome"),
                Arguments.of(Country.BRAZIL, "Welcome"),
                Arguments.of(Country.GERMANY, "Welcome")
        );
    }
}
