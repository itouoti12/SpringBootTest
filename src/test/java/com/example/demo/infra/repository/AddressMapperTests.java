package com.example.demo.infra.repository;

import com.example.demo.biz.domain.Address;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.operation.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
public class AddressMapperTests {

    @Autowired
    private AddressMapper target;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private final Operation RESET_DATA = Operations.deleteAllFrom("city", "address");
    private final Operation INSERT_DATA = Operations.insertInto("city")
            .columns("city_id", "city", "country_id", "last_update")
            .values("1", "Tokyo", "1", "2012-01-01 12:12:00")
            .build();
    private final Operation ALTER_DISABLE = Operations.sequenceOf(
            Operations.sql("ALTER TABLE city SET REFERENTIAL_INTEGRITY FALSE")
    );
    private final Operation ALTER_ENABLE = Operations.sequenceOf(
            Operations.sql("ALTER TABLE city SET REFERENTIAL_INTEGRITY TRUE")
    );
    private final Operation ALTER_RESET_SEQUENCE = Operations.sequenceOf(
            Operations.sql("ALTER SEQUENCE address_address_id_seq RESTART WITH 1")
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
                ALTER_RESET_SEQUENCE,
                INSERT_DATA,
                ALTER_ENABLE
        ));
    }

    @AfterEach
    void tearDown() {
        dbSetup(Operations.sequenceOf(
                ALTER_DISABLE,
                RESET_DATA,
                ALTER_RESET_SEQUENCE,
                ALTER_ENABLE
        ));
    }

    @DisplayName("シーケンスから自動生成されたデータを取得")
    @Nested
    class SequenceParameterTest {


        @DisplayName("1件Insertし、シーケンスによって自動生成された値を検証する")
        @ParameterizedTest
        @ArgumentsSource(ParameterizedSource.class)
        void test1Insert(ParameterizedSource args) {

            //GIVEN
            Address actual = Address.builder()
                    .address(args.address)
                    .district(args.district)
                    .cityId(args.cityId)
                    .phone(args.phone)
                    .lastUpdate(args.lastUpdate)
                    .build();

            //WHEN
            target.save(actual);

            //THEN
            assertThat(actual.getAddressId()).isEqualTo(args.expectAddressId);

            int expect = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM address", Integer.class);
            assertThat(expect).isEqualTo(args.expectCount);

        }

        @DisplayName("2件Insertし、シーケンスによって自動生成された値を検証する")
        @Test
        void test2Insert() {

            //GIVEN
            Address actualCase1 = Address.builder()
                    .address("613 Korolev Drive")
                    .district("Shibuya")
                    .cityId(1)
                    .phone("28303384290")
                    .lastUpdate(LocalDateTime.of(2015, 5, 2, 8, 50))
                    .build();

            Address actualCase2 = Address.builder()
                    .address("1411 Lillydale Drive")
                    .district("Shimbashi")
                    .cityId(1)
                    .phone("635297277345")
                    .lastUpdate(LocalDateTime.of(2015, 5, 2, 8, 50))
                    .build();

            //WHEN
            target.save(actualCase1);
            target.save(actualCase2);

            //THEN
            assertThat(actualCase1.getAddressId()).isEqualTo(1);
            assertThat(actualCase2.getAddressId()).isEqualTo(2);

            int expect = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM address", Integer.class);
            assertThat(expect).isEqualTo(2);

        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class ParameterizedSource implements ArgumentsProvider {

        int expectAddressId;
        int expectCount;
        String address;
        String district;
        int cityId;
        String phone;
        LocalDateTime lastUpdate;

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of(ParameterizedSource.builder()
                            .expectAddressId(1)
                            .expectCount(1)
                            .address("613 Korolev Drive")
                            .district("Shibuya")
                            .cityId(1)
                            .phone("28303384290")
                            .lastUpdate(setLastUpdate("2015-05-02 08:50:00"))
                            .build()
                    ),
                    Arguments.of(ParameterizedSource.builder()
                            .expectAddressId(1)
                            .expectCount(1)
                            .address("1411 Lillydale Drive")
                            .district("Shimbashi")
                            .cityId(1)
                            .phone("635297277345")
                            .lastUpdate(setLastUpdate("2015-05-02 08:50:00"))
                            .build()
                    )
            );
        }

        private LocalDateTime setLastUpdate(String lastUpdate) {
            return LocalDateTime.parse(lastUpdate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    @DisplayName("データ検証テスト")
    @Nested
    class FieldValue {

        private final Operation INSERT_ADDRESS_DATA = Operations.insertInto("address")
                .columns("address_id", "address", "district", "city_id", "phone", "last_update")
                .values(1, "fashion 109", "Shibuya", 1, "28303384290", "2012-01-01 12:12:00")
                .values(2, "salary 999", "Shinbashi", 1, "635297277345", "2012-01-02 12:12:00")
                .build();

        @BeforeEach
        void setUp() {
            dbSetup(INSERT_ADDRESS_DATA);
        }

        @DisplayName("Setに含まれる要素をまとめて検証する")
        @Test
        void dataValueTest() {

            //GIVEN
            int cityId = 1;

            //WHEN
            Set<Address> actual = target.findAddress(cityId);

            //THEN
            assertThat(actual).extracting("addressId").allMatch(i -> Arrays.asList(1, 2).contains(i));
            assertThat(actual).extracting("address").allMatch(i -> Arrays.asList("fashion 109", "salary 999").contains(i));
            assertThat(actual).extracting("address2").allMatch(Objects::isNull);
            assertThat(actual).extracting("district").allMatch(i -> Arrays.asList("Shibuya", "Shinbashi").contains(i));
            assertThat(actual).extracting("postalCode").allMatch(Objects::isNull);
            assertThat(actual).extracting("phone").allMatch(i -> Arrays.asList("28303384290", "635297277345").contains(i));
            assertThat(actual).extracting("lastUpdate").allMatch(i -> Arrays.asList(
                    LocalDateTime.parse("2012-01-01 12:12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.parse("2012-01-02 12:12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ).contains(i));
        }
    }
}
