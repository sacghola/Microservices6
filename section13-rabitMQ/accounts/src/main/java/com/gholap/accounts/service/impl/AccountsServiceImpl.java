package com.gholap.accounts.service.impl;

import com.gholap.accounts.constants.AccountsConstants;
import com.gholap.accounts.dto.AccountsDto;
import com.gholap.accounts.dto.AccountsMsgDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private static final Logger log = LoggerFactory.getLogger(AccountsServiceImpl.class);
    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;
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
      Accounts savedAccount =  accountsRepository.save(createNewAccount(savedCustomer));
        sendCommunication(savedAccount,savedCustomer);
    }

    private void sendCommunication(Accounts account, Customer customer){
        var accountsMsgDto = new AccountsMsgDto(account.getAccountNumber(),customer.getName(),
                customer.getEmail(),customer.getMobileNumber());
        log.info("Sending Communication Request for the details: {}",accountsMsgDto);
        var result = streamBridge.send("sendCommunication-out-0",accountsMsgDto);
        log.info("Is the Communication request successfully triggered ? : {}",result);
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

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;
        if(accountNumber!=null){
            Accounts accounts = accountsRepository.findById(accountNumber).orElseThrow(
                    () -> new ResourceNotFoundException("Account","AccountNumber",accountNumber.toString())
            );
            accounts.setCommunicationSw(true);
            accountsRepository.save(accounts);
            isUpdated = true;
        }
        return isUpdated;
    }


}
