package com.example.demo.biz.service;

import com.example.demo.biz.domain.Address;

import java.util.Set;

public interface AddressRegisterService {
    int addressRegister(Set<Address> addressSet);
}
