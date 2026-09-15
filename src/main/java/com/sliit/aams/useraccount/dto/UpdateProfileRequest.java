package com.sliit.aams.useraccount.dto;


import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String street;
    private String city;
    private String postalCode;
}