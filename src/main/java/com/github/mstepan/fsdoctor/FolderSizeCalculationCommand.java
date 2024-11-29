package com.github.mstepan.fsdoctor;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** Something similar to: du -ms <dir_name> */
@Command(
    name = "fs-doctor",
    mixinStandardHelpOptions = true,
    version = "fs-doctor 1.0.0",
    description = "Calculate folder size and output to STDOUT.")
class FolderSizeCalculationCommand implements Callable<Integer> {

  @Parameters(index = "0", description = "The base folder.")
  private Path baseFolder;

  //  @Option(
  //      names = {"-a", "--algorithm"},
  //      description = "MD5, SHA-1, SHA-256, ...")
  //  private String algorithm = "SHA-256";

  @Override
  public Integer call() throws Exception {
    SizeCalculator sizeCalculator = new SizeCalculator();

    System.out.printf("Calculating size for folder '%s'\n", baseFolder);
    Files.walkFileTree(baseFolder, sizeCalculator);

    DirSize dirSize = new DirSize(sizeCalculator.size);
    System.out.printf("Folder size: %s\n", dirSize);
    return 0;
  }

  private record DirSize(long size) {

    private static final long KB = 1024;
    private static final long MB = 1024 * KB;
    private static final long GB = 1024 * MB;
    private static final long TB = 1024 * GB;
    private static final long PB = 1024 * TB;
    private static final long EB = 1024 * PB;

    @Override
    public String toString() {

      if (size < KB) {
        return "%d b".formatted(size);
      }
      if (size < MB) {
        return "%.1f Kb".formatted((double) size / KB);
      }

      if (size < GB) {
        return "%.1f Mb".formatted((double) size / MB);
      }

      if (size < TB) {
        return "%.1f Gb".formatted((double) size / GB);
      }

      if (size < PB) {
        return "%.1f Tb".formatted((double) size / TB);
      }

      if (size < EB) {
        return "%.1f Pb".formatted((double) size / PB);
      }

      return "%.1f Eb".formatted((double) size / EB);
    }
  }

  private static final class SizeCalculator extends SimpleFileVisitor<Path> {

    long size;

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
      if (attr.isRegularFile() || attr.isOther()) {
        size += attr.size();
      }

      return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
      System.err.printf("Failed to get stat for file '%s'\n", file);
      return CONTINUE;
    }
  }
}
