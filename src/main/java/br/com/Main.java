package br.com;

import br.com.adapters.IMappingService;
import br.com.adapters.IFileService;
import br.com.service.ExecuteService;
import br.com.service.MappingService;
import br.com.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        IMappingService mappingService = new MappingService();
        IFileService readerService = new FileService();

        ExecuteService executeService = new ExecuteService(readerService, mappingService);

        Semaphore semaphore = new Semaphore(1);

        ExecutorService mainExecutor = Executors.newCachedThreadPool();

        long currentTimeMillis = System.currentTimeMillis();

        for (int controle = 0; controle < 10000; controle++) {
            LOGGER.info("Controle: {}", controle);
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
                LOGGER.error("Erro ao executar o serviço", e);
                Thread.currentThread().interrupt();
                break;
            }
        }

        long result = System.currentTimeMillis() - currentTimeMillis;
        LOGGER.info("Tempo de execução: {}", result);
    }
}