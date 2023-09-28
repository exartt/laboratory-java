package br.com;

import br.com.adapters.IMappingService;
import br.com.adapters.IFileService;
import br.com.model.ExecutionResult;
import br.com.service.ExecuteService;
import br.com.service.MappingService;
import br.com.service.FileService;
import br.com.utils.LaboratoryUtils;

public class Main {
    public static void main(String[] args) {
        IMappingService mappingService = new MappingService();
        IFileService readerService = new FileService();

        ExecuteService executeService = new ExecuteService(readerService, mappingService);

        executeService.execute();

        for (int controle = 0; controle < 100000; controle++) {
            System.out.println("Initiating capture number: " + controle);
            long currentTimeMillis = System.currentTimeMillis();
            ExecutionResult result = executeService.execute();
            long executionTime = System.currentTimeMillis() - currentTimeMillis;
            long memoryResult = LaboratoryUtils.getMedianMemory(result.getMemoryUsed());
            long idleThreadTime = LaboratoryUtils.calculateAverageIdleTimeInMilliseconds(result.getIdleTimes());
            LaboratoryUtils.persistData(result.getExecutionTime(), memoryResult, idleThreadTime, true, executionTime);
            System.out.println("capture nº " + controle + " collected successfully");
        }

        System.exit(200);
    }
}