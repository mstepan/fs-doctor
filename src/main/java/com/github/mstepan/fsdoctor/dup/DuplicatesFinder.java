package com.github.mstepan.fsdoctor.dup;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.FileVisitResult.CONTINUE;

public final class DuplicatesFinder extends SimpleFileVisitor<Path> {

    private final Map<String, List<Path>> filesChecksums = new HashMap<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (attr.isRegularFile() || attr.isOther()) {
            String fileChecksum = ChecksumUtils.fileChecksum(file);

            List<Path> filesWithSameChecksum =
                    filesChecksums.compute(
                            fileChecksum,
                            (notUsedKey, list) -> list == null ? new ArrayList<>() : list);

            filesWithSameChecksum.add(file);
            //                System.out.printf("file: %s => checksum: %s\n", file,
            // fileChecksum);
        }

        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.printf("Failed to visit '%s'\n", file);
        return CONTINUE;
    }

    public Map<String, List<Path>> filesChecksums() {
        return filesChecksums;
    }
}
