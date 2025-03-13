package org.serviceorder.database.dao;

import java.sql.ResultSet;

public interface BaseDao<entity> {

    public abstract entity insert(entity e);
    public boolean delete(entity e);
    public boolean update(entity e);
    public entity findById(entity e);
    public ResultSet findAll();
    public Integer getRegistersQuantity();
}
