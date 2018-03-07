package com.jwa.sam.transformator.generators.impl;

import com.jwa.sam.transformator.generators.Codegenerator;
import com.jwa.sam.transformator.generators.CodegeneratorException;
import com.jwa.sam.transformator.generators.config.FileTemplate;
import com.jwa.sam.transformator.generators.config.FreemarkerTemplate;
import com.jwa.sam.transformator.generators.config.GlobalConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedMessagemodelConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.sam.transformator.generators.constants.AmlmodelConstants;
import com.jwa.sam.transformator.generators.utils.CodefileUtils;
import com.jwa.sam.transformator.generators.utils.IOUtils;
import com.jwa.sam.transformator.generators.utils.MavenModuleInfo;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class MessagemodelCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedMessagemodelConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagemodelCodegenerator.class);

    @Override
    public final GeneratedMessagemodelConfig generate(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasMessagemodelRole(node)) {
            throw new IllegalArgumentException("Passed node has no messagemodel-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating message-model for port-node '" + portName + "' ...");

        final GeneratedComponentConfig componentConfig = portConfig.getPortsConfig().getComponentConfig();

        final GeneratedServiceConfig serviceConfig = componentConfig.getServiceConfig();
        MavenModuleInfo messagemodelMavenModule = serviceConfig.getMessagemodelMavenModuleInfo();
        if (messagemodelMavenModule == null) {
            MavenModuleInfo messagemodelMavenModuleInfo = createMessagemodelMavenModule(serviceConfig);
            copyMessagemodelFilesToMessagemodelMavenModule(messagemodelMavenModuleInfo, serviceConfig);
            serviceConfig.setMessagemodelMavenModuleInfo(messagemodelMavenModuleInfo);
            messagemodelMavenModule = serviceConfig.getMessagemodelMavenModuleInfo();
        }

        if (!componentConfig.isMessagemodelDependencySet()) {
            addComponentMessagemodelDependency(messagemodelMavenModule, componentConfig);
            componentConfig.setMessagemodelDependencySet();
        }

        final String messagemodelName = AmlmodelConstants.getMessagemodelName(node);
        createMessageClassIfNotExists(messagemodelName, messagemodelMavenModule);

        LOGGER.trace("Generating message-model for port-node '" + portName + "' finished");

        return new GeneratedMessagemodelConfig();
    }

    private static MavenModuleInfo createMessagemodelMavenModule(final GeneratedServiceConfig serviceConfig) throws CodegeneratorException {
        final String artifactId = "messagemodel";

        final String pomFileContent;
        final Path pomTemplate = GlobalConfig.getTemplate(FileTemplate.POM_MESSAGEMODEL);
        final Map<String, String> pomDatamodel = new HashMap<>();
        pomDatamodel.put("parentGroupId", serviceConfig.getServiceMavenProjectInfo().getGroupId());
        pomDatamodel.put("parentArtifactId", serviceConfig.getServiceMavenProjectInfo().getArtifactId());
        final String groupId = serviceConfig.getServiceMavenProjectInfo().getGroupAndArtifactId();
        pomDatamodel.put("groupId", groupId);
        pomDatamodel.put("artifactId", artifactId);
        pomDatamodel.put("dependencyCommunicationGroupId", serviceConfig.getServiceMavenProjectInfo().getGroupAndArtifactId());
        pomDatamodel.put("dependencyCommunicationArtifactId", serviceConfig.getCommunicationMavenModuleInfo().getArtifactId());
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

    private static void copyMessagemodelFilesToMessagemodelMavenModule(final MavenModuleInfo messagemodelMavenModuleInfo, final GeneratedServiceConfig serviceConfig) throws CodegeneratorException {
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.MESSAGEMODEL);
        final Map<String, String> datamodel = new HashMap<>();
        datamodel.put("packageName", messagemodelMavenModuleInfo.getGroupAndArtifactId());
        datamodel.put("communicationPackageName", serviceConfig.getCommunicationMavenModuleInfo().getGroupAndArtifactId());
        final Path file = messagemodelMavenModuleInfo.getCodeDirectory().resolve("MessageModel.java");
        try {
            CodefileUtils.processTemplate(template, datamodel, file, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Copying MessageModel class failed: " + e.getMessage(), e);
        }
    }

    private static void addComponentMessagemodelDependency(final MavenModuleInfo messagemodelMavenModuleInfo, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        try {
            CodefileUtils.addMavenDependency(messagemodelMavenModuleInfo, componentConfig.getComponentMavenModuleInfo(), GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Adding Maven dependency failed: " + e.getMessage(), e);
        }
    }

    private static void createMessageClassIfNotExists(final String messagemodelName, final MavenModuleInfo messagemodelMavenModuleInfo) throws CodegeneratorException {
        final String messagemodelClassFilename = messagemodelName + ".java";
        final Path file = messagemodelMavenModuleInfo.getCodeDirectory().resolve(messagemodelClassFilename);
        if (IOUtils.isValidFile(file)) {
            return;
        }
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.MESSAGE);
        final Map<String, String> datamodel = new HashMap<>();
        datamodel.put("packageName", messagemodelMavenModuleInfo.getGroupAndArtifactId());
        datamodel.put("messagemodelName", messagemodelName);
        try {
            CodefileUtils.processTemplate(template, datamodel, file, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Creating " + messagemodelName + " class failed: " + e.getMessage(), e);
        }
    }
}
