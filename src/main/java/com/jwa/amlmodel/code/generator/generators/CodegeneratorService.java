package com.jwa.amlmodel.code.generator.generators;

import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.config.CodegeneratorConfig;
import com.jwa.amlmodel.code.generator.generators.impl.ServiceCodegenerator;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import org.cdlflex.models.CAEX.CAEXFile;
import org.cdlflex.models.CAEX.DocumentRoot;
import org.cdlflex.models.CAEX.InstanceHierarchy;
import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlDeserializer;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.eclipse.emf.common.util.EList;
import org.openengsb.api.serialize.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public final class CodegeneratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodegeneratorService.class);
    // TODO: refactor to relative path from this class
    private final static String TEMPLATE_DIRECTORY = "src/main/java/com/jwa/amlmodel/code/generator/generators/templates/";
    private final Configuration freemarkerConfig;
    private final File amlmodelFile;
    private final Path outputDirectory;

    public CodegeneratorService(final File amlmodelFile, final Path outputDirectory) throws CodegeneratorException {
        final Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_26);
        final File templateDirectory = new File(TEMPLATE_DIRECTORY);
        try {
            freemarkerConfig.setDirectoryForTemplateLoading(templateDirectory);
        } catch (IOException e) {
            throw new CodegeneratorException("Template directory invalid: " + e.getMessage(), e);
        }
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setLogTemplateExceptions(false);
        this.freemarkerConfig = freemarkerConfig;

        this.amlmodelFile = amlmodelFile;
        this.outputDirectory = outputDirectory;
    }

    public final void generateCode() throws CodegeneratorException {
        final CAEXFile amlModel;
        try {
            amlModel = readAmlFile(amlmodelFile);
        } catch (IOException e) {
            throw new CodegeneratorException("Reading aml-model failed: " + e.getMessage(), e);
        }

        // TODO: if output-dir exists: clean; if not: create it
        /*
        try {
            FileUtils.cleanDirectory(outputDirectory.toFile());
        } catch (IOException e) {
            throw new CodegeneratorException("Cleaning output-directory failed: " + e.getMessage(), e);
        }
        */

        if (amlModel.getInstanceHierarchy().size() != 1) {
            throw new CodegeneratorException("Not valid");
        }
        InstanceHierarchy instanceHierarchy = amlModel.getInstanceHierarchy().get(0);

        CodegeneratorConfig codeGeneratorConfig = new CodegeneratorConfig(freemarkerConfig);

        EList<InternalElement> rootNodes = instanceHierarchy.getInternalElement();
        for(InternalElement rootNode : rootNodes) {
            // TODO: for every service
            boolean isService = AmlUtil.hasRole(rootNode, AmlmodelConstants.NAME_ROLE_SERVICE);
            if (isService) {
                new ServiceCodegenerator().generate(rootNode, codeGeneratorConfig);
            }
        }

        LOGGER.info("Generation finished");
    }

    private static CAEXFile readAmlFile(final File amlmodelFile) throws IOException {
        final Deserializer<DocumentRoot> deserializer = new AmlDeserializer();
        final DocumentRoot documentRoot;
        try (final FileInputStream fis = new FileInputStream(amlmodelFile)) {
            documentRoot = deserializer.deserialize(fis);
        }
        return documentRoot.getCAEXFile();
    }
}
