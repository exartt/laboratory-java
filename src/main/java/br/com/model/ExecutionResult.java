package br.com.model;

import java.util.List;
import java.util.Map;

public class ExecutionResult {
    private final List<Long> memoryUsed;
    private final Map<Thread, Long> idleTimes;

    public ExecutionResult(List<Long> memoryUsed, Map<Thread, Long> idleTimes) {
        this.memoryUsed = memoryUsed;
        this.idleTimes = idleTimes;
    }

    public List<Long> getMemoryUsed() {
        return memoryUsed;
    }

    public Map<Thread, Long> getIdleTimes() {
        return idleTimes;
    }

}
