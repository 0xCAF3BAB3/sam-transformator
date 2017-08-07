package com.jwa.amlmodel.code.generator.service;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedRootConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.amlmodel.code.generator.generators.impl.ComponentCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.MessagemodelCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PortCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PortparametersCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PortsCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PortstyleCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PorttypeCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.ServiceCodegenerator;
import com.jwa.amlmodel.code.generator.generators.utils.AmlmodelConstants;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import org.cdlflex.models.CAEX.CAEXFile;
import org.cdlflex.models.CAEX.DocumentRoot;
import org.cdlflex.models.CAEX.InstanceHierarchy;
import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlDeserializer;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.openengsb.api.serialize.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CodegeneratorService {
    // TODO: the following line is quite hacky
    private final static String TEMPLATE_DIRECTORY = "src/main/java/" + Codegenerator.class.getPackage().getName().replace(".", "/") + "/templates";

    public final void generateCode(final Path amlmodelFile, final Path outputDirectory) throws CodegeneratorServiceException {
        final CAEXFile amlmodel;
        try {
            amlmodel = deserialize(amlmodelFile);
        } catch (IOException e) {
            throw new CodegeneratorServiceException("Aml-model deserialization failed: " + e.getMessage(), e);
        }

        validate(amlmodel);

        /*
        // TODO: if output-dir exists: clean; if not: create it
        try {
            FileUtils.cleanDirectory(outputDirectory.toFile());
        } catch (IOException e) {
            throw new CodegeneratorException("Cleaning output-directory failed: " + e.getMessage(), e);
        }
        */

        try {
            generateRecursively(amlmodel, outputDirectory);
        } catch (CodegeneratorException e) {
            throw new CodegeneratorServiceException("Code-generation failed: " + e.getMessage(), e);
        }
    }

    private static CAEXFile deserialize(final Path amlmodelFile) throws IOException {
        final Deserializer<DocumentRoot> deserializer = new AmlDeserializer();
        final DocumentRoot documentRoot;
        try (final InputStream in = Files.newInputStream(amlmodelFile)) {
            documentRoot = deserializer.deserialize(in);
        }
        return documentRoot.getCAEXFile();
    }

    private static void validate(final CAEXFile amlmodel) throws CodegeneratorServiceException {
        // TODO validate document

        if (amlmodel.getInstanceHierarchy().size() != 1) {
            throw new CodegeneratorServiceException("Aml-model not valid: " + "exactly one instance-hierarchy expected");
        }
    }

    private static GlobalConfig createGlobalConfig() {
        final Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_26);
        final Path templateDirectory = Paths.get(TEMPLATE_DIRECTORY);
        try {
            freemarkerConfig.setDirectoryForTemplateLoading(templateDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Template directory invalid: " + e.getMessage(), e);
        }
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfig.setLogTemplateExceptions(false);

        return new GlobalConfig(freemarkerConfig);
    }

    private static void generateRecursively(final CAEXFile amlmodel, final Path outputDirectory) throws CodegeneratorException {
        GlobalConfig globalConfig = createGlobalConfig();

        InstanceHierarchy instanceHierarchy = amlmodel.getInstanceHierarchy().get(0);
        for(InternalElement node : instanceHierarchy.getInternalElement()) {
            boolean isServiceNode = AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_SERVICE);
            if (isServiceNode) {
                final GeneratedRootConfig rootConfig = new GeneratedRootConfig(outputDirectory);
                generateRecursively(node, rootConfig, globalConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedRootConfig rootConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        final GeneratedServiceConfig serviceConfig = new ServiceCodegenerator().generate(node, rootConfig, globalConfig);
        for(InternalElement children : node.getInternalElement()) {
            boolean isComponentNode = AmlUtil.hasRole(children, AmlmodelConstants.NAME_ROLE_COMPONENT);
            if (isComponentNode) {
                generateRecursively(children, serviceConfig, globalConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedServiceConfig serviceConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        final GeneratedComponentConfig componentConfig = new ComponentCodegenerator().generate(node, serviceConfig, globalConfig);
        for(InternalElement children : node.getInternalElement()) {
            boolean isPortsNode = AmlUtil.hasRole(children, AmlmodelConstants.NAME_ROLE_PORTS);
            if (isPortsNode) {
                generateRecursively(children, componentConfig, globalConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedComponentConfig componentConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        final GeneratedPortsConfig portsConfig = new PortsCodegenerator().generate(node, componentConfig, globalConfig);
        for(InternalElement children : node.getInternalElement()) {
            boolean isPortNode = AmlUtil.hasRole(children, AmlmodelConstants.NAME_ROLE_PORT);
            if (isPortNode) {
                generateRecursively(children, portsConfig, globalConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedPortsConfig portsConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        final GeneratedPortConfig portConfig = new PortCodegenerator().generate(node, portsConfig, globalConfig);
        if (AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_PORTSTYLE)) {
            generatePortstyle(node, portConfig, globalConfig);
        }
        if (AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_PORTPARAMETERS)) {
            generatePortparameters(node, portConfig, globalConfig);
        }
        if (AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_PORTTYPE)) {
            generatePorttype(node, portConfig, globalConfig);
        }
        if (AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL)) {
            generateMessagemodel(node, portConfig, globalConfig);
        }
    }

    private static void generatePortstyle(final InternalElement node, final GeneratedPortConfig portConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        new PortstyleCodegenerator().generate(node, portConfig, globalConfig);
    }

    private static void generatePortparameters(final InternalElement node, final GeneratedPortConfig portConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        new PortparametersCodegenerator().generate(node, portConfig, globalConfig);
    }

    private static void generatePorttype(final InternalElement node, final GeneratedPortConfig portConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        new PorttypeCodegenerator().generate(node, portConfig, globalConfig);
    }

    private static void generateMessagemodel(final InternalElement node, final GeneratedPortConfig portConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        new MessagemodelCodegenerator().generate(node, portConfig, globalConfig);
    }
}
