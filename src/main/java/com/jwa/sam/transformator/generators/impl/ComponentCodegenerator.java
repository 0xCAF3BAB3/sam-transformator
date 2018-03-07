package com.jwa.sam.transformator.generators.impl;

import com.jwa.sam.transformator.generators.Codegenerator;
import com.jwa.sam.transformator.generators.CodegeneratorException;
import com.jwa.sam.transformator.generators.config.FileTemplate;
import com.jwa.sam.transformator.generators.config.FreemarkerTemplate;
import com.jwa.sam.transformator.generators.config.GlobalConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.sam.transformator.generators.constants.AmlmodelConstants;
import com.jwa.sam.transformator.generators.utils.CodefileUtils;
import com.jwa.sam.transformator.generators.utils.MavenModuleInfo;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ComponentCodegenerator implements Codegenerator<GeneratedServiceConfig, GeneratedComponentConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentCodegenerator.class);

    @Override
    public final GeneratedComponentConfig generate(final InternalElement node, final GeneratedServiceConfig serviceConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasComponentRole(node)) {
            throw new IllegalArgumentException("Passed node has no component-role");
        }

        final String componentName = node.getName();

        LOGGER.trace("Generating component for component-node '" + componentName + "' ...");

        final String componentArtifactId = AmlmodelConstants.getComponentArtifactId(node);
        MavenModuleInfo componentMavenModuleInfo = createComponentMavenModule(componentArtifactId, serviceConfig);

        final Path componentMainClassFile = createMainClass(componentMavenModuleInfo);

        LOGGER.trace("Generating component for component-node '" + componentName + "' finished");

        return new GeneratedComponentConfig(componentMavenModuleInfo, componentMainClassFile, serviceConfig);
    }

    private static MavenModuleInfo createComponentMavenModule(final String artifactId, final GeneratedServiceConfig serviceConfig) throws CodegeneratorException {
        final String pomFileContent;
        final Path pomTemplate = GlobalConfig.getTemplate(FileTemplate.POM_COMPONENT);
        final Map<String, String> pomDatamodel = new HashMap<>();
        pomDatamodel.put("parentGroupId", serviceConfig.getServiceMavenProjectInfo().getGroupId());
        pomDatamodel.put("parentArtifactId", serviceConfig.getServiceMavenProjectInfo().getArtifactId());
        final String groupId = serviceConfig.getServiceMavenProjectInfo().getGroupAndArtifactId();
        pomDatamodel.put("groupId", groupId);
        pomDatamodel.put("artifactId", artifactId);
        pomDatamodel.put("profileId", artifactId);
        final String pathToMainClass = groupId + "." + artifactId + ".Main";
        pomDatamodel.put("pathToMainClass", pathToMainClass);
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

    private static Path createMainClass(final MavenModuleInfo componentMavenModuleInfo) throws CodegeneratorException {
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.MAIN_INITIAL);
        final Map<String, String> datamodel = new HashMap<>();
        final String packageName = componentMavenModuleInfo.getGroupAndArtifactId();
        datamodel.put("packageName", packageName);
        final Path file = componentMavenModuleInfo.getCodeDirectory().resolve("Main.java");
        try {
            return CodefileUtils.processTemplate(template, datamodel, file, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Creating Main class failed: " + e.getMessage(), e);
        }
    }
}
