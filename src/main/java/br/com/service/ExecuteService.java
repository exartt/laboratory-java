package br.com.service;

import br.com.adapters.IExecuteService;
import br.com.adapters.IMappingService;
import br.com.adapters.IReaderService;
import br.com.model.ProfessionalSalary;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExecuteService implements IExecuteService {

  private static final String filePath = "/home/leomoraes/Downloads/archive/Software_Professional_Salaries.csv";
  private final IReaderService readerService;
  private final IMappingService mappingService;

  public ExecuteService(IReaderService readerService, IMappingService mappingService) {
    this.readerService = readerService;
    this.mappingService = mappingService;
  }

  @Override
  public void execute() {
    try {
      List<Path> tempFiles = readerService.createBuckets(Paths.get(filePath));

      for (Path tempFile : tempFiles) {
        List<ProfessionalSalary> professionalSalaries = readerService.read(tempFile.toString());

        for (ProfessionalSalary professionalSalary : professionalSalaries) {
          int titleHash = mappingService.getTitleHash(professionalSalary.getJobTitle());
          int locationHash = mappingService.getLocationHash(professionalSalary.getLocation());

          professionalSalary.setJobTitle(String.valueOf(titleHash));
          professionalSalary.setLocation(String.valueOf(locationHash));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Erro ao executar o serviço", e);
    }
  }
}