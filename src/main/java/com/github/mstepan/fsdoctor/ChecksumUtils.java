package com.github.mstepan.fsdoctor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public final class ChecksumUtils {

    private static final ThreadLocal<MessageDigest> LOCAL_DIGEST =
            new ThreadLocal<>() {
                @Override
                protected MessageDigest initialValue() {
                    try {
                        return MessageDigest.getInstance("SHA256");
                    } catch (NoSuchAlgorithmException ex) {
                        throw new ExceptionInInitializerError(ex);
                    }
                }
            };

    private ChecksumUtils() {
        throw new AssertionError("Can't instantiate utility-ony class");
    }

    /**
     * Calculated SHA256 file checksum in HEX format. Should be similar to:
     *
     * <p>'shasum -a 256 <file_name>' =>
     * 'd65165279105ca6773180500688df4bdc69a2c7b771752f0a46ef120b7fd8ec3'
     */
    public static String fileChecksum(Path filePath) {
        Objects.requireNonNull(filePath, "null 'filePath' parameter detected");

        if (!Files.exists(filePath)) {
            throw new IllegalStateException("File doesnt' exist '%s'".formatted(filePath));
        }

        try {
            byte[] checksum = LOCAL_DIGEST.get().digest(Files.readAllBytes(filePath));
            assert checksum.length > 0;
            return toHexStr(checksum);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Can't calculate checksum from file: '%s'".formatted(filePath), ex);
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
