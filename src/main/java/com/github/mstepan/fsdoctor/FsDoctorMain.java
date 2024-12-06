package com.github.mstepan.fsdoctor;

import picocli.CommandLine;

/*
Sequential.
Searching for duplicates inside 'C:\Users\maksym\repo-py' (sequential)
Created MessagedDigest counts: 1
Duplicate groups count: 127497
Total duplicate files count: 185998
Elapsed time: 34.1 seconds

Concurrent.

Searching for duplicates inside 'C:\Users\maksym\repo-py' (parallel)
Created MessagedDigest counts: 185998
Duplicate groups count: 127497
Total duplicate files count: 185998
Elapsed time: 7.7 seconds

*/
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
