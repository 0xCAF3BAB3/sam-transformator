package com.jwa.pushlistener.code.generator;

import org.cdlflex.models.CAEX.CAEXFile;
import org.cdlflex.models.CAEX.DocumentRoot;
import org.cdlflex.models.CAEX.util.AmlDeserializer;
import org.openengsb.api.serialize.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String PATH_CODEINPUT = "code-input/";
    private static final String PATH_CODEOUTPUT = "code-output/";
    private static final String PATH_AMLFILE = PATH_CODEINPUT + "PushListener.aml";

    public static void main(final String[] args) throws IOException {
        LOGGER.info("Generator is now running ...");
        final CAEXFile amlModel = readAmlFile(PATH_AMLFILE);
        final String generatedCode = generateCode(amlModel);
        exportCode(generatedCode, PATH_CODEOUTPUT);
        LOGGER.info("Generator finished");
    }

    private static CAEXFile readAmlFile(final String pathToAmlFile) throws IOException {
        final Deserializer<DocumentRoot> deserializer = new AmlDeserializer();
        final DocumentRoot documentRoot;
        try (final FileInputStream fis = new FileInputStream(pathToAmlFile)) {
            documentRoot = deserializer.deserialize(fis);
        }
        return documentRoot.getCAEXFile();
    }

    private static void exportCode(final String code, final String pathToOutputFolder) throws IOException {
        final File file = new File(pathToOutputFolder + "Main.java");
        file.getParentFile().mkdirs();
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(code);
        }
    }

    private static String generateCode(final CAEXFile amlModel) {
        final String parameter = amlModel.getInstanceHierarchy().get(0).getName();
        return getTemplate().replace("{{parameter}}", parameter);
    }

    private static String getTemplate() {
        return "public final class Main {public static void main(final String[] args) {System.out.println(\"{{parameter}}\");}}";
    }
}
