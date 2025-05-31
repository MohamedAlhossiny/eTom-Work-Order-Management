package com.etom.model;

import lombok.Data;

@Data
public class WorkOrderItem {
    private Long id;
    private Long workOrderId;
    private String action;
    private String description;
    private String state;
    private Integer sequence;
} 