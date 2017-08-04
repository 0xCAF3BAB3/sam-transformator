package com.jwa.amlmodel.code.generator.generators;

import com.jwa.amlmodel.code.generator.generators.config.CodegeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;

public interface Codegenerator {
    void generate(final InternalElement node, final CodegeneratorConfig codeGeneratorConfig);
}
