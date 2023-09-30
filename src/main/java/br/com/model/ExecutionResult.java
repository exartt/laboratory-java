package br.com.model;

import java.util.List;
import java.util.Map;

public record ExecutionResult(List<Long> memoryUsed, Map<Thread, Long> idleTimes, long executionTime) {

}
