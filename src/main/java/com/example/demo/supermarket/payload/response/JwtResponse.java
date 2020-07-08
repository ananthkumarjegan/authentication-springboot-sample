package com.example.demo.supermarket.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
	public String accessToken;
	public String tokenType = "Bearer";
	public UserValues userValues;


	public JwtResponse(String accessToken,UserValues userValues) {
		this.accessToken=accessToken;
        this.userValues=userValues;
	}

}

