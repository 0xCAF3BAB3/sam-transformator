package com.jwa.pushlistener.code.generator.generators.impl;

import com.jwa.pushlistener.code.generator.generators.CodeGenerator;
import com.jwa.pushlistener.code.generator.generators.CodeGeneratorException;
import com.jwa.pushlistener.code.generator.generators.config.CodeGeneratorConfig;

public final class ServiceCodeGenerator implements CodeGenerator {
    private CodeGenerator communicationGenerator;
    private CodeGenerator messageModelGenerator;

    @Override
    public final void generateCode(final CodeGeneratorConfig config) throws IllegalArgumentException, CodeGeneratorException {
        // TODO: generates Maven project

        // TODO: for each found component: call componentGenerator and pass config

        if (communicationGenerator == null) {
            communicationGenerator = new CommunicationCodeGenerator();
        }
        communicationGenerator.generateCode(config);

        if (messageModelGenerator == null) {
            messageModelGenerator = new MessageModelCodeGenerator();
        }
        messageModelGenerator.generateCode(config);
    }
}
