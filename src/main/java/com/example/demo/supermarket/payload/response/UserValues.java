package com.example.demo.supermarket.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserValues{
	public Long id;
	public String username;
	public String email;
	public List<String> roles;
}
