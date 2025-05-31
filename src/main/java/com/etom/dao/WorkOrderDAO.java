package com.etom.dao;

import com.etom.config.DatabaseConfig;
import com.etom.model.WorkOrder;
import com.etom.model.WorkOrderItem;
import com.etom.model.PlaceRef;
import com.etom.model.RelatedParty;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorkOrderDAO {
    private final DataSource dataSource;

    public WorkOrderDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    public Optional<WorkOrder> findById(Long id) {
        String sql = "SELECT * FROM work_order WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                WorkOrder workOrder = mapWorkOrder(rs);
                workOrder.setItems(findWorkOrderItems(id));
                workOrder.setPlaces(findPlaceRefs(id));
                workOrder.setRelatedParties(findRelatedParties(id));
                return Optional.of(workOrder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<WorkOrder> findAll(String state, String priority, String externalId,
            Date startDate, Date endDate, Date createdAfter, Date createdBefore) {
        List<WorkOrder> workOrders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM work_order WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (state != null) {
            sql.append(" AND state = ?");
            params.add(state);
        }
        if (priority != null) {
            sql.append(" AND priority = ?");
            params.add(priority);
        }
        if (externalId != null) {
            sql.append(" AND external_id = ?");
            params.add(externalId);
        }
        if (startDate != null) {
            sql.append(" AND start_date >= ?");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append(" AND end_date <= ?");
            params.add(endDate);
        }
        if (createdAfter != null) {
            sql.append(" AND created_at >= ?");
            params.add(createdAfter);
        }
        if (createdBefore != null) {
            sql.append(" AND created_at <= ?");
            params.add(createdBefore);
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                WorkOrder workOrder = mapWorkOrder(rs);
                Long id = rs.getLong("id");
                workOrder.setItems(findWorkOrderItems(id));
                workOrder.setPlaces(findPlaceRefs(id));
                workOrder.setRelatedParties(findRelatedParties(id));
                workOrders.add(workOrder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workOrders;
    }

    public WorkOrder create(WorkOrder workOrder) {
        String sql = "INSERT INTO work_order (external_id, state, priority, description, " +
                    "start_date, end_date, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, workOrder.getExternalId());
            stmt.setString(2, workOrder.getState());
            stmt.setString(3, workOrder.getPriority());
            stmt.setString(4, workOrder.getDescription());
            stmt.setTimestamp(5, new Timestamp(workOrder.getStartDate().getTime()));
            stmt.setTimestamp(6, new Timestamp(workOrder.getEndDate().getTime()));
            stmt.setTimestamp(7, new Timestamp(workOrder.getCreatedAt().getTime()));
            stmt.setTimestamp(8, new Timestamp(workOrder.getUpdatedAt().getTime()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                workOrder.setId(rs.getLong(1));
                
                // Save work order items
                if (workOrder.getItems() != null && !workOrder.getItems().isEmpty()) {
                    saveWorkOrderItems(workOrder);
                }
                
                // Save places
                if (workOrder.getPlaces() != null && !workOrder.getPlaces().isEmpty()) {
                    savePlaceRefs(workOrder);
                }
                
                // Save related parties
                if (workOrder.getRelatedParties() != null && !workOrder.getRelatedParties().isEmpty()) {
                    saveRelatedParties(workOrder);
                }
                
                return workOrder;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<WorkOrderItem> findWorkOrderItems(Long workOrderId) {
        List<WorkOrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM work_order_item WHERE work_order_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, workOrderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                WorkOrderItem item = new WorkOrderItem();
                item.setId(rs.getLong("id"));
                item.setWorkOrderId(workOrderId);
                item.setAction(rs.getString("action"));
                item.setDescription(rs.getString("description"));
                item.setState(rs.getString("state"));
                item.setSequence(rs.getInt("sequence"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void saveWorkOrderItems(WorkOrder workOrder) {
        String sql = "INSERT INTO work_order_item (work_order_id, action, description, state, sequence) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (WorkOrderItem item : workOrder.getItems()) {
                stmt.setLong(1, workOrder.getId());
                stmt.setString(2, item.getAction());
                stmt.setString(3, item.getDescription());
                stmt.setString(4, item.getState());
                stmt.setInt(5, item.getSequence());
                stmt.addBatch();
            }
            stmt.executeBatch();
            
            // Get generated keys
            ResultSet rs = stmt.getGeneratedKeys();
            int index = 0;
            while (rs.next()) {
                workOrder.getItems().get(index).setId(rs.getLong(1));
                workOrder.getItems().get(index).setWorkOrderId(workOrder.getId());
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePlaceRefs(WorkOrder workOrder) {
        String sql = "INSERT INTO place_ref (work_order_id, role, name, address, city, state, zip_code) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (PlaceRef place : workOrder.getPlaces()) {
                stmt.setLong(1, workOrder.getId());
                stmt.setString(2, place.getRole());
                stmt.setString(3, place.getName());
                stmt.setString(4, place.getAddress());
                stmt.setString(5, place.getCity());
                stmt.setString(6, place.getState());
                stmt.setString(7, place.getZipCode());
                stmt.addBatch();
            }
            stmt.executeBatch();
            
            // Get generated keys
            ResultSet rs = stmt.getGeneratedKeys();
            int index = 0;
            while (rs.next()) {
                workOrder.getPlaces().get(index).setId(rs.getLong(1));
                workOrder.getPlaces().get(index).setWorkOrderId(workOrder.getId());
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveRelatedParties(WorkOrder workOrder) {
        String sql = "INSERT INTO related_party (work_order_id, role, name, email, phone) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (RelatedParty party : workOrder.getRelatedParties()) {
                stmt.setLong(1, workOrder.getId());
                stmt.setString(2, party.getRole());
                stmt.setString(3, party.getName());
                stmt.setString(4, party.getEmail());
                stmt.setString(5, party.getPhone());
                stmt.addBatch();
            }
            stmt.executeBatch();
            
            // Get generated keys
            ResultSet rs = stmt.getGeneratedKeys();
            int index = 0;
            while (rs.next()) {
                workOrder.getRelatedParties().get(index).setId(rs.getLong(1));
                workOrder.getRelatedParties().get(index).setWorkOrderId(workOrder.getId());
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<PlaceRef> findPlaceRefs(Long workOrderId) {
        List<PlaceRef> places = new ArrayList<>();
        String sql = "SELECT * FROM place_ref WHERE work_order_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, workOrderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PlaceRef place = new PlaceRef();
                place.setId(rs.getLong("id"));
                place.setWorkOrderId(workOrderId);
                place.setRole(rs.getString("role"));
                place.setName(rs.getString("name"));
                place.setAddress(rs.getString("address"));
                place.setCity(rs.getString("city"));
                place.setState(rs.getString("state"));
                place.setZipCode(rs.getString("zip_code"));
                places.add(place);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return places;
    }

    private List<RelatedParty> findRelatedParties(Long workOrderId) {
        List<RelatedParty> parties = new ArrayList<>();
        String sql = "SELECT * FROM related_party WHERE work_order_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, workOrderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                RelatedParty party = new RelatedParty();
                party.setId(rs.getLong("id"));
                party.setWorkOrderId(workOrderId);
                party.setRole(rs.getString("role"));
                party.setName(rs.getString("name"));
                party.setEmail(rs.getString("email"));
                party.setPhone(rs.getString("phone"));
                parties.add(party);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parties;
    }

    private WorkOrder mapWorkOrder(ResultSet rs) throws SQLException {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(rs.getLong("id"));
        workOrder.setExternalId(rs.getString("external_id"));
        workOrder.setState(rs.getString("state"));
        workOrder.setPriority(rs.getString("priority"));
        workOrder.setDescription(rs.getString("description"));
        workOrder.setStartDate(rs.getTimestamp("start_date"));
        workOrder.setEndDate(rs.getTimestamp("end_date"));
        workOrder.setCreatedAt(rs.getTimestamp("created_at"));
        workOrder.setUpdatedAt(rs.getTimestamp("updated_at"));
        return workOrder;
    }
} 