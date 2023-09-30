package br.com.service;

import br.com.adapters.IPersist;
import br.com.model.DataCollected;
import br.com.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class PersistData implements IPersist {
    private static final String INSERT_SQL = "INSERT INTO j_data (m_memory, m_speed_up, m_efficiency, m_execution_time, m_overhead, m_iddle_thread, m_full_execution_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_SQL_OT = "INSERT INTO j_data_ot (m_memory, m_execution_time, m_iddle_thread, m_full_execution_time) VALUES (?, ?, ?, ?)";

    public void insert(DataCollected dataCollected) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertSql = dataCollected.isSingleThread() ? INSERT_SQL_OT : INSERT_SQL;

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                AtomicInteger count = new AtomicInteger(1);
                stmt.setLong(count.getAndIncrement(), dataCollected.getMemory());

                if (dataCollected.isSingleThread()) {
                    stmt.setLong(count.getAndIncrement(), dataCollected.getExecutionTime());
                    stmt.setDouble(count.getAndIncrement(), dataCollected.getIdleThreadTimeMedian());
                } else {
                    stmt.setDouble(count.getAndIncrement(), dataCollected.getSpeedup());
                    stmt.setDouble(count.getAndIncrement(), dataCollected.getEfficiency());
                    stmt.setLong(count.getAndIncrement(), dataCollected.getExecutionTime());
                    stmt.setDouble(count.getAndIncrement(), dataCollected.getOverHead());
                    stmt.setDouble(count.getAndIncrement(), dataCollected.getIdleThreadTimeMedian());
                }

                stmt.setLong(count.get(), dataCollected.getFullExecutionTime());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getAverageExecutionTime() {
        String query = "SELECT AVG(m_execution_time) FROM j_data_ot";
        double averageExecutionTime = 0.0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                averageExecutionTime = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return averageExecutionTime;
    }
}
