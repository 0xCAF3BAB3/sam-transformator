package com.jwa.amlmodel.code.generator.generators;

import com.jwa.amlmodel.code.generator.generators.config.CodeGeneratorConfig;

public interface CodeGenerator {
    void generateCode(final CodeGeneratorConfig config) throws IllegalArgumentException, CodeGeneratorException;
}
