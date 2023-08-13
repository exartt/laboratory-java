package br.com.adapters;

import java.nio.file.Path;
import java.util.List;

public interface IProcessService {
  void processBuckets(List<Path> tempFiles);
  void processSingleBucket (Path tempFile);
}
