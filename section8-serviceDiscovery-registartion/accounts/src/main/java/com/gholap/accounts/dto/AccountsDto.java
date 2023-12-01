package com.gholap.accounts.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
        name = "Accounts",
        description = "Schema to hold Account information"
)
public class AccountsDto {
    @NotEmpty(message = "AccountNumber can not be null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})",message = "AccountNumber must be 10 digit")
    @Schema(
            description = "Account Number of Eazy Bank Account",example = "4356479809"
    )
    private Long accountNumber;
    @Schema(
            description = "AccountType of Eazy Bank Account",example = "Saving"
    )
    @NotEmpty(message = "AccountType can not be null or empty")
    private String accountType;
    @Schema(
            description = "Eazy Bank Branch Address",example = "123 New York"
    )
    @NotEmpty(message = "BranchAddress can not be null or empty")
    private String branchAddress;
}
