package br.com.service;

import br.com.adapters.IExecuteService;
import br.com.adapters.IMappingService;
import br.com.adapters.IFileService;
import br.com.model.ExecutionResult;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static br.com.utils.LaboratoryUtils.getUsedThread;

public class ExecuteService implements IExecuteService {
//  private static final String filePath = "src/main/resources/Software_Professional_Salaries.csv";
  private static final String filePath = "/home/opc/laboratory-java/src/main/resources/Software_Professional_Salaries.csv";
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
      List<Long> executeTimeR = Collections.synchronizedList(new ArrayList<>());
      List<Long> executeTimeW = Collections.synchronizedList(new ArrayList<>());

      List<Long> idleTimes =  Collections.synchronizedList(new ArrayList<>());
      AtomicLong lastEndTime = new AtomicLong(0);
      AtomicBoolean isValid = new AtomicBoolean(true);

      long currentTimeMillis = System.currentTimeMillis();
      for (Path tempFile : tempFiles) {
        bucketExecutor.submit(() -> {
          long last = lastEndTime.get();
          long startTime = System.currentTimeMillis();
          long idleTime = startTime - last;

          if (lastEndTime.get() > 0) {
            idleTimes.add(idleTime);
          }

          try {

            long initialMemory = this.getMemoryNow();

            long getTime =  System.currentTimeMillis();
            List<ProfessionalSalary> professionalSalaries = fileService.read(tempFile.toString());
            executeTimeR.add(System.currentTimeMillis() - getTime);

            memoryUsed.add(this.getMemoryUsed(initialMemory));

            long size = professionalSalaries.size();

            for (ProfessionalSalary professionalSalary : professionalSalaries) {
              int titleHash = mappingService.getTitleHash(professionalSalary.getJobTitle());
              int locationHash = mappingService.getLocationHash(professionalSalary.getLocation());

              professionalSalary.setJobTitle(String.valueOf(titleHash));
              professionalSalary.setLocation(String.valueOf(locationHash));
            }

            memoryUsed.add(this.getMemoryUsed(initialMemory));

            getTime =  System.currentTimeMillis();
            Path pathFile = fileService.write(professionalSalaries);
            executeTimeW.add(System.currentTimeMillis() - getTime);
            processedFiles.add(pathFile);

            if (!fileService.hasThousandLines(pathFile, size + 1)) { // size +1 porque tem o header que eu escrevo a mão lá
              isValid.set(false);
            }

            memoryUsed.add(this.getMemoryUsed(initialMemory));

            this.deleteFile(tempFile);
          } catch (Exception e){
            throw new RuntimeException("Erro ao executar o serviço", e);
          } finally {
            long endTime = System.currentTimeMillis();
            lastEndTime.set(endTime);

            latch.countDown();
          }
        });
      }

      latch.await();
      long executionTime = System.currentTimeMillis() - currentTimeMillis;
      processedFiles.forEach(this::deleteFile);
      return new ExecutionResult(memoryUsed, executeTimeR, executeTimeW, idleTimes, executionTime, isValid);
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