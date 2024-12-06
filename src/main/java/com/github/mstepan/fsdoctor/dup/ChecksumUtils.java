package com.github.mstepan.fsdoctor.dup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public final class ChecksumUtils {

    private static final AtomicLong digestInstancesCount = new AtomicLong();

    private static final int DIGEST_POOL_CAPACITY = Runtime.getRuntime().availableProcessors();

    /** The MessageDigest class is NOT thread safe so we should do pooling */
    private static final BlockingQueue<MessageDigest> DIGEST_POOL =
            new ArrayBlockingQueue<>(DIGEST_POOL_CAPACITY);

    static {
        //        System.out.printf("DIGEST_POOL_CAPACITY: %d\n", DIGEST_POOL_CAPACITY);
        for (int i = 0; i < DIGEST_POOL_CAPACITY; ++i) {
            try {
                DIGEST_POOL.add(MessageDigest.getInstance("SHA256"));
                digestInstancesCount.incrementAndGet();
            } catch (NoSuchAlgorithmException ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }
    }

    private ChecksumUtils() {
        throw new AssertionError("Can't instantiate utility-ony class");
    }

    public static long digestInstancesCount() {
        return digestInstancesCount.get();
    }

    /**
     * Calculated SHA256 file checksum in HEX format. Should be similar to:
     *
     * <p>'shasum -a 256 <file_name>' =>
     * 'd65165279105ca6773180500688df4bdc69a2c7b771752f0a46ef120b7fd8ec3'
     */
    static String fileChecksum(Path filePath) {
        Objects.requireNonNull(filePath, "null 'filePath' parameter detected");

        if (!Files.exists(filePath)) {
            throw new IllegalStateException("File doesnt' exist '%s'".formatted(filePath));
        }

        try {
            byte[] digest = calculateDigest(filePath);
            return toHexStr(digest);
        } catch (InterruptedException interEx) {
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Can't calculate checksum for file: '%s'".formatted(filePath), ex);
        }
    }

    private static byte[] calculateDigest(Path filePath) throws InterruptedException, IOException {
        final MessageDigest fileDigest = DIGEST_POOL.take();
        try {
            final byte[] buffer = new byte[4096];

            try (InputStream fileIn = Files.newInputStream(filePath);
                    BufferedInputStream in = new BufferedInputStream(fileIn)) {
                int readBytesCnt;
                while ((readBytesCnt = in.read(buffer)) > 0) {
                    fileDigest.update(buffer, 0, readBytesCnt);
                }
            }

            byte[] digest = fileDigest.digest();
            assert digest.length > 0;
            return digest;
        } finally {
            DIGEST_POOL.add(fileDigest);
        }
    }

    private static final char[] HEX_CHARS =
            new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
            };

    private static String toHexStr(byte[] checksum) {
        assert checksum != null;
        StringBuilder res = new StringBuilder(checksum.length * 2);

        for (byte val : checksum) {
            res.append(HEX_CHARS[(val >> 4) & 0x0F]).append(HEX_CHARS[val & 0x0F]);
        }

        return res.toString();
    }
}
