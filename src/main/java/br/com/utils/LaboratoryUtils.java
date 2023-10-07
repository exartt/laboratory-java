package br.com.utils;

import br.com.model.DataCollected;
import br.com.model.ExecutionResult;
import br.com.service.ExecuteService;
import br.com.service.PersistData;

import java.math.BigInteger;
import java.util.List;

public class LaboratoryUtils {
    private static double sequentialExecutionTime = 0;
    private static int usedThread = 1;
    public static long getArithmeticMean(List<Long> numbers) {
        BigInteger ret = new BigInteger("0");

        for (Long num : numbers) {
            ret = ret.add(BigInteger.valueOf(num));
        }

        BigInteger size = BigInteger.valueOf(numbers.size());

        ret = ret.divide(size);

        return ret.longValueExact();
    }

    public static void persistData (long executionTime, long memoryUsedMedian, long idleThreadTimeMedian , boolean isSingleThread, long fullExecutionTime, long exeR, long exeW, boolean isValid) {
        DataCollected dataCollected = new DataCollected();

        dataCollected.setExecutionTime(executionTime);
        dataCollected.setMemory(memoryUsedMedian);
        dataCollected.setExeR(exeR);
        dataCollected.setExeW(exeW);
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
        dataCollected.setValid(isValid);
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

    public static long calculateAverageIdleTimeInMilliseconds(List<Long> idleTimes) {
        if (idleTimes.isEmpty()) {
            return 0L;
        }

        long size = idleTimes.size();
        Long sum = idleTimes.stream().reduce(0L, Long::sum);

        return sum / size;
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
            long memoryResult = LaboratoryUtils.getArithmeticMean(result.memoryUsed());
            long memoryResultW = LaboratoryUtils.getArithmeticMean(result.executionTimeR());
            long memoryResultR = LaboratoryUtils.getArithmeticMean(result.executionTimeW());
            long idleThreadTime = LaboratoryUtils.calculateAverageIdleTimeInMilliseconds(result.idleTimes());
            LaboratoryUtils.persistData(result.executionTime(), memoryResult, idleThreadTime, threadType.equals("singleThread"), executionTime, memoryResultR, memoryResultW, result.isValid().get());
            System.out.println("capture " + threadType + " nº " + controle + " collected successfully");
        }
    }
}

