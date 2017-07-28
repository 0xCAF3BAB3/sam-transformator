package com.jwa.pushlistener.code.generator.generators;

import com.jwa.pushlistener.code.generator.generators.config.CodeGeneratorConfig;
import com.jwa.pushlistener.code.generator.generators.impl.ServiceCodeGenerator;

public final class CodeGeneratorProducer implements CodeGenerator {
    private static CodeGeneratorProducer instance = null;
    private CodeGenerator serviceGenerator;

    private CodeGeneratorProducer() {}

    public static CodeGeneratorProducer getInstance() {
        if(instance == null) {
            instance = new CodeGeneratorProducer();
        }
        return instance;
    }

    public final void generateCode(final CodeGeneratorConfig config) throws IllegalArgumentException, CodeGeneratorException {
        if (serviceGenerator == null) {
            serviceGenerator = new ServiceCodeGenerator();
        }
        serviceGenerator.generateCode(config);
    }
}
