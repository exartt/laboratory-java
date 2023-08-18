package br.com;

import br.com.adapters.IMappingService;
import br.com.adapters.IReaderService;
import br.com.service.ExecuteService;
import br.com.service.MappingService;
import br.com.service.ReaderService;

public class Main {

    public static void main(String[] args) {
        IMappingService mappingService = new MappingService();
        IReaderService readerService = new ReaderService();

        ExecuteService executeService = new ExecuteService(readerService, mappingService);

        executeService.execute();
    }

}