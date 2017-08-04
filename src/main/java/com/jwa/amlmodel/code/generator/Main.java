package com.jwa.amlmodel.code.generator;

import com.jwa.amlmodel.code.generator.generators.CodeGeneratorException;
import com.jwa.amlmodel.code.generator.generators.CodeGeneratorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String PATH_CODEINPUT = "code-input/";
    private static final String PATH_CODEOUTPUT = "code-output/";
    private static final String PATH_AMLFILE = PATH_CODEINPUT + "PushListener.aml";

    public static void main(final String[] args) {
        LOGGER.info("Generator is now running ...");
        // final String generatedCode = generateCode(amlModel);
        // exportCode(generatedCode, PATH_CODEOUTPUT);
        File amlmodelFile = new File(PATH_AMLFILE);
        Path outputDirectory = Paths.get(PATH_CODEOUTPUT);
        try {
            new CodeGeneratorService(amlmodelFile, outputDirectory).generateCode();
        } catch (CodeGeneratorException e) {
            LOGGER.error("Error: " + e.getMessage(), e);
        }
        LOGGER.info("Generator finished");
    }

    /*
    // TODO: remove me
    private static void exportCode(final String code, final String pathToOutputFolder) throws IOException {
        final File file = new File(pathToOutputFolder + "Main.java");
        file.getParentFile().mkdirs();
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(code);
        }
    }
    */
}
