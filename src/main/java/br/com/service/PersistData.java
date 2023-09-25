package br.com.service;

import br.com.adapters.IPersist;
import br.com.model.DataCollected;
import br.com.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersistData implements IPersist {
    private static final String INSERT_SQL = "INSERT INTO j_data (m_memory, m_speed_up, m_efficiency, m_execution_time, m_overhead, m_iddle_thread) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String INSERT_SQL_OT = "INSERT INTO j_data_ot (m_memory, m_execution_time, m_iddle_thread) VALUES (?, ?, ?)";

    public void insert(DataCollected dataCollected) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertSql = dataCollected.isSingleThread() ? INSERT_SQL_OT : INSERT_SQL;

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setLong(1, dataCollected.getMemory());

                if (dataCollected.isSingleThread()) {
                    stmt.setLong(2, dataCollected.getExecutionTime());
                    stmt.setDouble(3, dataCollected.getIdleThreadTimeMedian());
                } else {
                    stmt.setDouble(2, dataCollected.getSpeedup());
                    stmt.setDouble(3, dataCollected.getEfficiency());
                    stmt.setLong(4, dataCollected.getExecutionTime());
                    stmt.setDouble(5, dataCollected.getOverHead());
                    stmt.setDouble(6, dataCollected.getIdleThreadTimeMedian());
                }

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
