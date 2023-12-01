package com.gholap.accounts.service.impl;

import com.gholap.accounts.constants.AccountsConstants;
import com.gholap.accounts.dto.AccountsDto;
import com.gholap.accounts.dto.CustomerDto;
import com.gholap.accounts.entity.Accounts;
import com.gholap.accounts.entity.Customer;
import com.gholap.accounts.exception.CustomerAlreadyExistsException;
import com.gholap.accounts.exception.ResourceNotFoundException;
import com.gholap.accounts.mapper.AccountsMapper;
import com.gholap.accounts.mapper.CustomeMapper;
import com.gholap.accounts.repository.AccountsRepository;
import com.gholap.accounts.repository.CustomerRepository;
import com.gholap.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {
    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomeMapper.mapToCustomer(customerDto,new Customer());
        Optional<Customer> optionalCustomer =  customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer.isPresent()){
            throw new CustomerAlreadyExistsException("Customer Already registered with given mobile number"
            +customerDto.getMobileNumber());
        }
//        customer.setCreatedAt(LocalDateTime.now());
//        customer.setCreatedBy("Anonymous");
        Customer savedCustomer =  customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }
    private Accounts createNewAccount(Customer customer){
        Accounts newAccount= new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccountNumber = 1000000000l + new Random().nextInt(900000000);  //create 10 digit acc no

        newAccount.setAccountNumber(randomAccountNumber);
        newAccount.setAccountType(AccountsConstants.SAVING);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
//        newAccount.setCreatedAt(LocalDateTime.now());
//        newAccount.setCreatedBy("Anonymous");
        return newAccount;
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
       Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()->new ResourceNotFoundException("Customer","mobileNumber",mobileNumber)
        );
       Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                ()->new ResourceNotFoundException("Account","CustomerId",customer.getCustomerId().toString())
        );

       CustomerDto customerDto = CustomeMapper.mapToCustomerDto(customer, new CustomerDto());
       customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts,new AccountsDto()));

        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto!=null){
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    ()-> new ResourceNotFoundException("Account","AccountNumber",accountsDto.getAccountNumber().toString())
            );

            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    ()-> new ResourceNotFoundException("Customer","CustomerId",customerId.toString())
            );
            CustomeMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()-> new ResourceNotFoundException("Customer","mobileNumber",mobileNumber)
        );
        accountsRepository.deleteByCustomerId(customer.getCustomerId()); //written by us
        customerRepository.deleteById(customer.getCustomerId());  //taken this from framework
        return true;
    }
}
