package org.serviceorder.database.dao;

import org.serviceorder.database.ConnectionJDBC;
import org.serviceorder.database.entities.ServiceOrder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class ServiceOrderDao implements BaseDao<ServiceOrder>{
    Connection connection;

    public ServiceOrderDao() {
        try {
            this.connection = new ConnectionJDBC().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ServiceOrder insert (ServiceOrder service_order) {
        String sql = "INSERT INTO service_orders (code,description,name,cache_id,time) VALUES (?,?,?,?,?);";
        try {
            LocalDateTime now = LocalDateTime.now();

            System.out.println(service_order.toString());

            String cache_id = UUID.randomUUID().toString();

            PreparedStatement pst = this.connection.prepareStatement(sql);
            pst.setString(1, service_order.getCode());
            pst.setString(2, service_order.getDescription());
            pst.setString(3, service_order.getName());
            pst.setString(4, cache_id);
            pst.setTimestamp(5, Timestamp.valueOf(now));
            pst.execute();

            String sqlSelect = "select * from service_orders where cache_id=?;";
            PreparedStatement pstSelect = this.connection.prepareStatement(sqlSelect);

            pstSelect.setString(1, cache_id);
            ResultSet rs = pstSelect.executeQuery();

            if(rs.next()) {
                service_order.setId(rs.getInt("id"));
                service_order.setCacheId(cache_id);
                return service_order;
            }

            return null;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public boolean delete(ServiceOrder service_order) {
        String sql = "DELETE FROM service_orders WHERE id=?;";
        try {
            PreparedStatement pst = this.connection.prepareStatement(sql);
            pst.setInt(1, service_order.getId());
            pst.execute();

            return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(ServiceOrder service_order) {
        String sql = "UPDATE service_orders SET code=?,description=?,name=? WHERE id=?";
        try {
            PreparedStatement pst = this.connection.prepareStatement(sql);
            pst.setString(1, service_order.getCode());
            pst.setString(2, service_order.getDescription() );
            pst.setString(3, service_order.getName() );
            pst.setInt(4, service_order.getId());
            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public ServiceOrder findById(ServiceOrder service_order) {
        String sql = "SELECT * FROM service_orders WHERE id=?;";
        try {
            PreparedStatement pst = this.connection.prepareStatement(sql);
            pst.setInt(1, service_order.getId());
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                service_order.setCode(rs.getString("code"));
                service_order.setDescription(rs.getString("description"));
                service_order.setTime(rs.getString("time"));
                service_order.setId(Integer.parseInt(rs.getString("id")));
            }

            return service_order;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return service_order;
        }
    }

    public ResultSet findAll() {
        String sql = "SELECT * FROM service_orders;";
        try {
            PreparedStatement pst = this.connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            return rs;

        } catch (SQLException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
            return null;
        }
    }

    public Integer getRegistersQuantity() {
        String sql = "SELECT count(*) as register_quantity FROM service_orders;";
        try {
            PreparedStatement pst = this.connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt("register_quantity");
        } catch (SQLException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
            return null;
        }
    }
}
