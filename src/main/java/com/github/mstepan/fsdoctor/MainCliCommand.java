package com.github.mstepan.fsdoctor;

import com.github.mstepan.fsdoctor.dup.ChecksumUtils;
import com.github.mstepan.fsdoctor.dup.DuplicatesFinder;
import com.github.mstepan.fsdoctor.size.DirSize;
import com.github.mstepan.fsdoctor.size.SizeCalculator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** Something similar to: du -ms <dir_name> */
@Command(
        name = "fs-doctor",
        mixinStandardHelpOptions = true,
        version = "fs-doctor 1.0.0",
        description = "Calculate folder size and output to STDOUT.")
class MainCliCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The base folder.")
    private Path baseFolder;

    @CommandLine.Option(
            names = {"-s", "--size"},
            description = "Calculate folder size with all the subfolders")
    private boolean calculateSize;

    @CommandLine.Option(
            names = {"-d", "--dup"},
            description = "Find duplicate files")
    private boolean findDuplicates;

    @CommandLine.Option(
            names = {"-p", "--parallel"},
            description = "Run in parallel using multiple threads")
    private boolean runInParallel;

    @Override
    public Integer call() throws Exception {

        if (calculateSize) {
            SizeCalculator sizeCalculator = new SizeCalculator();

            System.out.printf("Calculating size for folder '%s'\n", baseFolder);
            Files.walkFileTree(baseFolder, sizeCalculator);

            DirSize dirSize = new DirSize(sizeCalculator.size());
            System.out.printf("Folder size: %s\n", dirSize);
        }

        if (findDuplicates) {

            System.out.printf(
                    "Searching for duplicates inside '%s' (%s)\n",
                    baseFolder, (runInParallel ? "parallel" : "sequential"));

            try (var scope = new StructuredTaskScope<Void>()) {

                DuplicatesFinder duplicatesFinder = new DuplicatesFinder(runInParallel, scope);
                Files.walkFileTree(baseFolder, duplicatesFinder);
                scope.join();

                System.out.printf(
                        "Created MessagedDigest counts: %d\n",
                        ChecksumUtils.digestInstancesCount());

                System.out.printf(
                        "Duplicate groups count: %d\n", duplicatesFinder.duplicateGroupsCount());
                System.out.printf(
                        "Total duplicate files count: %d\n",
                        duplicatesFinder.totalDuplicatesCount());
                //
                //                for (Map.Entry<String, List<Path>> entry :
                //                        duplicatesFinder.filesChecksums().entrySet()) {
                //
                //                    String checksum = entry.getKey();
                //                    List<Path> duplicates = entry.getValue();
                //
                //                    if (duplicates.size() > 1) {
                //                        System.out.printf(
                //                                "<======= Checksum '%s', files count: %d\n",
                //                                checksum, duplicates.size());
                //
                //                        int idx = 1;
                //                        for (Path singleDuplicate : duplicates) {
                //                            System.out.printf("%d: %s\n", idx, singleDuplicate);
                //                            ++idx;
                //                        }
                //                        System.out.println();
                //                    }
                //                }
            }
        }

        return 0;
    }
}
