package com.jwa.amlmodel.code.generator;

import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String PATH_FOLDER_CODEINPUT = "code-input/";
    private static final String PATH_FILE_AMLMODEL = PATH_FOLDER_CODEINPUT + "PushListener.aml";
    private static final String PATH_FOLDER_CODEOUTPUT = "code-output/";

    public static void main(final String[] args) {
        LOGGER.info("Generator is now running ...");
        final Path outputDirectory = Paths.get(PATH_FOLDER_CODEOUTPUT);
        try {
            new CodegeneratorService(getAmlmodelFile(), outputDirectory).generateCode();
        } catch (CodegeneratorException | IOException e) {
            LOGGER.error("Error: " + e.getMessage(), e);
            return;
        }
        LOGGER.info("Generator finished");
    }

    private static File getAmlmodelFile() throws IOException {
        final URL url = new URL("https://bitbucket.org/0xCAF3BAB3/pushlistener-amlmodel/raw/master/AMLmodel_v4/PushListener.aml");
        final File file = new File(PATH_FILE_AMLMODEL);
        file.getParentFile().mkdirs();
        FileUtils.copyURLToFile(url, file);
        return file;
    }
}
