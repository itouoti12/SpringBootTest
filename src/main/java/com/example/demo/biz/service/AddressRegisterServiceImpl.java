package com.example.demo.biz.service;

import com.example.demo.biz.domain.Address;
import com.example.demo.exception.BusinessException;
import com.example.demo.infra.repository.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AddressRegisterServiceImpl implements AddressRegisterService{

    private final AddressMapper addressMapper;


    @Override
    public int addressRegister(Set<Address> addressSet) {

        if (addressSet.size() == 0){
            throw new BusinessException(HttpStatus.BAD_REQUEST,"No Data");
        }

        int successCount = 0;

        for (Address address: addressSet){

            if (address.getAddressId() == 0){
                addressMapper.save(address);
                successCount++;
            }

        }

        return successCount;
    }
}
