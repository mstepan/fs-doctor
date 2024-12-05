package com.github.mstepan.fsdoctor.size;

public record DirSize(long size) {

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
