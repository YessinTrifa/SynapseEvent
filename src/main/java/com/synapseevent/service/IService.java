package com.synapseevent.service;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {
    boolean ajouter(T event) throws SQLException;
    List<T> readAll() throws SQLException;
    T findbyId(Long id) throws SQLException;
    boolean modifier(T event) throws SQLException;
    boolean supprimer(T event) throws SQLException;
}
