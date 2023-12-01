package com.gholap.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
        name = "CustomerDetails",
        description = "Schema to hold Customer,Account,Cards and Loans information"
)
public class CustomerDetailsDto {

    @Schema(
            description = "Name of the Customer",example = "Eazy Bytes"
    )
    @NotEmpty(message = "Name can not be null or empty")
    @Size(min = 5,max = 30, message = "The length of the customer name should be between 5 to 30")
    private String name;
    @Schema(
            description = "Email address of the Customer",example = "sachin@gmail.com"
    )
    @NotEmpty(message = "Email address can not be null or empty")
    @Email(message = "Email address should be valid value")
    private String email;
    @Schema(
            description = "MobileNumber of the Customer",example = "9327986543"
    )
    @Pattern(regexp = "(^$|[0-9]{10})",message = "Mobile number must be 10 digit")
    private String mobileNumber;
    @Schema(
            description = "Account Details of the Customer"
    )
    private AccountsDto accountsDto;

    @Schema(
            description = "Loans Details of the Customer"
    )
    private LoansDto loansDto;
    @Schema(
            description = "Cards Details of the Customer"
    )
    private CardsDto cardsDto;
}
