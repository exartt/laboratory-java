package br.com.service;

import br.com.adapters.IExecuteService;
import br.com.adapters.IMappingService;
import br.com.adapters.IReaderService;
import br.com.model.ProfessionalSalary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuteService implements IExecuteService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteService.class);
  private static final String filePath = "src/main/resources/Software_Professional_Salaries.csv";
  private final IReaderService readerService;
  private final IMappingService mappingService;
  private final ExecutorService bucketExecutor = Executors.newFixedThreadPool(5);


  public ExecuteService(IReaderService readerService, IMappingService mappingService) {
    this.readerService = readerService;
    this.mappingService = mappingService;
  }

  @Override
  public void execute() {
    try {
      LOGGER.info("Iniciando a execução do serviço...");
      List<Path> tempFiles = readerService.createBuckets(Paths.get(filePath));
      CountDownLatch latch = new CountDownLatch(tempFiles.size());

      for (Path tempFile : tempFiles) {
        bucketExecutor.submit(() -> {
          try {
            LOGGER.info("Processando arquivo temporário: {}", tempFile);
            List<ProfessionalSalary> professionalSalaries = readerService.read(tempFile.toString());

            for (ProfessionalSalary professionalSalary : professionalSalaries) {
              int titleHash = mappingService.getTitleHash(professionalSalary.getJobTitle());
              int locationHash = mappingService.getLocationHash(professionalSalary.getLocation());

              professionalSalary.setJobTitle(String.valueOf(titleHash));
              professionalSalary.setLocation(String.valueOf(locationHash));
            }

            Files.delete(tempFile);
            LOGGER.info("Arquivo temporário {} excluído com sucesso", tempFile);

          } catch (Exception e) {
            LOGGER.error("Erro ao processar o arquivo temporário {}", tempFile, e);
            throw new RuntimeException("Erro ao executar o serviço", e);
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();
      LOGGER.info("Todos os arquivos temporários foram processados.");
    } catch (Exception e) {
      throw new RuntimeException("Erro ao executar o serviço", e);
    }
  }
}