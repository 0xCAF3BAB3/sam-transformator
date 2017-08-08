package com.jwa.amlmodel.code.generator.generators;

import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;

import org.cdlflex.models.CAEX.InternalElement;

public interface Codegenerator <ParentConfig extends GeneratedConfig, ReturnedConfig extends GeneratedConfig> {
    ReturnedConfig generate(final InternalElement node, final ParentConfig parentConfig) throws CodegeneratorException;
}
