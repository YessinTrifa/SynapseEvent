package com.synapseevent.service;

import com.synapseevent.dao.ParticipantDAO;
import com.synapseevent.entities.Participant;
import java.util.List;

public class ParticipantService {
    private ParticipantDAO dao = new ParticipantDAO();

    public void add(Participant participant) { dao.create(participant); }
    public List<Participant> getAll() { return dao.readAll(); }
    public Participant getById(Long id) { return dao.readById(id); }
    public void update(Participant participant) { dao.update(participant); }
    public void delete(Long id) { dao.delete(id); }
}
