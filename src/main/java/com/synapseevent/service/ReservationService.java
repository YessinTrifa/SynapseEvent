package com.synapseevent.service;

import com.synapseevent.dao.ReservationDAO;
import com.synapseevent.entities.Reservation;
import java.util.List;

public class ReservationService {
    private ReservationDAO dao = new ReservationDAO();

    public void add(Reservation reservation) { dao.create(reservation); }
    public List<Reservation> getAll() { return dao.readAll(); }
    public Reservation getById(Long id) { return dao.readById(id); }
    public void update(Reservation reservation) { dao.update(reservation); }
    public void delete(Long id) { dao.delete(id); }
}
