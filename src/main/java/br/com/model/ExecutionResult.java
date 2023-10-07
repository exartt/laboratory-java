package br.com.model;

import java.util.List;

public record ExecutionResult(List<Long> memoryUsed,
                              List<Long> executionTimeR,
                              List<Long> executionTimeW,
                              List<Long> idleTimes,
                              long executionTime,
                              java.util.concurrent.atomic.AtomicBoolean isValid) {}
