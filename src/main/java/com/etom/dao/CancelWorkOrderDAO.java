package com.etom.dao;

import com.etom.config.DatabaseConfig;
import com.etom.model.CancelWorkOrder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CancelWorkOrderDAO {
    private final DataSource dataSource;

    public CancelWorkOrderDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    public Optional<CancelWorkOrder> findById(Long id) {
        String sql = "SELECT * FROM cancel_work_order WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapCancelWorkOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<CancelWorkOrder> findAll(String status, Long workOrderId, Date startDate, Date endDate) {
        List<CancelWorkOrder> cancelRequests = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM cancel_work_order WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (workOrderId != null) {
            sql.append(" AND work_order_id = ?");
            params.add(workOrderId);
        }
        if (startDate != null) {
            sql.append(" AND requested_at >= ?");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append(" AND requested_at <= ?");
            params.add(endDate);
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cancelRequests.add(mapCancelWorkOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cancelRequests;
    }

    private CancelWorkOrder mapCancelWorkOrder(ResultSet rs) throws SQLException {
        CancelWorkOrder cancelRequest = new CancelWorkOrder();
        cancelRequest.setId(rs.getLong("id"));
        cancelRequest.setWorkOrderId(rs.getLong("work_order_id"));
        cancelRequest.setReason(rs.getString("reason"));
        cancelRequest.setStatus(rs.getString("status"));
        cancelRequest.setRequestedBy(rs.getString("requested_by"));
        cancelRequest.setRequestedAt(rs.getTimestamp("requested_at"));
        
        Timestamp processedAt = rs.getTimestamp("processed_at");
        if (processedAt != null) {
            cancelRequest.setProcessedAt(processedAt);
        }
        
        return cancelRequest;
    }

    public CancelWorkOrder create(CancelWorkOrder cancelRequest) {
        String sql = "INSERT INTO cancel_work_order (work_order_id, reason, status, " +
                    "requested_by, requested_at, processed_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, cancelRequest.getWorkOrderId());
            stmt.setString(2, cancelRequest.getReason());
            stmt.setString(3, cancelRequest.getStatus());
            stmt.setString(4, cancelRequest.getRequestedBy());
            stmt.setTimestamp(5, new Timestamp(cancelRequest.getRequestedAt().getTime()));
            stmt.setTimestamp(6, cancelRequest.getProcessedAt() != null ? 
                new Timestamp(cancelRequest.getProcessedAt().getTime()) : null);
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                cancelRequest.setId(rs.getLong(1));
                return cancelRequest;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
} 