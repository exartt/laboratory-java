package br.com.service;

import br.com.adapters.IPersist;
import br.com.model.DataCollected;
import br.com.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersistData implements IPersist {
    private static final String INSERT_SQL = "INSERT INTO j_data (m_memory, m_speed_up, m_efficiency, m_execution_time, m_is_single_thread) VALUES (?, ?, ?, ?, ?)";

    public void insert(DataCollected dataCollected) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setLong(1, dataCollected.getMemory());
            stmt.setDouble(2, dataCollected.getSpeedup());
            stmt.setDouble(3, dataCollected.getEfficiency());
            stmt.setLong(4, dataCollected.getExecutionTime());
            stmt.setBoolean(5, dataCollected.isSingleThread());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
