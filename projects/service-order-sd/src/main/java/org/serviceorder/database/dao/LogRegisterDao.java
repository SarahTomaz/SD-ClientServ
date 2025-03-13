package org.serviceorder.database.dao;

import org.serviceorder.database.ConnectionJDBC;
import org.serviceorder.database.entities.LogRegister;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogRegisterDao implements BaseDao<LogRegister> {
    Connection connection;

    public LogRegisterDao() {
        try {
            this.connection = new ConnectionJDBC().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LogRegister insert (LogRegister log_register) {
        String sql = "INSERT INTO log_registers (operation,operationResponse,time) VALUES (?,?,?);";
        try {
            PreparedStatement pst = this.connection.prepareStatement(sql);
            pst.setString(1, log_register.getOperation());
            pst.setString(2, log_register.getOperationResponse());
            pst.setString(3, log_register.getTime().toString());
            pst.execute();

            String sqlSelect = "select * from log_registers where operation=? and operation_response=?;";
            PreparedStatement pstSelect = this.connection.prepareStatement(sqlSelect);

            pstSelect.setString(1, log_register.getOperation());
            pstSelect.setString(2, log_register.getOperationResponse());
            ResultSet rs = pstSelect.executeQuery();

            if(rs.next()) {
                log_register.setId(rs.getInt("id"));
                return log_register;
            }

            return null;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(LogRegister e) {
        return false;
    }

    @Override
    public boolean update(LogRegister e) {
        return false;
    }

    @Override
    public LogRegister findById(LogRegister e) {
        return null;
    }

    public ResultSet findAll() {
        String sql = "SELECT * FROM log_registers;";
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

    @Override
    public Integer getRegistersQuantity() {
        return null;
    }
}
