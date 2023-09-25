package br.com.model;

import java.util.List;
import java.util.Map;

public class ExecutionResult {
    private final List<Long> memoryUsed;
    private final Map<Thread, Long> idleTimes;
    private final long executionTime;

    public ExecutionResult(List<Long> memoryUsed, Map<Thread, Long> idleTimes, long executionTime) {
        this.memoryUsed = memoryUsed;
        this.idleTimes = idleTimes;
        this.executionTime = executionTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }
    public List<Long> getMemoryUsed() {
        return memoryUsed;
    }

    public Map<Thread, Long> getIdleTimes() {
        return idleTimes;
    }

}
