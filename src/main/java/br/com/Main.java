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
        int repeatNum = 10000;

        for (int i = 1; i <= 10; i++) {
            String type = "singleThread";
            if (i > 1) {
                LaboratoryUtils.setSequentialExecutionTime();
                type = "multiThread";
            }
            LaboratoryUtils.setUsedThread(i);
            LaboratoryUtils.insertData();
            ExecuteService executeService = new ExecuteService(readerService, mappingService);
            executeAndCollectData(executeService, type, repeatNum);
        }

        System.exit(0);
    }
}