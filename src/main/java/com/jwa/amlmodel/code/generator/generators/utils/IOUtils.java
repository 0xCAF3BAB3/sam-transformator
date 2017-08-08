package com.jwa.amlmodel.code.generator.generators.utils;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class IOUtils {
    private IOUtils() {}

    public static Path downloadFile(final URL urlFile, final Path destinationFile) throws IOException {
        if (urlFile == null) {
            throw new IllegalArgumentException("Passed url-file is null");
        }
        if (destinationFile == null) {
            throw new IllegalArgumentException("Passed destination-file is null");
        }
        if (Files.notExists(destinationFile)) {
            Files.createDirectories(destinationFile.getParent());
            Files.createFile(destinationFile);
        }
        try (InputStream in = urlFile.openStream()) {
            Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return destinationFile;
    }

    public static void clearDirectory(final Path directory) throws IOException {
        if (!isValidDirectory(directory)) {
            throw new IllegalArgumentException("Passed directory '" + directory + "' doesn't exists or is not valid");
        }
        FileUtils.cleanDirectory(directory.toFile());
    }

    public static boolean isValidDirectory(final Path directory) {
        return directory != null && Files.exists(directory) && Files.isDirectory(directory);
    }

    public static boolean isValidFile(final Path file) {
        return file != null && Files.exists(file) && Files.isRegularFile(file);
    }

    public static boolean isUrl(final String url) {
        if (url != null) {
            try {
                new URL(url);
                return true;
            } catch (MalformedURLException ignored) {}
        }
        return false;
    }
}
