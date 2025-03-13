package org.serviceorder.database.services;

import org.serviceorder.controller.dto.LogRegisterDTO;
import org.serviceorder.database.dao.BaseDao;
import org.serviceorder.database.dao.LogRegisterDao;
import org.serviceorder.database.entities.LogRegister;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogRegisterService {
    BaseDao<LogRegister> dao = new LogRegisterDao();

    public LogRegister add(LogRegisterDTO dto) throws SQLException {
        LogRegister log_register = LogRegister.convert(dto);

        return dao.insert(log_register);
    }

    public List<LogRegister> list() {
        List<LogRegister> log_registers = new ArrayList<>();
        ResultSet rs = dao.findAll();
        try {
            while(rs.next()) {
                LogRegister log_register = new LogRegister();
                log_register.setOperationResponse(rs.getString("operation_response"));
                log_register.setTime(rs.getString("time"));
                log_register.setOperation(rs.getString("operation"));

                log_registers.add(log_register);
            }
            return log_registers;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
