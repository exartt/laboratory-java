package br.com.model;

public class DataCollected {
    public DataCollected() {
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public double getOverHead() {
        return overHead;
    }

    public void setOverHead(double overHead) {
        this.overHead = overHead;
    }
    public double getSpeedup() {
        return speedup;
    }

    public void setSpeedup(double speedup) {
        this.speedup = speedup;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public boolean isSingleThread() {
        return isSingleThread;
    }

    public void setSingleThread(boolean singleThread) {
        isSingleThread = singleThread;
    }
    public long getIdleThreadTimeMedian() {
        return idleThreadTimeMedian;
    }

    public void setIdleThreadTimeMedian(long idleThreadTimeMedian) {
        this.idleThreadTimeMedian = idleThreadTimeMedian;
    }
    public long getFullExecutionTime() {
        return fullExecutionTime;
    }

    public void setFullExecutionTime(long fullExecutionTime) {
        this.fullExecutionTime = fullExecutionTime;
    }
    public long getExeR() {
        return exeR;
    }

    public void setExeR(long exeR) {
        this.exeR = exeR;
    }

    public long getExeW() {
        return exeW;
    }

    public void setExeW(long exeW) {
        this.exeW = exeW;
    }
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private long memory = 0;
    private double speedup = 0;
    private double efficiency = 0;
    private long executionTime = 0;
    private double overHead = 0;
    private long idleThreadTimeMedian = 0;
    private boolean isSingleThread = false;
    private long fullExecutionTime = 0;
    private long exeR = 0;
    private long exeW = 0;
    private boolean valid;
}
