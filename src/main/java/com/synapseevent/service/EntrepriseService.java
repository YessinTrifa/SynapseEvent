package com.synapseevent.service;

import com.synapseevent.dao.EntrepriseDAO;
import com.synapseevent.entities.Entreprise;
import java.util.List;

public class EntrepriseService {
    private EntrepriseDAO dao = new EntrepriseDAO();

    public void add(Entreprise entreprise) { dao.create(entreprise); }
    public List<Entreprise> getAll() { return dao.readAll(); }
    public Entreprise getById(Long id) { return dao.readById(id); }
    public void update(Entreprise entreprise) { dao.update(entreprise); }
    public void delete(Long id) { dao.delete(id); }
}
