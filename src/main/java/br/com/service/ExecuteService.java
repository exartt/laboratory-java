package br.com.service;

import br.com.adapters.IExecuteService;
import br.com.adapters.IMappingService;
import br.com.adapters.IFileService;
import br.com.model.ProfessionalSalary;

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
  private static final String filePath = "C:\\Users\\leo_m\\OneDrive\\Documentos\\RunProject\\Software_Professional_Salaries.csv";
  private final IFileService fileService;
  private final IMappingService mappingService;
  private final ExecutorService bucketExecutor = Executors.newFixedThreadPool(1);


  public ExecuteService(IFileService fileService, IMappingService mappingService) {
    this.fileService = fileService;
    this.mappingService = mappingService;
  }

  @Override
  public List<Long> execute() {
    try {
      List<Path> tempFiles = fileService.createBuckets(Paths.get(filePath));
      CountDownLatch latch = new CountDownLatch(tempFiles.size());
      List<Path> processedFiles = Collections.synchronizedList(new ArrayList<>());
      List<Long> memoryUsed = Collections.synchronizedList(new ArrayList<>());

      for (Path tempFile : tempFiles) {
        bucketExecutor.submit(() -> {
          try {
            long initialMemory = getMemoryNow();
            List<ProfessionalSalary> professionalSalaries = fileService.read(tempFile.toString());
            memoryUsed.add(this.getMemoryUsed(initialMemory));
            for (ProfessionalSalary professionalSalary : professionalSalaries) {
              int titleHash = mappingService.getTitleHash(professionalSalary.getJobTitle());
              int locationHash = mappingService.getLocationHash(professionalSalary.getLocation());

              professionalSalary.setJobTitle(String.valueOf(titleHash));
              professionalSalary.setLocation(String.valueOf(locationHash));
            }
            memoryUsed.add(this.getMemoryUsed(initialMemory));

            processedFiles.add(fileService.write(professionalSalaries));
            memoryUsed.add(this.getMemoryUsed(initialMemory));

            this.deleteFile(tempFile);

          } catch (Exception e){
            throw new RuntimeException("Erro ao executar o serviço", e);
          } finally {
            latch.countDown();
          }
        });
      }


      latch.await();

      processedFiles.forEach(this::deleteFile);

      return memoryUsed;

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

  public long getMemoryNow() {
    return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
  }

  public long getMemoryUsed(long initialMemory) {
    return this.getMemoryNow() - initialMemory;
  }

}