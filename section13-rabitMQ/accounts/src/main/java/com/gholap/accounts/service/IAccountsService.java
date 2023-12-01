package com.gholap.accounts.service;

import com.gholap.accounts.dto.CustomerDto;

public interface IAccountsService {
    /**
     *
     * @param customerDto- CustomerDto Object
     */
    void createAccount(CustomerDto customerDto);
    CustomerDto fetchAccount(String mobileNumber);
    boolean updateAccount(CustomerDto customerDto);
    boolean deleteAccount(String mobileNumber);

    boolean updateCommunicationStatus(Long accountNumber);
}
