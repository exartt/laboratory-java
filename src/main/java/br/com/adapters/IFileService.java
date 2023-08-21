package br.com.adapters;

import br.com.model.ProfessionalSalary;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IFileService {
    List<Path> createBuckets(Path file) throws IOException;
    List<ProfessionalSalary> read(String path) throws Exception;
    Path write (List<ProfessionalSalary> professionalSalaries) throws IOException;
}
