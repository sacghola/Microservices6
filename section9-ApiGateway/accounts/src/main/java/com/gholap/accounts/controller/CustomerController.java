package com.gholap.accounts.controller;

import com.gholap.accounts.dto.CustomerDetailsDto;
import com.gholap.accounts.dto.ErrorResponseDto;
import com.gholap.accounts.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "REST API for Customer in EazyBank",
        description = "REST APIs in EazyBank to FETCH customer details"
)
@RestController
@RequestMapping(value = "/api",produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final ICustomerService iCustomerService;

    public CustomerController(ICustomerService iCustomerService){
        this.iCustomerService = iCustomerService;
    }
    @Operation(
            summary = "Fetch Customer Details REST API",
            description = "REST API to fetch Customer details based on a mobileNumber"
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
    @GetMapping("/fetchCustomerDetails")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(@RequestHeader("eazybank-correlation-id") String correlationId,
                                                                   @RequestParam
                                                                   @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile number must be 10 digit")
                                                                   String mobileNumber){
        logger.debug("EazyBank-correlation-id found:{}",correlationId);
      CustomerDetailsDto customerDetailsDto =  iCustomerService.fetchCustomerDetails(mobileNumber,correlationId);

      return ResponseEntity.status(HttpStatus.OK).body(customerDetailsDto);
    }
}
