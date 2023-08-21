package br.com.service;

import br.com.adapters.IExecuteService;
import br.com.adapters.IMappingService;
import br.com.adapters.IFileService;
import br.com.model.ProfessionalSalary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuteService implements IExecuteService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteService.class);
  private static final String filePath = "src/main/resources/Software_Professional_Salaries.csv";
  private final IFileService fileService;
  private final IMappingService mappingService;
  private final ExecutorService bucketExecutor = Executors.newFixedThreadPool(5);


  public ExecuteService(IFileService fileService, IMappingService mappingService) {
    this.fileService = fileService;
    this.mappingService = mappingService;
  }

  @Override
  public void execute() {
    try {
//      LOGGER.info("Iniciando a execução do serviço...");
      List<Path> tempFiles = fileService.createBuckets(Paths.get(filePath));
      CountDownLatch latch = new CountDownLatch(tempFiles.size());
      List<Path> processedFiles = Collections.synchronizedList(new ArrayList<>());

      for (Path tempFile : tempFiles) {
        bucketExecutor.submit(() -> {
          try {
//            LOGGER.info("Processando arquivo temporário: {}", tempFile);
            List<ProfessionalSalary> professionalSalaries = fileService.read(tempFile.toString());

            for (ProfessionalSalary professionalSalary : professionalSalaries) {
              int titleHash = mappingService.getTitleHash(professionalSalary.getJobTitle());
              int locationHash = mappingService.getLocationHash(professionalSalary.getLocation());

              professionalSalary.setJobTitle(String.valueOf(titleHash));
              professionalSalary.setLocation(String.valueOf(locationHash));
            }

            processedFiles.add(fileService.write(professionalSalaries));

            this.deleteFile(tempFile);
//            LOGGER.info("Arquivo temporário {} excluído com sucesso", tempFile);

          } catch (Exception e) {
//            LOGGER.error("Erro ao processar o arquivo temporário {}", tempFile, e);

            throw new RuntimeException("Erro ao executar o serviço", e);
          } finally {
            latch.countDown();
          }
        });
      }

      latch.await();
      processedFiles.forEach(this::deleteFile);

      LOGGER.info("Todos os arquivos temporários foram processados.");
    } catch (Exception e) {
      throw new RuntimeException("Erro ao executar o serviço", e);
    }
  }

  private void deleteFile (Path path) {
    try {
      Files.delete(path);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}