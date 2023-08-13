package br.com.adapters;

import br.com.model.ProfessionalSalary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IReaderService {
    List<Path> createBuckets(Path file) throws IOException;
    List<ProfessionalSalary> read(String path) throws FileNotFoundException;
}
