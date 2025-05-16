package org.example.service;

import org.example.models.Reservation;

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
