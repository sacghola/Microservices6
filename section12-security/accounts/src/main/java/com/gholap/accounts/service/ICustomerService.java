package com.gholap.accounts.service;

import com.gholap.accounts.dto.CustomerDetailsDto;

public interface ICustomerService {
    CustomerDetailsDto fetchCustomerDetails(String mobileNumber,String correlationId);
}
