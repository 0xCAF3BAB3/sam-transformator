package com.jwa.amlmodel.code.generator;

import com.jwa.amlmodel.code.generator.generators.utils.IOUtils;
import com.jwa.amlmodel.code.generator.service.CodegeneratorService;
import com.jwa.amlmodel.code.generator.service.CodegeneratorServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws IllegalArgumentException {
        final Path amlmodelFile;
        final Path outputDirectory;
        if (args.length == 0) {
            amlmodelFile = downloadFile("https://bitbucket.org/0xCAF3BAB3/pushlistener-amlmodel/raw/master/AMLmodel_v4/PushListener.aml");
            outputDirectory = Paths.get("code-output/");
            try {
                IOUtils.createDirectoryIfNotExists(outputDirectory);
            } catch (IOException e) {
                throw new IllegalArgumentException("Creating output-directory failed: " + e.getMessage(), e);
            }
        } else if (args.length == 2) {
            for(String arg : args) {
                if (arg == null || arg.isEmpty()) {
                    throw new IllegalArgumentException("Passed argument is invalid");
                }
            }
            final String pathToAmlmodelFile = args[0];
            if (pathToAmlmodelFile.startsWith("http")) {
                amlmodelFile = downloadFile(pathToAmlmodelFile);
            } else {
                amlmodelFile = Paths.get(pathToAmlmodelFile);
            }
            outputDirectory = Paths.get(args[1]);
        } else {
            throw new IllegalArgumentException("Wrong usage");
        }

        if (!IOUtils.isValidFile(amlmodelFile)) {
            throw new IllegalArgumentException("File '" + amlmodelFile + "' doesn't exists or is not a valid file");
        }
        LOGGER.trace("File '" + amlmodelFile + "' is used as AML-model file");

        if (!IOUtils.isValidDirectory(outputDirectory)) {
            throw new IllegalArgumentException("Directory '" + outputDirectory + "' doesn't exists or is not a valid directory");
        }
        LOGGER.trace("Directory '" + outputDirectory + "' is used as output-directory");

        LOGGER.info("Generator is now running ...");
        try {
            new CodegeneratorService().generateCode(amlmodelFile, outputDirectory);
        } catch (CodegeneratorServiceException e) {
            LOGGER.error("Error: " + e.getMessage(), e);
            return;
        }
        LOGGER.info("Generator finished");
    }

    private static Path downloadFile(final String fileUrl) {
        try {
            final URL url = new URL(fileUrl);
            final Path file = Paths.get("code-input/PushListener.aml");
            final Path downloadedFile = IOUtils.downloadFile(url, file);
            LOGGER.trace("File '" + fileUrl + "' was downloaded to '" + file + "'");
            return downloadedFile;
        } catch (IOException e) {
            throw new IllegalArgumentException("Downloading file failed: " + e.getMessage(), e);
        }
    }
}
