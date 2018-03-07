package com.jwa.sam.transformator.generators;

import com.jwa.sam.transformator.generators.config.generated.GeneratedConfig;

import org.cdlflex.models.CAEX.InternalElement;

public interface Codegenerator <ParentConfig extends GeneratedConfig, ReturnedConfig extends GeneratedConfig> {
    ReturnedConfig generate(final InternalElement node, final ParentConfig parentConfig) throws CodegeneratorException;
}
