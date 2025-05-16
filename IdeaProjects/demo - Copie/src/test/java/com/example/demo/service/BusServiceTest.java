package com.example.demo.service;

import com.example.demo.model.Bus;
import com.example.demo.model.Itineraire;
import com.example.demo.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusServiceTest {
    private BusService busService;

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private ItineraireService mockItineraireService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        busService = new BusService();
        
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
        when(mockResultSet.getInt("idBus")).thenReturn(1);
        when(mockResultSet.getString("modele")).thenReturn("Test Model");
        when(mockResultSet.getInt("capacite")).thenReturn(50);
        when(mockResultSet.getString("plaqueImmat")).thenReturn("ABC123");
        when(mockResultSet.getInt("idItineraire")).thenReturn(1);
        when(mockResultSet.getString("type")).thenReturn("Test Type");
        when(mockResultSet.getDouble("Tarif")).thenReturn(100.0);

        // Act
        List<Bus> buses = busService.getAll();

        // Assert
        assertNotNull(buses);
        assertEquals(1, buses.size());
        Bus bus = buses.get(0);
        assertEquals(1, bus.getIdBus());
        assertEquals("Test Model", bus.getModele());
        assertEquals(50, bus.getCapacite());
        assertEquals("ABC123", bus.getPlaqueImmat());
        assertEquals(1, bus.getIdItineraire());
        assertEquals("Test Type", bus.getType());
        assertEquals(100.0, bus.getTarif());
    }

    @Test
    void testGetById() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("idBus")).thenReturn(1);
        when(mockResultSet.getString("modele")).thenReturn("Test Model");
        when(mockResultSet.getInt("capacite")).thenReturn(50);
        when(mockResultSet.getString("plaqueImmat")).thenReturn("ABC123");
        when(mockResultSet.getInt("idItineraire")).thenReturn(1);
        when(mockResultSet.getString("type")).thenReturn("Test Type");
        when(mockResultSet.getDouble("Tarif")).thenReturn(100.0);

        // Act
        Bus bus = busService.getById(1);

        // Assert
        assertNotNull(bus);
        assertEquals(1, bus.getIdBus());
        assertEquals("Test Model", bus.getModele());
        assertEquals(50, bus.getCapacite());
        assertEquals("ABC123", bus.getPlaqueImmat());
        assertEquals(1, bus.getIdItineraire());
        assertEquals("Test Type", bus.getType());
        assertEquals(100.0, bus.getTarif());
    }

    @Test
    void testAdd() throws SQLException {
        // Arrange
        Bus bus = new Bus();
        bus.setModele("Test Model");
        bus.setCapacite(50);
        bus.setPlaqueImmat("ABC123");
        bus.setIdItineraire(1);
        bus.setType("Test Type");
        bus.setTarif(100.0);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
            .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Act
        busService.add(bus);

        // Assert
        verify(mockPreparedStatement).executeUpdate();
        assertEquals(1, bus.getIdBus());
    }

    @Test
    void testUpdate() throws SQLException {
        // Arrange
        Bus bus = new Bus();
        bus.setIdBus(1);
        bus.setModele("Updated Model");
        bus.setCapacite(60);
        bus.setPlaqueImmat("XYZ789");
        bus.setIdItineraire(2);
        bus.setType("Updated Type");
        bus.setTarif(150.0);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        busService.update(bus);

        // Assert
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testDelete() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        busService.delete(1);

        // Assert
        verify(mockPreparedStatement).executeUpdate();
    }
} 