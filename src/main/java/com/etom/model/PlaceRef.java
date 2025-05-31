package com.etom.model;

import lombok.Data;

@Data
public class PlaceRef {
    private Long id;
    private Long workOrderId;
    private String role;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
} 