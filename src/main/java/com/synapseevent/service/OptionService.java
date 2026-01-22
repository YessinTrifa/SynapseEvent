package com.synapseevent.service;

import com.synapseevent.dao.OptionDAO;
import com.synapseevent.entities.Option;
import java.util.List;

public class OptionService {
    private OptionDAO dao = new OptionDAO();

    public void add(Option option) { dao.create(option); }
    public List<Option> getAll() { return dao.readAll(); }
    public Option getById(Long id) { return dao.readById(id); }
    public void update(Option option) { dao.update(option); }
    public void delete(Long id) { dao.delete(id); }
}
