package com.etom.resource;

import com.etom.dao.CancelWorkOrderDAO;
import com.etom.model.CancelWorkOrder;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/cancelWorkOrder")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CancelWorkOrderResource {
    private final CancelWorkOrderDAO cancelWorkOrderDAO;

    public CancelWorkOrderResource() {
        this.cancelWorkOrderDAO = new CancelWorkOrderDAO();
    }

    @GET
    @Path("/{id}")
    public Response getCancelWorkOrder(@PathParam("id") Long id) {
        return cancelWorkOrderDAO.findById(id)
                .map(cancelRequest -> Response.ok(cancelRequest).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getCancelWorkOrders(
            @QueryParam("status") String status,
            @QueryParam("workOrderId") Long workOrderId,
            @QueryParam("startDate") Date startDate,
            @QueryParam("endDate") Date endDate) {
        java.sql.Date sqlStartDate = (startDate != null) ? new java.sql.Date(startDate.getTime()) : null;
        java.sql.Date sqlEndDate = (endDate != null) ? new java.sql.Date(endDate.getTime()) : null;
        List<CancelWorkOrder> cancelRequests = cancelWorkOrderDAO.findAll(status, workOrderId, sqlStartDate, sqlEndDate);
        return Response.ok(cancelRequests).build();
    }

    @POST
    public Response cancelWorkOrder(CancelWorkOrder cancelRequest) {
        // Set request timestamp
        cancelRequest.setRequestedAt(new Date());
        
        // Set initial status
        cancelRequest.setStatus("pending");
        
        CancelWorkOrder created = cancelWorkOrderDAO.create(cancelRequest);
        if (created != null) {
            return Response.status(Response.Status.CREATED)
                    .entity(created)
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
} 