package com.jwa.pushlistener.code.generator.generators;

import com.jwa.pushlistener.code.generator.generators.config.CodeGeneratorConfig;

public interface CodeGenerator {
    void generateCode(final CodeGeneratorConfig config) throws IllegalArgumentException, CodeGeneratorException;
}
