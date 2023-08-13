package br.com.service;

import br.com.adapters.IReaderService;
import br.com.model.ProfessionalSalary;
import br.com.service.enums.JobTitleEnum;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ReaderService implements IReaderService {
    private static final int MAX_ROWS = 1000;
    private static final String SEPARATOR = ",";
    @Override
    public List<Path> createBuckets(Path pathFile) throws IOException {
        List<String> allLines = Files.readAllLines(pathFile);
        List<Path> tempFiles = new ArrayList<>();

        for (int i = 0; i < allLines.size(); i += MAX_ROWS) {
            List<String> partition = allLines.subList(i, Math.min(i + MAX_ROWS, allLines.size()));
            Path tempFile = Files.createTempFile("bucket", (i / MAX_ROWS) + ".csv");
            tempFile.toFile().deleteOnExit();
            Files.write(tempFile, partition, StandardOpenOption.WRITE);
            tempFiles.add(tempFile);
        }

        return tempFiles;
    }

    @Override
    public List<ProfessionalSalary> read(String partFilePath) throws FileNotFoundException {
        List<ProfessionalSalary> professionalSalaryList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(partFilePath))) {
            String linha;

            br.readLine();

            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(SEPARATOR);

                if (valores.length != 2) {
                    continue;
                }

                double rating = Double.parseDouble(valores[0].trim());
                String companyName = valores[1].trim();
                JobTitleEnum jobTitle = JobTitleEnum.valueOf(valores[2].trim());
                double salary = Double.parseDouble(valores[3].trim());
                int reports = Integer.parseInt(valores[4].trim());
                String location = valores[5].trim();

                ProfessionalSalary professionalSalary = new ProfessionalSalary();
                professionalSalary.setRating(rating);
                professionalSalary.setCompanyName(companyName);
                professionalSalary.setJobTitle(jobTitle);
                professionalSalary.setSalary(salary);
                professionalSalary.setReports(reports);
                professionalSalary.setLocation(location);

                professionalSalaryList.add(professionalSalary);
            }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

      return professionalSalaryList;
    }

}
