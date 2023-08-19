package br.com;

import br.com.adapters.IMappingService;
import br.com.adapters.IReaderService;
import br.com.service.ExecuteService;
import br.com.service.MappingService;
import br.com.service.ReaderService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {
        IMappingService mappingService = new MappingService();
        IReaderService readerService = new ReaderService();

        ExecuteService executeService = new ExecuteService(readerService, mappingService);

        Semaphore semaphore = new Semaphore(3);

        ExecutorService mainExecutor = Executors.newCachedThreadPool();

        while (true) {
            try {
                semaphore.acquire();

                mainExecutor.submit(() -> {
                    try {
                        executeService.execute();
                    } finally {
                        semaphore.release();
                    }
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}