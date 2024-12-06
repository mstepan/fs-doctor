package com.github.mstepan.fsdoctor.dup;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;

public final class DuplicatesFinder extends SimpleFileVisitor<Path> {

    private final boolean runInParallel;
    private final StructuredTaskScope<Void> scope;

    public DuplicatesFinder(boolean runInParallel, StructuredTaskScope<Void> scope) {
        this.runInParallel = runInParallel;
        this.scope = scope;
    }

    private final Map<String, Queue<Path>> filesChecksums = new ConcurrentHashMap<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (attr.isRegularFile() || attr.isOther()) {
            if (runInParallel) {
                scope.fork(new HandleFile(file));
            } else {
                new HandleFile(file).call();
            }
        }

        return CONTINUE;
    }

    private final class HandleFile implements Callable<Void> {

        final Path file;

        private HandleFile(Path file) {
            this.file = file;
        }

        @Override
        public Void call() {
            try {
                String fileChecksum = ChecksumUtils.fileChecksum(file);

                Queue<Path> filesWithSameChecksum =
                        filesChecksums.compute(
                                fileChecksum,
                                (notUsedKey, list) ->
                                        list == null ? new ConcurrentLinkedQueue<>() : list);
                filesWithSameChecksum.add(file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.printf("Failed to visit '%s'\n", file);
        return CONTINUE;
    }

    public Map<String, Queue<Path>> filesChecksums() {
        return filesChecksums;
    }

    public int duplicateGroupsCount() {
        int duplicatesGroupsCount = 0;

        for (Queue<Path> filesWithSameChecksum : filesChecksums.values()) {
            if (filesChecksums.size() > 1) {
                ++duplicatesGroupsCount;
            }
        }

        return duplicatesGroupsCount;
    }

    public int totalDuplicatesCount() {
        int duplicatesCount = 0;

        for (Queue<Path> filesWithSameChecksum : filesChecksums.values()) {
            if (filesChecksums.size() > 1) {
                duplicatesCount += filesWithSameChecksum.size();
            }
        }

        return duplicatesCount;
    }
}
