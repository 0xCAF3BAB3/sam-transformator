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
                amlmodelFile = IOUtils.downloadFile(url, PATH_DOWLOADEDFILE);
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
            if (IOUtils.isUrl(pathToAmlmodelFile)) {
                try {
                    final URL url = new URL(pathToAmlmodelFile);
                    amlmodelFile = IOUtils.downloadFile(url, PATH_DOWLOADEDFILE);
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

        if (!IOUtils.isValidFile(amlmodelFile)) {
            throw new IllegalArgumentException("File '" + amlmodelFile + "' doesn't exists or is not a valid file");
        }

        if (!IOUtils.isValidDirectory(outputDirectory)) {
            throw new IllegalArgumentException("Directory '" + outputDirectory + "' doesn't exists or is not a valid directory");
        }

        LOGGER.info("Generator is now running ...");
        try {
            new CodegeneratorService().generateCode(amlmodelFile, outputDirectory);
        } catch (CodegeneratorServiceException e) {
            LOGGER.error("Error: " + e.getMessage(), e);
            return;
        }
        LOGGER.info("Generator finished");
    }
}
