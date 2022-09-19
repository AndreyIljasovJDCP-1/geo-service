package ru.netology.i18n;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.entity.Country;

import java.util.stream.Stream;

@DisplayName("Тест LocalService")
@TestMethodOrder(OrderAnnotation.class)
class LocalizationServiceImplTest {
    private static LocalizationServiceImpl localizationService;

    @BeforeAll
    static void setUp() {
        localizationService = new LocalizationServiceImpl();
    }

    @Test
    @Order(1)
    @DisplayName("Тест метода public String locale. Россия.")
    void localeTest() {
        Assertions.assertEquals("Добро пожаловать",
                localizationService.locale(Country.RUSSIA));
    }

    @Order(2)
    @ParameterizedTest
    @DisplayName("Тест метода public String locale. All countries.")
    @MethodSource("getArguments")
    void localeTest1(Country country, String expected) {
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
