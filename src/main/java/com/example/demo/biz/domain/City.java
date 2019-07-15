package com.example.demo.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class City {

    private int cityId;
    private String city;
    private int countryId;
    private LocalDateTime lastUpdate;

}
