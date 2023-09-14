package br.com.utils;

import br.com.model.DataCollected;
import br.com.service.PersistData;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

public class LaboratoryUtils {
    private static final long sequentialExecutionTime = 0;
    public static long getMedianMemory(List<Long> memoryUsedList) {
        BigInteger ret = new BigInteger("0");

        for (Long memUsed : memoryUsedList) {
            ret = ret.add(BigInteger.valueOf(memUsed));
        }

        BigInteger size = BigInteger.valueOf(memoryUsedList.size());

        ret = ret.divide(size);

        return ret.longValueExact();
    }

    public static void persistData (long executionTime, long memoryUsedMedian, boolean isSingleThread) {
        DataCollected dataCollected = new DataCollected();

        dataCollected.setExecutionTime(executionTime);
        dataCollected.setMemory(memoryUsedMedian);
        dataCollected.setSingleThread(isSingleThread);

        if (!isSingleThread) {
            double speedUp = getSpeedup(executionTime);
            double efficiency = getEfficiency(speedUp);
//            long overHead = getOverhead(executionTime);
            dataCollected.setSpeedup(speedUp);
            dataCollected.setEfficiency(efficiency);
//            dataCollected.setOverHead(overHead);
        }
        PersistData persistData = new PersistData();
        persistData.insert(dataCollected);
    }

    private static double getSpeedup(long parallelExecutionTime) {
        BigDecimal speedUp = new BigDecimal(sequentialExecutionTime);
        return speedUp.divide(BigDecimal.valueOf(parallelExecutionTime), 2, RoundingMode.HALF_UP).doubleValue();
    }

    private static double getEfficiency(double speedUp) {
        BigDecimal efficiency = new BigDecimal(speedUp);
        return efficiency.divide(BigDecimal.valueOf(50), 2, RoundingMode.HALF_UP).doubleValue();
    }

    private static long getOverhead (long parallelExecutionTime) {
        return sequentialExecutionTime - parallelExecutionTime;
    }
}

