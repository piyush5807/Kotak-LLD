package com.example.demo_kotak.auth;

public class AuthenticationService {

    public void authenticate(Long accountId, String securityKey){
        /**
         * 1. call account module and fetch details
         * 2. if security key not matching, return 400
         * 3. if failure in retrieving details, return 500
         */
    }
}
