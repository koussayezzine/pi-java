package org.example.service;

import org.example.models.Ticket;

import java.awt.image.BufferedImage;
import java.util.List;

    public interface ITicketService {
        void add(Ticket t);
        void update(Ticket t);
        void delete(int idTicket);
        List<Ticket> getAll();
        Ticket getById(int id);
        boolean validateTicket(int idTicket);
        boolean cancelTicket(int idTicket);
        public String generateQRCode(int idTicket) ;    }


