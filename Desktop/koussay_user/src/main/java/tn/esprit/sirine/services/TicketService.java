package tn.esprit.sirine.services;

import tn.esprit.sirine.models.EtatTicket;
import tn.esprit.sirine.models.Ticket;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.imageio.ImageIO;

public class TicketService implements ITicketService {
    private final Connection conn;

    public TicketService(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(Ticket ticket) {
        String SQL = "INSERT INTO Ticket (dateEmission, codeQR, prix, etatTicket, nomPassager) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(ticket.getDateEmission()));
            stmt.setString(2, ticket.getCodeQR());
            stmt.setDouble(3, ticket.getPrix());
            stmt.setString(4, ticket.getEtatTicket().name());
            stmt.setString(5, ticket.getNomPassager());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    ticket.setIdTicket(rs.getInt(1));
                }
            }

            System.out.println("Ticket ajouté avec succès, idTicket: " + ticket.getIdTicket());
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du ticket : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Ticket ticket) {
        String SQL = "UPDATE Ticket SET dateEmission = ?, codeQR = ?, prix = ?, etatTicket = ?, nomPassager = ? WHERE idTicket = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(ticket.getDateEmission()));
            stmt.setString(2, ticket.getCodeQR());
            stmt.setDouble(3, ticket.getPrix());
            stmt.setString(4, ticket.getEtatTicket().name());
            stmt.setString(5, ticket.getNomPassager());
            stmt.setInt(6, ticket.getIdTicket());

            stmt.executeUpdate();
            System.out.println("Ticket mis à jour avec succès, idTicket: " + ticket.getIdTicket());
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du ticket : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int idTicket) {
        String SQL = "DELETE FROM Ticket WHERE idTicket = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, idTicket);
            stmt.executeUpdate();
            System.out.println("Ticket supprimé avec succès, idTicket: " + idTicket);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du ticket : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Ticket> getAll() {
        List<Ticket> tickets = new ArrayList<>();
        String SQL = "SELECT * FROM Ticket";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des tickets : " + e.getMessage());
            e.printStackTrace();
        }

        return tickets;
    }

    @Override
    public Ticket getById(int id) {
        String SQL = "SELECT * FROM Ticket WHERE idTicket = ?";
        Ticket ticket = null;

        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ticket = mapResultSetToTicket(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du ticket : " + e.getMessage());
            e.printStackTrace();
        }

        return ticket;
    }

    @Override
    public boolean validateTicket(int idTicket) {
        return updateEtatTicket(idTicket, EtatTicket.VALIDE);
    }

    @Override
    public boolean cancelTicket(int idTicket) {
        return updateEtatTicket(idTicket, EtatTicket.ANNULE);
    }

    private boolean updateEtatTicket(int idTicket, EtatTicket nouvelEtat) {
        String SQL = "UPDATE Ticket SET etatTicket = ? WHERE idTicket = ?";
        try (PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, nouvelEtat.name());
            stmt.setInt(2, idTicket);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de l'état du ticket : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String generateQRCode(int id) {
        String text = "QR-TICKET-" + id + "-" + System.currentTimeMillis();
        return generateQRCodeWithContent(text, "ticket_" + id);
    }

    public String generateTempQRCode(String content) {
        String fileName = "temp_" + content.hashCode();
        return generateQRCodeWithContent(content, fileName);
    }

    private String generateQRCodeWithContent(String content, String fileName) {
        int width = 300;
        int height = 300;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            String filePath = "qr_codes/qr_" + fileName + ".png";
            File outputfile = new File(filePath);
            outputfile.getParentFile().mkdirs();
            ImageIO.write(image, "png", outputfile);

            System.out.println("Fichier QR créé: " + filePath + ", Existe: " + outputfile.exists() + ", Contenu: " + content);
            return filePath;
        } catch (WriterException | IOException e) {
            System.out.println("Erreur lors de la génération du QR code pour " + fileName + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket(
                rs.getTimestamp("dateEmission").toLocalDateTime(),
                rs.getString("codeQR"),
                rs.getDouble("prix"),
                EtatTicket.valueOf(rs.getString("etatTicket").toUpperCase()),
                rs.getString("nomPassager")
        );
        ticket.setIdTicket(rs.getInt("idTicket"));
        return ticket;
    }

    public List<Ticket> getAllByNomPassager(String nomPassager) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM ticket WHERE nomPassager = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nomPassager);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des tickets par nomPassager: " + e.getMessage());
            e.printStackTrace();
        }
        return tickets;
    }

    public Ticket getTicketById(int id) {
        String query = "SELECT * FROM ticket WHERE idTicket = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du ticket par ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Ticket getByNomPassager(String nomPassager) {
        String query = "SELECT * FROM ticket WHERE nomPassager = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nomPassager);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du ticket par nomPassager: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}