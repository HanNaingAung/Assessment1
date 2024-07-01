package org.example.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
public class TopupPayload {
     private String redirectUrl;
     private long amount;
     private String authToken;
     private String signature;
     private String nonce;
     private String timestamp;
     private String env;

}
