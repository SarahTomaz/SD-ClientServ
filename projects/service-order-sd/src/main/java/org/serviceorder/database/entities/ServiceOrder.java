package org.serviceorder.database.entities;

import org.serviceorder.controller.dto.ServiceOrderDTO;

public class ServiceOrder {
    private String code, description, name, cacheId, time;
    private int id;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ServiceOrder convert(ServiceOrderDTO dto) {
        ServiceOrder service_order = new ServiceOrder();
        service_order.setId(dto.getId());
        service_order.setCode(dto.getCode());
        service_order.setDescription(dto.getDescription());
        service_order.setName(dto.getName());
        return service_order;
    }
}
