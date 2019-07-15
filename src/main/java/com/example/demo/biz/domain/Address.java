package com.example.demo.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    int addressId;
    String address;
    String address2;
    String district;
    int cityId;
    String postalCode;
    String phone;
    LocalDateTime lastUpdate;
}
