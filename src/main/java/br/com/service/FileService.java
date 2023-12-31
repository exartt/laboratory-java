package br.com.service;

import br.com.adapters.IFileService;
import br.com.model.ProfessionalSalary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileService implements IFileService {
    private static final int MAX_ROWS = 1000;
    private static final char QUOTE_CHAR = '\"';
    private static final char DELIMITER = ',';
    @Override
    public List<Path> createBuckets(Path pathFile) throws IOException {
        List<String> allLines = Files.readAllLines(pathFile);
        List<Path> tempFiles = new ArrayList<>();

        for (int i = 0; i < allLines.size(); i += MAX_ROWS) {
            List<String> partition = allLines.subList(i, Math.min(i + MAX_ROWS, allLines.size()));
            Path tempFile = Files.createTempFile("bucket", System.currentTimeMillis() + (i / MAX_ROWS) + ".csv");
            tempFile.toFile().deleteOnExit();
            Files.write(tempFile, partition, StandardOpenOption.WRITE);
            tempFiles.add(tempFile);
        }

        return tempFiles;
    }

    @Override
    public List<ProfessionalSalary> read(String partFilePath) throws Exception {
        List<ProfessionalSalary> professionalSalaryList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(partFilePath))) {
            String linha;

            br.readLine();

            while ((linha = br.readLine()) != null) {
                List<String> valores = splitCSVLine(linha);

                double rating = Double.parseDouble(valores.get(0).trim());
                String companyName = valores.get(1).trim();
                String jobTitle = valores.get(2).trim();
                double salary = Double.parseDouble(valores.get(3).trim());
                int reports = Integer.parseInt(valores.get(4).trim());
                String location = valores.get(5).trim();

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
        } catch (Exception e) {
            throw new Exception(e);
        }

      return professionalSalaryList;
    }

    @Override
    public Path write(List<ProfessionalSalary> professionalSalaries) throws IOException {
        Path tempFile = Files.createTempFile("bucket_result_", System.currentTimeMillis() + ".csv");

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
            writer.write("Rating;CompanyName;JobTitle;Salary;Reports;Location\n");

            for (ProfessionalSalary salary : professionalSalaries) {
                writer.write(String.format("%f;%s;%d;%f;%d;%d\n",
                        salary.getRating(),
                        salary.getCompanyName(),
                        Integer.valueOf(salary.getJobTitle()),
                        salary.getSalary(),
                        salary.getReports(),
                        Integer.valueOf(salary.getLocation())));
            }
        }

        return tempFile;
    }

    public static List<String> splitCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean insideQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (char currentChar : line.toCharArray()) {
            if (isQuoteChar(currentChar)) {
                insideQuotes = !insideQuotes;
                continue;
            }

            if (isDelimiterOutsideQuotes(currentChar, insideQuotes)) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(currentChar);
            }
        }
        fields.add(currentField.toString());

        return fields;
    }

    private static boolean isQuoteChar(char character) {
        return character == QUOTE_CHAR;
    }

    private static boolean isDelimiterOutsideQuotes(char character, boolean insideQuotes) {
        return character == DELIMITER && !insideQuotes;
    }

    @Override
    public boolean hasThousandLines(Path filePath, long size) {
        int lineCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            while (reader.readLine() != null) {
                lineCount++;
                if (lineCount > size) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return lineCount == size;
    }
}
