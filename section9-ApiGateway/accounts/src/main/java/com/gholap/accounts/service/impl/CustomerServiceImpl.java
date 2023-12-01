package com.gholap.accounts.service.impl;

import com.gholap.accounts.dto.AccountsDto;
import com.gholap.accounts.dto.CardsDto;
import com.gholap.accounts.dto.CustomerDetailsDto;
import com.gholap.accounts.dto.LoansDto;
import com.gholap.accounts.entity.Accounts;
import com.gholap.accounts.entity.Customer;
import com.gholap.accounts.exception.ResourceNotFoundException;
import com.gholap.accounts.mapper.AccountsMapper;
import com.gholap.accounts.mapper.CustomeMapper;
import com.gholap.accounts.repository.AccountsRepository;
import com.gholap.accounts.repository.CustomerRepository;
import com.gholap.accounts.service.ICustomerService;
import com.gholap.accounts.service.client.CardsFeignClient;
import com.gholap.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()->new ResourceNotFoundException("Customer","mobileNumber",mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                ()->new ResourceNotFoundException("Account","CustomerId",customer.getCustomerId().toString())
        );

       CustomerDetailsDto customerDetailsDto = CustomeMapper.mapToCustomerDetailsDto(customer,new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts,new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId,mobileNumber);
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId,mobileNumber);
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;
    }
}
