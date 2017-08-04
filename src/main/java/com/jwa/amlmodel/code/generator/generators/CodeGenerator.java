package com.jwa.amlmodel.code.generator.generators;

import com.jwa.amlmodel.code.generator.generators.config.CodeGeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;

public interface CodeGenerator {
    void generate(final InternalElement node, final CodeGeneratorConfig codeGeneratorConfig);
}
