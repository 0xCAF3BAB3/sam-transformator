package com.jwa.sam.transformator.generators.impl;

import com.jwa.sam.transformator.generators.Codegenerator;
import com.jwa.sam.transformator.generators.CodegeneratorException;
import com.jwa.sam.transformator.generators.config.FileTemplate;
import com.jwa.sam.transformator.generators.config.FreemarkerTemplate;
import com.jwa.sam.transformator.generators.config.GlobalConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedRootConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.sam.transformator.generators.constants.AmlmodelConstants;
import com.jwa.sam.transformator.generators.utils.CodefileUtils;
import com.jwa.sam.transformator.generators.utils.MavenProjectInfo;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ServiceCodegenerator implements Codegenerator<GeneratedRootConfig, GeneratedServiceConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCodegenerator.class);

    @Override
    public final GeneratedServiceConfig generate(final InternalElement node, final GeneratedRootConfig rootConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasServiceRole(node)) {
            throw new IllegalArgumentException("Passed node has no service-role");
        }

        final String serviceName = node.getName();

        LOGGER.trace("Generating service for service-node '" + serviceName + "' ...");

        final String serviceGroupId = AmlmodelConstants.getServiceGroupId(node);
        final String serviceArtifactId = AmlmodelConstants.getServiceArtifactId(node);
        final MavenProjectInfo serviceMavenProjectInfo = createServiceMavenProject(serviceName, serviceGroupId, serviceArtifactId, rootConfig);

        createReadme(serviceName, serviceMavenProjectInfo);

        LOGGER.trace("Generating service for service-node '" + serviceName + "' finished");

        return new GeneratedServiceConfig(serviceMavenProjectInfo, rootConfig);
    }

    private static MavenProjectInfo createServiceMavenProject(final String serviceName, final String serviceGroupId, final String serviceArtifactId, final GeneratedRootConfig rootConfig) throws CodegeneratorException {
        final String pomFileContent;
        final Path pomTemplate = GlobalConfig.getTemplate(FileTemplate.POM_SERVICE);
        final Map<String, String> pomDatamodel = new HashMap<>();
        pomDatamodel.put("groupId", serviceGroupId);
        pomDatamodel.put("artifactId", serviceArtifactId);
        try {
            pomFileContent = CodefileUtils.processTemplate(pomTemplate, pomDatamodel, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Generating POM file-content failed: " + e.getMessage(), e);
        }

        try {
            return CodefileUtils.createMavenProject(
                    serviceGroupId,
                    serviceArtifactId,
                    rootConfig.getOutputDirectory().resolve(serviceName),
                    pomFileContent,
                    GlobalConfig.getCharset()
            );
        } catch (IOException e) {
            throw new CodegeneratorException("Creating Maven project failed: " + e.getMessage(), e);
        }
    }

    private static void createReadme(final String serviceName, final MavenProjectInfo serviceMavenProjectInfo) throws CodegeneratorException {
        final Template readmeTemplate = GlobalConfig.getTemplate(FreemarkerTemplate.README_SERVICE);
        final Map<String, String> readmeDatamodel = new HashMap<>();
        readmeDatamodel.put("serviceName", serviceName);
        final Path readmeFile = serviceMavenProjectInfo.getDirectory().resolve("README.md");
        try {
            CodefileUtils.processTemplate(readmeTemplate, readmeDatamodel, readmeFile, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Creating Readme failed: " + e.getMessage(), e);
        }
    }
}
