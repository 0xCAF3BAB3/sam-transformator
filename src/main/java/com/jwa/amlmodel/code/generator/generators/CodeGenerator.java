package com.jwa.amlmodel.code.generator.generators;

import org.cdlflex.models.CAEX.InternalElement;

public interface CodeGenerator {
    void generate(final InternalElement node);
}
