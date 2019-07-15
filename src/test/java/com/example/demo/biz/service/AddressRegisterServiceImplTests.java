package com.example.demo.biz.service;

import com.example.demo.biz.domain.Address;
import com.example.demo.infra.repository.AddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressRegisterServiceImplTests {


    AddressRegisterServiceImpl target;

    @Mock
    AddressMapper addressMapper;

    @BeforeEach
    void setUp() {
        target = new AddressRegisterServiceImpl(addressMapper);
        doNothing().when(addressMapper).save(any());

    }

    @DisplayName("2件中１件が該当であり、アドレスデータを登録する")
    @Test
    void test2in1() {
        //GIVEN
        //instanceInitializer
        Set<Address> params = new HashSet() {
            {
                add(Address.builder()
                        .address("fashion 109")
                        .district("Shibuya")
                        .cityId(1)
                        .phone("01202002002")
                        .lastUpdate(LocalDateTime.of(2014, 12, 11, 1, 1))
                        .build());
                add(Address.builder()
                        .addressId(2)
                        .address("salary 999")
                        .district("Shinbashi")
                        .cityId(1)
                        .phone("8887979888")
                        .lastUpdate(LocalDateTime.of(2019, 3, 2, 9, 30))
                        .build());
            }
        };

        Set<Address> params1 = new HashSet();
        params1.add(Address.builder()
                .address("fashion 109")
                .district("Shibuya")
                .build());
        params1.add(Address.builder()
                .address("salary 999")
                .district("Shinbashi")
                .build());



        //WHEN
        int actual = target.addressRegister(params);

        //THEN
        assertThat(actual).isEqualTo(1);
        verify(addressMapper, times(1)).save(any());
    }

    @DisplayName("1件アドレスデータを登録する。")
    @Test
    void test1Pattern1() {

        //GIVEN
        //Java9から使用可能な記法
        // Set<Address> params = new HashSet<>(List.of(Address.builder()
        Set<Address> params = new HashSet<>(Collections.singletonList(Address.builder()
                .address("fashion 109")
                .district("Shibuya")
                .cityId(1)
                .phone("01202002002")
                .lastUpdate(LocalDateTime.of(2014, 12, 11, 1, 1))
                .build()));

        //WHEN
        int actual = target.addressRegister(params);

        //THEN
        assertThat(actual).isEqualTo(1);
        verify(addressMapper, times(1)).save(any());
    }
}
