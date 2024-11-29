package com.github.mstepan.fsdoctor;

import picocli.CommandLine;

public class FsDoctorMain {

  public static void main(String[] args) {
    int exitCode = new CommandLine(new FolderSizeCalculationCommand()).execute(args);
    System.exit(exitCode);
  }
}
