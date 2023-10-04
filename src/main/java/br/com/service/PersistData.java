package br.com.service;

import br.com.adapters.IPersist;
import br.com.model.DataCollected;
import br.com.utils.DatabaseConnection;
import br.com.utils.LaboratoryUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.utils.LaboratoryUtils.getSequentialExecutionTime;
import static br.com.utils.LaboratoryUtils.getUsedThread;

public class PersistData implements IPersist {
    private static final String INSERT_SQL = "INSERT INTO j_data (m_thread, m_memory, m_memory_r, m_memory_w,  m_speed_up, m_efficiency, m_execution_time, m_overhead, m_iddle_thread, m_full_execution_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_SQL_OT = "INSERT INTO j_data_ot (m_thread, m_memory, m_memory_r, m_memory_w, m_execution_time, m_iddle_thread, m_full_execution_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_RECORD_PARAM = "INSERT INTO record_params (r_sequential_time, r_max_threads, r_lang) VALUES (?, ?, ?)";
    public void insert(DataCollected dataCollected) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertSql = dataCollected.isSingleThread() ? INSERT_SQL_OT : INSERT_SQL;

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                AtomicInteger count = new AtomicInteger(1);
                stmt.setLong(count.getAndIncrement(), LaboratoryUtils.getUsedThread());
                stmt.setLong(count.getAndIncrement(), dataCollected.getMemory());
                stmt.setLong(count.getAndIncrement(), dataCollected.getMemoryR());
                stmt.setLong(count.getAndIncrement(), dataCollected.getMemoryW());

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

    public static void insertData() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_RECORD_PARAM)) {
                AtomicInteger count = new AtomicInteger(1);
                stmt.setDouble(count.getAndIncrement(), getSequentialExecutionTime());
                stmt.setInt(count.getAndIncrement(), getUsedThread());
                stmt.setInt(count.get(), 1);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
