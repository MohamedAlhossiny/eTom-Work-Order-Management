package com.etom.resource;

import com.etom.dao.WorkOrderDAO;
import com.etom.model.WorkOrder;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/workOrder")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkOrderResource {
    private final WorkOrderDAO workOrderDAO;

    public WorkOrderResource() {
        this.workOrderDAO = new WorkOrderDAO();
    }

    @GET
    @Path("/{id}")
    public Response getWorkOrder(@PathParam("id") Long id) {
        return workOrderDAO.findById(id)
                .map(workOrder -> Response.ok(workOrder).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getWorkOrders(
            @QueryParam("state") String state,
            @QueryParam("priority") String priority,
            @QueryParam("externalId") String externalId,
            @QueryParam("startDate") Date startDate,
            @QueryParam("endDate") Date endDate,
            @QueryParam("createdAfter") Date createdAfter,
            @QueryParam("createdBefore") Date createdBefore) {
        
        java.sql.Date sqlStartDate = (startDate != null) ? new java.sql.Date(startDate.getTime()) : null;
        java.sql.Date sqlEndDate = (endDate != null) ? new java.sql.Date(endDate.getTime()) : null;
        java.sql.Date sqlCreatedAfter = (createdAfter != null) ? new java.sql.Date(createdAfter.getTime()) : null;
        java.sql.Date sqlCreatedBefore = (createdBefore != null) ? new java.sql.Date(createdBefore.getTime()) : null;
        
        List<WorkOrder> workOrders = workOrderDAO.findAll(state, priority, externalId, 
            sqlStartDate, sqlEndDate, sqlCreatedAfter, sqlCreatedBefore);
        return Response.ok(workOrders).build();
    }

    @POST
    public Response createWorkOrder(WorkOrder workOrder) {
        // Set creation and update timestamps
        Date now = new Date();
        workOrder.setCreatedAt(now);
        workOrder.setUpdatedAt(now);
        
        WorkOrder created = workOrderDAO.create(workOrder);
        if (created != null) {
            return Response.status(Response.Status.CREATED)
                    .entity(created)
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
} 