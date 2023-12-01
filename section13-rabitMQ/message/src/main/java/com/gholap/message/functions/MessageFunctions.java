package com.gholap.message.functions;

import com.gholap.message.dto.AccountsMsgdto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {

    private static final Logger log= LoggerFactory.getLogger(MessageFunctions.class);
    @Bean
    public Function<AccountsMsgdto, AccountsMsgdto> email(){
        return accountsMsgdto -> {
          log.info("Sending email with details: "+accountsMsgdto.toString());
          return accountsMsgdto;
        };
    }
    @Bean
    public Function<AccountsMsgdto, Long> sms(){
        return accountsMsgdto -> {
            log.info("Sending sms with details: "+accountsMsgdto.toString());
            return accountsMsgdto.accountNumber();
        };
    }
}
