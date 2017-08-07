package com.jwa.amlmodel.code.generator;

import com.jwa.amlmodel.code.generator.service.CodegeneratorService;
import com.jwa.amlmodel.code.generator.service.CodegeneratorServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Path PATH_DOWLOADEDFILE = Paths.get("code-input/PushListener.aml");

    /**
     * Usage:
     * java Main
     * or
     * java Main <local path or URL to AML-model file> <local path to folder, in which the generated code should be placed>
     */
    public static void main(final String[] args) throws IllegalArgumentException {
        final Path amlmodelFile;
        final Path outputDirectory;
        if (args.length == 0) {
            try {
                final URL url = new URL("https://bitbucket.org/0xCAF3BAB3/pushlistener-amlmodel/raw/master/AMLmodel_v4/PushListener.aml");
                amlmodelFile = downloadFile(url, PATH_DOWLOADEDFILE);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            outputDirectory = Paths.get("code-output/");
        } else if (args.length == 2) {
            for(String arg : args) {
                if (arg == null || arg.isEmpty()) {
                    throw new IllegalArgumentException("Passed argument is null or invalid");
                }
            }
            final String pathToAmlmodelFile = args[0];
            if (isUrl(pathToAmlmodelFile)) {
                try {
                    final URL url = new URL(pathToAmlmodelFile);
                    amlmodelFile = downloadFile(url, PATH_DOWLOADEDFILE);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                amlmodelFile = Paths.get(pathToAmlmodelFile);
            }
            outputDirectory = Paths.get(args[1]);
        } else {
            throw new IllegalArgumentException("Wrong usage");
        }

        if (Files.notExists(amlmodelFile)) {
            throw new IllegalArgumentException("Path '" + amlmodelFile + "' not valid");
        }
        if (Files.notExists(outputDirectory)) {
            throw new IllegalArgumentException("Path '" + outputDirectory + "' not valid");
        }

        LOGGER.info("Generator is now running ...");
        try {
            new CodegeneratorService().generateCode(amlmodelFile, outputDirectory);
        } catch (CodegeneratorServiceException e) {
            LOGGER.error("Error: " + e.getMessage(), e);
            return;
        }
        LOGGER.info("Generator finished");

        /*
        // TODO:
        try {
            Files.deleteIfExists(PATH_DOWLOADEDFILE);
        } catch (IOException e) {
            LOGGER.warn("Deleting downloaded AML-model file failed: " + e.getMessage(), e);
        }
        */
    }

    private static Path downloadFile(final URL urlToFile, final Path file) throws IOException {
        if (Files.notExists(file)) {
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        }
        try (InputStream in = urlToFile.openStream()) {
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
        }
        return file;
    }

    private static boolean isUrl(final String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException ignored) {}
        return false;
    }
}
