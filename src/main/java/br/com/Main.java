package br.com;

import br.com.adapters.IMappingService;
import br.com.adapters.IFileService;
import br.com.service.ExecuteService;
import br.com.service.MappingService;
import br.com.service.FileService;
import br.com.utils.LaboratoryUtils;

import static br.com.utils.LaboratoryUtils.executeAndCollectData;

public class Main {
    public static void main(String[] args) {
        // Configuração de serviços
        IMappingService mappingService = new MappingService();
        IFileService readerService = new FileService();
        ExecuteService executeService = new ExecuteService(readerService, mappingService);
        int repeatNum = 100000;

        executeAndCollectData(executeService, "singleThread", repeatNum);

        LaboratoryUtils.setSequentialExecutionTime();
        LaboratoryUtils.setUsedThread(10);
        LaboratoryUtils.insertData();

        executeService = new ExecuteService(readerService, mappingService);

        executeAndCollectData(executeService, "multiThread", repeatNum);

        System.exit(0);
    }
}