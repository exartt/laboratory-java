package br.com.utils;

import br.com.model.DataCollected;
import br.com.service.PersistData;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class LaboratoryUtils {
    private static final long sequentialExecutionTime = 270;
    private static final int thread_used = 10;
    public static long getMedianMemory(List<Long> memoryUsedList) {
        BigInteger ret = new BigInteger("0");

        for (Long memUsed : memoryUsedList) {
            ret = ret.add(BigInteger.valueOf(memUsed));
        }

        BigInteger size = BigInteger.valueOf(memoryUsedList.size());

        ret = ret.divide(size);

        return ret.longValueExact();
    }

    public static void persistData (long executionTime, long memoryUsedMedian, long idleThreadTimeMedian , boolean isSingleThread) {
        DataCollected dataCollected = new DataCollected();

        dataCollected.setExecutionTime(executionTime);
        dataCollected.setMemory(memoryUsedMedian);
        dataCollected.setSingleThread(isSingleThread);

        if (!isSingleThread) {
            double speedUp = getSpeedup(executionTime);
            double efficiency = getEfficiency(speedUp);
            long overHead = getOverhead(executionTime);
            dataCollected.setSpeedup(speedUp);
            dataCollected.setEfficiency(efficiency);
            dataCollected.setOverHead(overHead);
        }
        dataCollected.setIdleThreadTimeMedian(idleThreadTimeMedian);
        PersistData persistData = new PersistData();
        persistData.insert(dataCollected);
    }

    private static double getSpeedup(long parallelExecutionTime) {
        BigDecimal speedUp = new BigDecimal(sequentialExecutionTime);
        return speedUp.divide(BigDecimal.valueOf(parallelExecutionTime), 2, RoundingMode.HALF_UP).doubleValue();
    }

    private static double getEfficiency(double speedUp) {
        return speedUp / thread_used;
    }

    private static long getOverhead (long parallelExecutionTime) {
        return sequentialExecutionTime - parallelExecutionTime;
    }

    public static long calculateAverageIdleTimeInMilliseconds(Map<Thread, Long> idleTimes) {
        if (idleTimes.isEmpty()) {
            return 0L;
        }

        long totalIdleTime = idleTimes.values().stream().mapToLong(Long::longValue).sum();
        int totalIdleThreads = idleTimes.size();

        return totalIdleTime / totalIdleThreads;
    }
}

