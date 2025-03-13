package org.serviceorder.database.services;

import org.serviceorder.controller.dto.ServiceOrderDTO;
import org.serviceorder.database.dao.BaseDao;
import org.serviceorder.database.dao.ServiceOrderDao;
import org.serviceorder.database.entities.ServiceOrder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceOrderService {
    BaseDao<ServiceOrder> dao;

    public ServiceOrderService() {
        this.dao = new ServiceOrderDao();
    }

    public ServiceOrder add(ServiceOrderDTO dto) throws SQLException {
        ServiceOrder service_order = ServiceOrder.convert(dto);
        return dao.insert(service_order);
    }

    public List<ServiceOrder> list() {
        List<ServiceOrder> service_orders = new ArrayList<>();
        ResultSet rs = dao.findAll();
        try {
            while(rs.next()) {
                ServiceOrder service_order = new ServiceOrder();
                service_order.setName(rs.getString("name"));
                service_order.setDescription(rs.getString("description"));
                service_order.setCode(rs.getString("code"));
                service_order.setTime(rs.getString("time"));
                service_order.setId(rs.getInt("id"));

                service_orders.add(service_order);
            }
            return service_orders;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public boolean update (ServiceOrderDTO service_order_dto) throws SQLException {
        ServiceOrder service_order_converted = ServiceOrder.convert(service_order_dto);

        return dao.update(service_order_converted);
    }

    public boolean delete (ServiceOrderDTO service_order_dto) {
        ServiceOrder service_order = ServiceOrder.convert(service_order_dto);

        if(service_order.getId() != 0) {
            return dao.delete(service_order);
        }
        else return false;
    }

    public int get_registers_quantity() {
        return dao.getRegistersQuantity();
    }
}
