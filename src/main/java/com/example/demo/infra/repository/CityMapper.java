package com.example.demo.infra.repository;

import com.example.demo.biz.domain.City;
import org.apache.ibatis.annotations.*;

import java.util.Set;

@Mapper
public interface CityMapper {


    //application.ymlでmapUnderscoreToCamelCaseをtrueにしている場合はsnake-camel間は自動でマッピングされる
    @Results(id = "City", value = {
            @Result(column = "city_id", property = "cityId"),
            @Result(column = "city", property = "city"),
            @Result(column = "country_id", property = "countryId"),
            @Result(column = "last_update", property = "lastUpdate")
    })
    @Select("SELECT * FROM city WHERE city_id = #{cityId}")
    City findCity(@Param("cityId") int cityId);

    @Select("SELECT * FROM city WHERE country_id = #{countryId}")
    Set<City> findCities(@Param("countryId") int countryId);
}
