package com.github.mstepan.fsdoctor.size;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

public final class SizeCalculator extends SimpleFileVisitor<Path> {

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

    public long size(){
        return size;
    }
}
