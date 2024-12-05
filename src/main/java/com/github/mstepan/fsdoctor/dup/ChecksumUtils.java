package com.github.mstepan.fsdoctor.dup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

final class ChecksumUtils {

    /** MessageDigest NOT thread safe so we should use ThreadLocal */
    private static final ThreadLocal<MessageDigest> LOCAL_DIGEST =
            ThreadLocal.withInitial(
                    () -> {
                        try {
                            return MessageDigest.getInstance("SHA256");
                        } catch (NoSuchAlgorithmException ex) {
                            throw new ExceptionInInitializerError(ex);
                        }
                    });

    private static final ThreadLocal<byte[]> LOCAL_BUF =
            ThreadLocal.withInitial(() -> new byte[4096]);

    private ChecksumUtils() {
        throw new AssertionError("Can't instantiate utility-ony class");
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
            final MessageDigest fileDigest = LOCAL_DIGEST.get();
            final byte[] buffer = LOCAL_BUF.get();

            try (InputStream fileIn = Files.newInputStream(filePath);
                    BufferedInputStream in = new BufferedInputStream(fileIn)) {
                int readBytesCnt;
                while ((readBytesCnt = in.read(buffer)) > 0) {
                    fileDigest.update(buffer, 0, readBytesCnt);
                }
            }

            byte[] checksum = fileDigest.digest();
            assert checksum.length > 0;
            return toHexStr(checksum);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Can't calculate checksum for file: '%s'".formatted(filePath), ex);
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
