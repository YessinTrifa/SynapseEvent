package com.synapseevent.service;

import com.synapseevent.dao.PaymentDAO;
import com.synapseevent.entities.Payment;
import java.util.List;

public class PaymentService {
    private PaymentDAO dao = new PaymentDAO();

    public void add(Payment payment) { dao.create(payment); }
    public List<Payment> getAll() { return dao.readAll(); }
    public Payment getById(Long id) { return dao.readById(id); }
    public void update(Payment payment) { dao.update(payment); }
    public void delete(Long id) { dao.delete(id); }
}
