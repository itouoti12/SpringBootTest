package com.example.demo.infra.repository;

import com.example.demo.biz.domain.City;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CityMapperTests {

    @Autowired
    CityMapper target;

    @Autowired
    DataSource dataSource;


    private final Operation RESET_DATA = Operations.deleteAllFrom("city");
    private final Operation INSERT_DATA = Operations.insertInto("city")
            .columns("city_id", "city", "country_id", "last_update")
            .values("1", "Tokyo", "1", "2012-01-01 12:12:00")
            .values("2", "Osaka", "1", "2013-04-01 00:00:00")
            .values("3", "Kyoto", "1", "2012-03-02 00:12:00")
            .build();
    private final Operation ALTER_DISABLE = Operations.sequenceOf(
            Operations.sql("ALTER TABLE city SET REFERENTIAL_INTEGRITY FALSE")
    );
    private final Operation ALTER_ENABLE = Operations.sequenceOf(
            Operations.sql("ALTER TABLE city SET REFERENTIAL_INTEGRITY TRUE")
    );

    private void dbSetup(Operation operation) {
        Destination destination = new DataSourceDestination(dataSource);
        DbSetup dbSetup = new DbSetup(destination, operation);
        dbSetup.launch();
    }

    @BeforeEach
    void setUp() {
        dbSetup(Operations.sequenceOf(
                ALTER_DISABLE,
                RESET_DATA,
                INSERT_DATA,
                ALTER_ENABLE
        ));
    }

    @AfterEach
    void tearDown() {
        dbSetup(Operations.sequenceOf(
                ALTER_DISABLE,
                RESET_DATA,
                ALTER_ENABLE
        ));
    }

    @DisplayName("単発のテスト実装")
    @Nested
    class NormalTestPattern {

        @DisplayName("1件検証")
        @Test
        void test() {

            //GIVEN
            int cityId = 1;

            //WHEN
            City actual = target.findCity(cityId);

            //THEN
            assertThat(actual.getCity()).isEqualTo("Tokyo");
            assertThat(actual.getCountryId()).isEqualTo(1);
            assertThat(actual.getLastUpdate()).isEqualTo(LocalDateTime.of(2012, 1, 1, 12, 12));
        }
    }

    @DisplayName("DataPatterを使用したテスト実装")
    @Nested
    class DataPatternTestPattern {

        @DisplayName("3件検証")
        @Test
        void dataPatternTest() {

            //GIVEN
            for (DataPattern param : Arrays.asList(
                    new DataPattern(1, "Tokyo", 1, LocalDateTime.of(2012, 1, 1, 12, 12)),
                    new DataPattern(2, "Osaka", 1, LocalDateTime.of(2013, 4, 1, 0, 0)),
                    new DataPattern(3, "Kyoto", 1, LocalDateTime.of(2012, 3, 2, 0, 12))
            )) {
                //WHEN
                City actual = target.findCity(param.cityId);

                //THEN
                assertThat(actual.getCity()).isEqualTo(param.expCity);
                assertThat(actual.getCountryId()).isEqualTo(param.expCountryId);
                assertThat(actual.getLastUpdate()).isEqualTo(param.expLastUpdate);
            }
        }

        class DataPattern {

            private final int cityId;
            private final String expCity;
            private final int expCountryId;
            private final LocalDateTime expLastUpdate;

            DataPattern(int cityId, String expCity, int expCountryId, LocalDateTime expLastUpdate) {

                this.cityId = cityId;
                this.expCity = expCity;
                this.expCountryId = expCountryId;
                this.expLastUpdate = expLastUpdate;
            }
        }
    }

    @DisplayName("ParameterizedTestパターン")
    @Nested
    class ParameterizedTestPattern {

        @DisplayName("3件検証")
        @ParameterizedTest
        @ArgumentsSource(ParameterizedSource.class)
        void parameterizedTest(
                //GIVEN
                ParameterizedSource args
        ) {
            //WHEN
            City actual = target.findCity(args.cityId);

            //THEN
            assertThat(actual.getCity()).isEqualTo(args.city);
            assertThat(actual.getCountryId()).isEqualTo(args.countryId);
            assertThat(actual.getLastUpdate()).isEqualTo(args.lastUpdate);

        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ParameterizedSource implements ArgumentsProvider {

        private int cityId;
        private String city;
        private int countryId;
        private LocalDateTime lastUpdate;

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new ParameterizedSource()
                            .testParam(1).expectParam("Tokyo", 1, "2012-01-01 12:12:00")
                    ),
                    Arguments.of(new ParameterizedSource()
                            .testParam(2).expectParam("Osaka", 1, "2013-04-01 00:00:00")
                    ),
                    Arguments.of(new ParameterizedSource()
                            .testParam(3).expectParam("Kyoto", 1, "2012-03-02 00:12:00")
                    )
            );
        }

        private ParameterizedSource expectParam(String city, int countryId, String lastUpdate) {
            this.city = city;
            this.countryId = countryId;
            this.lastUpdate = LocalDateTime.parse(lastUpdate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return this;
        }

        private ParameterizedSource testParam(int cityId) {
            this.cityId = cityId;
            return this;
        }
    }

    @DisplayName("複数取得の検証")
    @Nested
    class assertionSetAct {

        @DisplayName("3件取得")
        @Test
        void parameterizedTest() {

            //GIVEN
            int countryId = 1;

            //WHEN
            Set<City> actual = target.findCities(countryId);

            //THEN
            List<Tuple> expect = Arrays.asList(
                    Tuple.tuple(1, "Tokyo", 1, LocalDateTime.parse("2012-01-01 12:12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                    Tuple.tuple(2, "Osaka", 1, LocalDateTime.parse("2013-04-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                    Tuple.tuple(3, "Kyoto", 1, LocalDateTime.parse("2012-03-02 00:12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            );
            assertThat(actual)
                    .hasSize(3)
                    .extracting("cityId", "city", "countryId", "lastUpdate")
                    .contains(expect.toArray(new Tuple[0]));
        }
    }

}
