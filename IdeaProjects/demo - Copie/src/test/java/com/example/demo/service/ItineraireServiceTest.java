package com.example.demo.service;

import com.example.demo.model.Itineraire;
import com.example.demo.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItineraireServiceTest {
    private ItineraireService itineraireService;

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        itineraireService = new ItineraireService();
        
        // Mock DatabaseConnection
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        }
    }

    @Test
    void testGetAll() throws SQLException {
        // Arrange
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("idItin")).thenReturn(1);
        when(mockResultSet.getString("villeDepart")).thenReturn("Paris");
        when(mockResultSet.getString("villeArrivee")).thenReturn("Lyon");
        when(mockResultSet.getFloat("Distance")).thenReturn(465.0f);
        when(mockResultSet.getTime("dureeEstimee")).thenReturn(Time.valueOf("04:30:00"));
        when(mockResultSet.getString("arrets")).thenReturn("[\"Dijon\",\"Beaune\"]");

        // Act
        List<Itineraire> itineraires = itineraireService.getAll();

        // Assert
        assertNotNull(itineraires);
        assertEquals(1, itineraires.size());
        Itineraire itineraire = itineraires.get(0);
        assertEquals(1, itineraire.getIdItin());
        assertEquals("Paris", itineraire.getVilleDepart());
        assertEquals("Lyon", itineraire.getVilleArrivee());
        assertEquals(465.0f, itineraire.getDistance());
        assertEquals(LocalTime.of(4, 30), itineraire.getDureeEstimee());
        assertEquals(Arrays.asList("Dijon", "Beaune"), itineraire.getArrets());
    }

    @Test
    void testGetById() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("idItin")).thenReturn(1);
        when(mockResultSet.getString("villeDepart")).thenReturn("Paris");
        when(mockResultSet.getString("villeArrivee")).thenReturn("Lyon");
        when(mockResultSet.getFloat("Distance")).thenReturn(465.0f);
        when(mockResultSet.getTime("dureeEstimee")).thenReturn(Time.valueOf("04:30:00"));
        when(mockResultSet.getString("arrets")).thenReturn("[\"Dijon\",\"Beaune\"]");

        // Act
        Itineraire itineraire = itineraireService.getById(1);

        // Assert
        assertNotNull(itineraire);
        assertEquals(1, itineraire.getIdItin());
        assertEquals("Paris", itineraire.getVilleDepart());
        assertEquals("Lyon", itineraire.getVilleArrivee());
        assertEquals(465.0f, itineraire.getDistance());
        assertEquals(LocalTime.of(4, 30), itineraire.getDureeEstimee());
        assertEquals(Arrays.asList("Dijon", "Beaune"), itineraire.getArrets());
    }

    @Test
    void testAdd() throws SQLException {
        // Arrange
        Itineraire itineraire = new Itineraire();
        itineraire.setVilleDepart("Paris");
        itineraire.setVilleArrivee("Lyon");
        itineraire.setDistance(465.0f);
        itineraire.setDureeEstimee(LocalTime.of(4, 30));
        itineraire.setArrets(Arrays.asList("Dijon", "Beaune"));

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
            .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Act
        itineraireService.add(itineraire);

        // Assert
        verify(mockPreparedStatement).executeUpdate();
        assertEquals(1, itineraire.getIdItin());
    }

    @Test
    void testUpdate() throws SQLException {
        // Arrange
        Itineraire itineraire = new Itineraire();
        itineraire.setIdItin(1);
        itineraire.setVilleDepart("Paris");
        itineraire.setVilleArrivee("Marseille");
        itineraire.setDistance(750.0f);
        itineraire.setDureeEstimee(LocalTime.of(7, 30));
        itineraire.setArrets(Arrays.asList("Lyon", "Avignon"));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        itineraireService.update(itineraire);

        // Assert
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDelete() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        itineraireService.delete(1);

        // Assert
        verify(mockPreparedStatement).executeUpdate();
    }
} 