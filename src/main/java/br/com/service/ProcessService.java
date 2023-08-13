package br.com.service;

import br.com.adapters.IProcessService;

import java.nio.file.Path;
import java.util.List;

public class ProcessService implements IProcessService {
  private static final int NUM_OF_THREADS = Runtime.getRuntime().availableProcessors();
  @Override
  public void processBuckets(List<Path> tempFiles) {

  }
  @Override
  public void processSingleBucket(Path tempFile) {

  }
}
