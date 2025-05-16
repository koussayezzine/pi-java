package tn.esprit.sirine.services;

import tn.esprit.sirine.models.Reservation;

import java.util.List;

public interface IReservationService {
    void add(Reservation r);
    void update(Reservation r);
    void delete(Reservation r);
    List<Reservation> readAll();
    List<Reservation> getAll();
    Reservation getById(int id);
    boolean confirmReservation(int id);
    boolean cancelReservation(int id);
}
