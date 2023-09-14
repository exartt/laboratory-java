package br.com;

import br.com.adapters.IMappingService;
import br.com.adapters.IFileService;
import br.com.service.ExecuteService;
import br.com.service.MappingService;
import br.com.service.FileService;
import br.com.utils.LaboratoryUtils;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        IMappingService mappingService = new MappingService();
        IFileService readerService = new FileService();

        ExecuteService executeService = new ExecuteService(readerService, mappingService);

        for (int controle = 0; controle < 999; controle++) {
            long currentTimeMillis = System.currentTimeMillis();
            List<Long> memoryList = executeService.execute();
            long executionTime = System.currentTimeMillis() - currentTimeMillis;
            long memoryResult = LaboratoryUtils.getMedianMemory(memoryList);
            LaboratoryUtils.persistData(executionTime, memoryResult, true);
        }
    }
}