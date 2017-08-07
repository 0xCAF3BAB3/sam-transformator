package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedMessagemodelConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.utils.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.AmlmodelUtils;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessagemodelCodegenerator implements Codegenerator<GeneratedMessagemodelConfig, GeneratedPortConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagemodelCodegenerator.class);

    @Override
    public final GeneratedMessagemodelConfig generate(final InternalElement node, final GeneratedPortConfig parentConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        String messagemodelName = AmlmodelUtils.getRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL + "/");

        LOGGER.trace("Generating message-model for xxx '" + messagemodelName + "' ...");

        /*
        final Path messagemodelFile = Paths.get(messagemodelOutput.toFile().getPath() + "/" + messagemodelName + ".java");

        final boolean exists = Files.exists(messagemodelFile) && Files.isRegularFile(messagemodelFile);
        if (exists) {
            LOGGER.trace(messagemodelName + " is already generated");
        } else {
            final Map<String, String> dataModel = new HashMap<>();
            dataModel.put("packageName", packageName);
            dataModel.put("messagemodelName", messagemodelName);
            try {
                final Template template = codeGeneratorConfig.getFreemarkerConfig().getTemplate("Messagemodel_Instance.ftlh");
                try (final Writer writer = Files.newBufferedWriter(messagemodelFile, StandardCharsets.UTF_8)) {
                    template.process(dataModel, writer);
                }
            } catch (IOException | TemplateException e) {
                throw new CodegeneratorException(e.getMessage(), e);
            }
            LOGGER.trace(messagemodelName + " was created");
        }
        */

        LOGGER.trace("Generating message-model for xxx '" + messagemodelName + "' finished");

        return new GeneratedMessagemodelConfig();
    }
}
