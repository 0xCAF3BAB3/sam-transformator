package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.FileTemplate;
import com.jwa.amlmodel.code.generator.generators.config.Files;
import com.jwa.amlmodel.code.generator.generators.config.FreemarkerTemplate;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;
import com.jwa.amlmodel.code.generator.generators.utils.IOUtils;
import com.jwa.amlmodel.code.generator.generators.utils.MavenModuleInfo;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PortsCodegenerator implements Codegenerator<GeneratedComponentConfig, GeneratedPortsConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortsCodegenerator.class);

    @Override
    public final GeneratedPortsConfig generate(final InternalElement node, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPortsRole(node)) {
            throw new IllegalArgumentException("Passed node has no ports-role");
        }

        final String portsName = node.getName();

        LOGGER.trace("Generating ports for ports-node '" + portsName + "' ...");

        final GeneratedServiceConfig serviceConfig = componentConfig.getServiceConfig();
        MavenModuleInfo communicationMavenModule = serviceConfig.getCommunicationMavenModuleInfo();
        if (communicationMavenModule == null) {
            MavenModuleInfo communicationMavenModuleInfo = createCommunicationMavenModule(serviceConfig);
            copyCommunicationFilesToCommunicationMavenModule(communicationMavenModuleInfo);
            serviceConfig.setCommunicationMavenModuleInfo(communicationMavenModuleInfo);
            communicationMavenModule = serviceConfig.getCommunicationMavenModuleInfo();
        }

        addComponentCommunicationDependency(communicationMavenModule, componentConfig);

        final Path componentCommunicationserviceClassFile = createComponentCommunicationserviceClass(communicationMavenModule, componentConfig);

        adaptComponentMainClass(communicationMavenModule, componentConfig);

        LOGGER.trace("Generating ports for ports-node '" + portsName + "' finished");

        return new GeneratedPortsConfig(componentCommunicationserviceClassFile, componentConfig);
    }

    private static MavenModuleInfo createCommunicationMavenModule(final GeneratedServiceConfig serviceConfig) throws CodegeneratorException {
        final String artifactId = "communication";

        final String pomFileContent;
        final Path pomTemplate = GlobalConfig.getTemplate(FileTemplate.POM_COMMUNICATION);
        final Map<String, String> pomDatamodel = new HashMap<>();
        pomDatamodel.put("parentGroupId", serviceConfig.getServiceMavenProjectInfo().getGroupId());
        pomDatamodel.put("parentArtifactId", serviceConfig.getServiceMavenProjectInfo().getArtifactId());
        final String groupId = serviceConfig.getServiceMavenProjectInfo().getGroupAndArtifactId();
        pomDatamodel.put("groupId", groupId);
        pomDatamodel.put("artifactId", artifactId);
        try {
            pomFileContent = CodefileUtils.processTemplate(pomTemplate, pomDatamodel, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Generating POM file-content failed: " + e.getMessage(), e);
        }

        final String loggerConfigFileContent;
        final Template loggerConfigTemplate = GlobalConfig.getTemplate(FreemarkerTemplate.LOG4J2);
        final Map<String, Object> loggerConfigDatamodel = new HashMap<>();
        loggerConfigDatamodel.put("name", artifactId);
        loggerConfigDatamodel.put("groupId", groupId);
        try {
            loggerConfigFileContent = CodefileUtils.processTemplate(loggerConfigTemplate, loggerConfigDatamodel, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Generating logger-config file-content failed: " + e.getMessage(), e);
        }

        try {
            return CodefileUtils.createMavenModule(
                    artifactId,
                    serviceConfig.getServiceMavenProjectInfo(),
                    pomFileContent,
                    loggerConfigFileContent,
                    GlobalConfig.getCharset()
            );
        } catch (IOException e) {
            throw new CodegeneratorException("Creating Maven module failed: " + e.getMessage(), e);
        }
    }

    private static void copyCommunicationFilesToCommunicationMavenModule(final MavenModuleInfo communicationMavenModuleInfo) throws CodegeneratorException {
        final Path communicationFilesDirectory = GlobalConfig.getFiles(Files.COMMUNICATION);
        try {
            IOUtils.copyDirectory(communicationFilesDirectory, communicationMavenModuleInfo.getCodeDirectory());
        } catch (IOException e) {
            throw new CodegeneratorException("Copying directory failed: " + e.getMessage(), e);
        }
        try {
            CodefileUtils.adaptPackageAndImportNames(
                    communicationMavenModuleInfo.getCodeDirectory(),
                    "{{communicationPackageName}}",
                    communicationMavenModuleInfo.getGroupAndArtifactId(),
                    GlobalConfig.getCharset()
            );
        } catch (IOException e) {
            throw new CodegeneratorException("Adapting package- and import-names failed: " + e.getMessage(), e);
        }
    }

    private static void addComponentCommunicationDependency(final MavenModuleInfo communicationMavenModuleInfo, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        try {
            CodefileUtils.addMavenDependency(
                    communicationMavenModuleInfo,
                    componentConfig.getComponentMavenModuleInfo(),
                    GlobalConfig.getCharset()
            );
        } catch (IOException e) {
            throw new CodegeneratorException("Adding Maven dependency failed: " + e.getMessage(), e);
        }
    }

    private static Path createComponentCommunicationserviceClass(final MavenModuleInfo communicationMavenModuleInfo, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.COMMSERVICE_INITIAL);
        final Map<String, String> datamodel = new HashMap<>();
        datamodel.put("packageName", componentConfig.getComponentMavenModuleInfo().getGroupAndArtifactId());
        datamodel.put("communicationPackageName", communicationMavenModuleInfo.getGroupAndArtifactId());
        final Path file = componentConfig.getComponentMavenModuleInfo().getCodeDirectory().resolve("CommunicationService.java");
        try {
            return CodefileUtils.processTemplate(template, datamodel, file, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Creating CommunicationService class failed: " + e.getMessage(), e);
        }
    }

    private static void adaptComponentMainClass(final MavenModuleInfo communicationMavenModuleInfo, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        final String commServiceUsageStatements;
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.MAIN_COMMSERVICEUSAGE_SNIPPET);
        final Map<String, Object> datamodel = new HashMap<>();
        try {
            commServiceUsageStatements = CodefileUtils.processTemplate(template, datamodel, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Generating CommunicationService-usage statements failed: " + e.getMessage(), e);
        }

        final Path mainClassFile = componentConfig.getComponentMainClassFile();
        try {
            CodefileUtils.appendStatementsToMethod(
                    commServiceUsageStatements,
                    "main",
                    mainClassFile,
                    GlobalConfig.getCharset()
            );
            final String[] importStatements = {
                    communicationMavenModuleInfo.getGroupAndArtifactId() + ".ports.PortsService",
                    communicationMavenModuleInfo.getGroupAndArtifactId() + ".ports.PortsServiceException"
            };
            for(String importStatement : importStatements) {
                CodefileUtils.addImportStatement(importStatement, mainClassFile, GlobalConfig.getCharset());
            }
        } catch (IOException e) {
            throw new CodegeneratorException("Adapting file '" + mainClassFile + "' failed: " + e.getMessage(), e);
        }
    }
}
