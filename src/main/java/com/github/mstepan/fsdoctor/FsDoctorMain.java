package com.github.mstepan.fsdoctor;

import picocli.CommandLine;

public class FsDoctorMain {

    private static final int NANOS_IN_SECOND = 1_000_000_000;

    public static void main(String[] args) {

        long startTime = System.nanoTime();
        int exitCode = new CommandLine(new MainCliCommand()).execute(args);

        long endTime = System.nanoTime();
        System.out.printf(
                "Elapsed time: %.1f seconds\n", ((double) (endTime - startTime)) / NANOS_IN_SECOND);

        System.exit(exitCode);
    }
}
