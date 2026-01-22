package com.synapseevent.service;

import com.synapseevent.dao.EventDAO;
import com.synapseevent.entities.Event;
import java.util.List;

public class EventService {
    private EventDAO dao = new EventDAO();

    public void add(Event event) { dao.create(event); }
    public List<Event> getAll() { return dao.readAll(); }
    public Event getById(Long id) { return dao.readById(id); }
    public void update(Event event) { dao.update(event); }
    public void delete(Long id) { dao.delete(id); }
}
