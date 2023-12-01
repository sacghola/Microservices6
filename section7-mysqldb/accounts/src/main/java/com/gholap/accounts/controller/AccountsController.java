package com.gholap.accounts.controller;

import com.gholap.accounts.constants.AccountsConstants;
import com.gholap.accounts.dto.AccountsContactInfoDto;
import com.gholap.accounts.dto.CustomerDto;
import com.gholap.accounts.dto.ErrorResponseDto;
import com.gholap.accounts.dto.ResponseDto;
import com.gholap.accounts.service.IAccountsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST API for Accounts in EazyBank",
        description = "CRUD REST APIs in EazyBank to CREATE,UPDATE,FETCH AND DELETE account details"
)
@RestController
@RequestMapping(value = "/api",produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class AccountsController {

    private final IAccountsService iAccountsService;

    public AccountsController(IAccountsService iAccountsService) {
        this.iAccountsService = iAccountsService;
    }

    //1st approach to read configuration
   @Value("${build.version}")
   private String buildVersion;

    //2nd approach to read configuration
    @Autowired
    private Environment environment;

    //3rd approach to read bunch of information
    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;

   @Operation(
           summary = "Create Account REST API",
           description = "REST API to create new Customer & Account inside EazyBank"
   )
   @ApiResponse(
           responseCode = "201",
           description = "HTTP Status CREATED"
   )
  @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto){
      iAccountsService.createAccount(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201,AccountsConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Account Details REST API",
            description = "REST API to fetch Customer & Account details based on a mobileNumber"
    )
    @ApiResponses({
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam
                                                @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile number must be 10 digit")
                                                String mobileNumber){
      CustomerDto customerDto = iAccountsService.fetchAccount(mobileNumber);
      return ResponseEntity.status(HttpStatus.OK).body(customerDto);
    }

    @Operation(
            summary = "Update Account Details REST API",
            description = "REST API to update Customer & Account based on accountNumber"
    )
    @ApiResponses({
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    ), @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
    ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )
            )
    }
    )
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto){
    boolean isUpdated = iAccountsService.updateAccount(customerDto);
    if(isUpdated){
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(new ResponseDto(AccountsConstants.STATUS_200,AccountsConstants.MESSAGE_200));
    }else{
      return ResponseEntity
              .status(HttpStatus.EXPECTATION_FAILED)
              .body(new ResponseDto(AccountsConstants.STATUS_417,AccountsConstants.MESSAGE_417_UPDATE));
    }
    }

    @Operation(
            summary = "Delete Account & Customer Details REST API",
            description = "REST API to delete Customer & Account based on mobileNumber"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ), @ApiResponse(
            responseCode = "417",
            description = "Expectation Failed"
    ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam
                                        @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile number must be 10 digit")
                                        String mobileNumber){
    boolean isDeleted = iAccountsService.deleteAccount(mobileNumber);
    if(isDeleted){
      return ResponseEntity
              .status(HttpStatus.OK)
              .body(new ResponseDto(AccountsConstants.STATUS_200,AccountsConstants.MESSAGE_200));
    }else {
      return ResponseEntity
              .status(HttpStatus.EXPECTATION_FAILED)
              .body(new ResponseDto(AccountsConstants.STATUS_417,AccountsConstants.MESSAGE_417_DELETE));
    }
    }

    @Operation(
            summary = "Get Build Information",
            description = "Get Build Information that is deployed into accounts microservices"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildVersion(){
       return ResponseEntity
               .status(HttpStatus.OK)
               .body(buildVersion);
    }

    @Operation(
            summary = "Get java version Information",
            description = "Get java version Information that is deployed into accounts microservices"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME")); //MAVEN_HOME
    }

    @Operation(
            summary = "Get Contact Information",
            description = "Get contact Information that is deployed into accounts microservices"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Http Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }


}
