package com.synapseevent.service;

import com.synapseevent.dao.RoleDAO;
import com.synapseevent.entities.Role;
import java.util.List;

public class RoleService {
    private RoleDAO dao = new RoleDAO();

    public void add(Role role) { dao.create(role); }
    public List<Role> getAll() { return dao.readAll(); }
    public Role getById(Long id) { return dao.readById(id); }
    public void update(Role role) { dao.update(role); }
    public void delete(Long id) { dao.delete(id); }
}
