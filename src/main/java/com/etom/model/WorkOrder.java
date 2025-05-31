package com.etom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class WorkOrder {
    private Long id;
    private String externalId;
    private String state;
    private String priority;
    private String description;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date endDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date createdAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Date updatedAt;
    
    private List<WorkOrderItem> items;
    private List<PlaceRef> places;
    private List<RelatedParty> relatedParties;
} 