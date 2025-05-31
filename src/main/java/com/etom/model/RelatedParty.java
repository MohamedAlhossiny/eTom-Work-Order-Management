package com.etom.model;

import lombok.Data;

@Data
public class RelatedParty {
    private Long id;
    private Long workOrderId;
    private String role;
    private String name;
    private String email;
    private String phone;
} 