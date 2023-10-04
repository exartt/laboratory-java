package br.com.service;

import br.com.adapters.IExecuteService;
import br.com.adapters.IMappingService;
import br.com.adapters.IFileService;
import br.com.model.ExecutionResult;
import br.com.model.ProfessionalSalary;
import br.com.utils.LaboratoryUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.utils.LaboratoryUtils.getUsedThread;

public class ExecuteService implements IExecuteService {
  private static final String filePath = "src/main/resources/Software_Professional_Salaries.csv";
  private final IFileService fileService;
  private final IMappingService mappingService;
  private final ExecutorService bucketExecutor = Executors.newFixedThreadPool(getUsedThread());

  public ExecuteService(IFileService fileService, IMappingService mappingService) {
    this.fileService = fileService;
    this.mappingService = mappingService;
  }

  @Override
  public ExecutionResult execute() {
    try {

      List<Path> tempFiles = fileService.createBuckets(Paths.get(filePath));
      CountDownLatch latch = new CountDownLatch(tempFiles.size());
      List<Path> processedFiles = Collections.synchronizedList(new ArrayList<>());
      List<Long> memoryUsed = Collections.synchronizedList(new ArrayList<>());
      List<Long> memoryUsedR = Collections.synchronizedList(new ArrayList<>());
      List<Long> memoryUsedW = Collections.synchronizedList(new ArrayList<>());

      Map<Thread, Long> idleTimes = new ConcurrentHashMap<>();

      long currentTimeMillis = System.currentTimeMillis();
      for (Path tempFile : tempFiles) {
        bucketExecutor.submit(() -> {
          long startTime = System.currentTimeMillis();
          try {

            long initialMemory = this.getMemoryNow();
            List<ProfessionalSalary> professionalSalaries = fileService.read(tempFile.toString());
            memoryUsedR.add(this.getMemoryUsed(initialMemory));

            for (ProfessionalSalary professionalSalary : professionalSalaries) {
              int titleHash = mappingService.getTitleHash(professionalSalary.getJobTitle());
              int locationHash = mappingService.getLocationHash(professionalSalary.getLocation());

              professionalSalary.setJobTitle(String.valueOf(titleHash));
              professionalSalary.setLocation(String.valueOf(locationHash));
            }

            memoryUsed.add(this.getMemoryUsed(initialMemory));

            long beforeMemoryRead = this.getMemoryNow();
            processedFiles.add(fileService.write(professionalSalaries));
            memoryUsedW.add(this.getMemoryUsed(beforeMemoryRead));

            memoryUsed.add(this.getMemoryUsed(initialMemory));

            this.deleteFile(tempFile);

          } catch (Exception e){
            throw new RuntimeException("Erro ao executar o serviço", e);
          } finally {
            long endTime = System.currentTimeMillis();
            long idleTime = endTime - startTime;

            if (idleTime > 0) {
              idleTimes.merge(Thread.currentThread(), idleTime, Long::sum);
            }
            latch.countDown();
          }
        });
      }

      latch.await();
      long executionTime = System.currentTimeMillis() - currentTimeMillis;

      processedFiles.forEach(this::deleteFile);
      return new ExecutionResult(memoryUsed, memoryUsedR, memoryUsedW, idleTimes, executionTime);
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