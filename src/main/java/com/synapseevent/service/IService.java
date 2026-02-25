package com.synapseevent.service;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    List<T> readAll() throws SQLException;
    T findbyId(Long id) throws SQLException;
}
