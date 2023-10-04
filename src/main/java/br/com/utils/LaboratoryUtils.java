package br.com.utils;

import br.com.model.DataCollected;
import br.com.model.ExecutionResult;
import br.com.service.ExecuteService;
import br.com.service.PersistData;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class LaboratoryUtils {
    private static double sequentialExecutionTime = 0;
    private static int usedThread = 1;
    public static long getMedianMemory(List<Long> memoryUsedList) {
        BigInteger ret = new BigInteger("0");

        for (Long memUsed : memoryUsedList) {
            ret = ret.add(BigInteger.valueOf(memUsed));
        }

        BigInteger size = BigInteger.valueOf(memoryUsedList.size());

        ret = ret.divide(size);

        return ret.longValueExact();
    }

    public static void persistData (long executionTime, long memoryUsedMedian, long idleThreadTimeMedian , boolean isSingleThread, long fullExecutionTime, long memoryUsedMedianR, long memoryUsedMedianW) {
        DataCollected dataCollected = new DataCollected();

        dataCollected.setExecutionTime(executionTime);
        dataCollected.setMemory(memoryUsedMedian);
        dataCollected.setMemoryR(memoryUsedMedianR);
        dataCollected.setMemoryW(memoryUsedMedianW);
        dataCollected.setSingleThread(isSingleThread);

        if (!isSingleThread) {
            double speedUp = getSpeedup(executionTime);
            double efficiency = getEfficiency(speedUp);
            double overHead = getOverhead(executionTime);
            dataCollected.setSpeedup(speedUp);
            dataCollected.setEfficiency(efficiency);
            dataCollected.setOverHead(overHead);
        }
        dataCollected.setFullExecutionTime(fullExecutionTime);
        dataCollected.setIdleThreadTimeMedian(idleThreadTimeMedian);
        PersistData persistData = new PersistData();
        persistData.insert(dataCollected);
    }

    private static double getSpeedup(long parallelExecutionTime) {
        return getSequentialExecutionTime() / parallelExecutionTime;
    }

    private static double getEfficiency(double speedUp) {
        return speedUp / getUsedThread();
    }

    private static double getOverhead (long parallelExecutionTime) {
        return getSequentialExecutionTime() - parallelExecutionTime;
    }

    public static long calculateAverageIdleTimeInMilliseconds(Map<Thread, Long> idleTimes) {
        if (idleTimes.isEmpty()) {
            return 0L;
        }

        long totalIdleTime = idleTimes.values().stream().mapToLong(Long::longValue).sum();
        int totalIdleThreads = idleTimes.size();

        return totalIdleTime / totalIdleThreads;
    }

    public static void setSequentialExecutionTime () {
        setSequentialExecutionTime(new PersistData().getAverageExecutionTime());
    }

    public static void insertData () {
        PersistData.insertData();
    }

    public static int getUsedThread() {
        return usedThread;
    }

    public static void setUsedThread(int setThread) {
        usedThread = setThread;
    }

    public static double getSequentialExecutionTime() {
        return sequentialExecutionTime;
    }

    public static void setSequentialExecutionTime(double setSequencial) {
        sequentialExecutionTime = setSequencial;
    }
    public static void executeAndCollectData(ExecuteService executeService, String threadType, int numIterations) {
        for (int controle = 0; controle < numIterations; controle++) {
            System.out.println("Initiating " + threadType + " capture number: " + controle);
            long currentTimeMillis = System.currentTimeMillis();
            ExecutionResult result = executeService.execute();
            long executionTime = System.currentTimeMillis() - currentTimeMillis;
            result.memoryUsed().addAll(result.memoryUsedR()); // we use the same initialMem to collect the memoryusedR, it validates the collect.
            long memoryResult = LaboratoryUtils.getMedianMemory(result.memoryUsed());
            long memoryResultW = LaboratoryUtils.getMedianMemory(result.memoryUsedW());
            long memoryResultR = LaboratoryUtils.getMedianMemory(result.memoryUsedR());
            long idleThreadTime = LaboratoryUtils.calculateAverageIdleTimeInMilliseconds(result.idleTimes());
            LaboratoryUtils.persistData(result.executionTime(), memoryResult, idleThreadTime, threadType.equals("singleThread"), executionTime, memoryResultR, memoryResultW);
            System.out.println("capture " + threadType + " nº " + controle + " collected successfully");
        }
    }
}

