package com.example.demo.infra.repository;

import com.example.demo.biz.domain.Address;
import org.apache.ibatis.annotations.*;

import java.util.Set;

@Mapper
public interface AddressMapper {


    @Insert("INSERT INTO address (" +
            "address_id," +
            "address," +
            "address2," +
            "district," +
            "city_id," +
            "postal_code," +
            "phone," +
            "last_update" +
            ")" +
            "VALUES (" +
            "address_address_id_seq.NEXTVAL," +
            "#{address.address}," +
            "#{address.address2}," +
            "#{address.district}," +
            "#{address.cityId}," +
            "#{address.postalCode}," +
            "#{address.phone}," +
            "#{address.lastUpdate}" +
            ")")
    @Options(useGeneratedKeys = true, keyColumn = "address_id", keyProperty = "address.addressId")
    void save(@Param("address") Address address);

    @Select("SELECT * FROM address WHERE city_id = #{cityId}")
    Set<Address> findAddress(@Param("cityId") int cityId);
}
