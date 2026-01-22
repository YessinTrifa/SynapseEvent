package com.synapseevent.service;

import com.synapseevent.dao.UserDAO;
import com.synapseevent.entities.User;
import java.sql.SQLException;
import java.util.List;

public class UserService implements IService<User> {
    private UserDAO dao = new UserDAO();

    @Override
    public boolean ajouter(User user) throws SQLException {
        return dao.create(user);
    }

    @Override
    public boolean supprimer(User user) throws SQLException {
        if (user.getId() != null) {
            return dao.delete(user.getId());
        }
        return false;
    }

    @Override
    public boolean modifier(User user) throws SQLException {
        return dao.update(user);
    }

    @Override
    public User findbyId(Long id) throws SQLException {
        return dao.readById(id);
    }

    @Override
    public List<User> readAll() throws SQLException {
        return dao.readAll();
    }
}