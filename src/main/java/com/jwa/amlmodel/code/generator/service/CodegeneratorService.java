package com.jwa.amlmodel.code.generator.service;

import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedRootConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.impl.ComponentCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.MessagemodelCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PortCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PortparametersCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PortsCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PortstyleCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.PorttypeCodegenerator;
import com.jwa.amlmodel.code.generator.generators.impl.ServiceCodegenerator;
import com.jwa.amlmodel.code.generator.generators.utils.AmlmodelUtils;
import com.jwa.amlmodel.code.generator.generators.utils.IOUtils;

import org.cdlflex.models.CAEX.CAEXFile;
import org.cdlflex.models.CAEX.InstanceHierarchy;
import org.cdlflex.models.CAEX.InternalElement;

import java.io.IOException;
import java.nio.file.Path;

public final class CodegeneratorService {
    public final void generateCode(final Path amlmodelFile, final Path outputDirectory) throws CodegeneratorServiceException {
        final CAEXFile amlmodel;
        try {
            amlmodel = AmlmodelUtils.deserialize(amlmodelFile);
        } catch (IOException e) {
            throw new CodegeneratorServiceException("Deserialization of aml-model file '" + amlmodelFile + "' failed: " + e.getMessage(), e);
        }

        try {
            validate(amlmodel);
        } catch (IllegalArgumentException e) {
            throw new CodegeneratorServiceException("AML-model not valid: " + e.getMessage(), e);
        }

        try {
            IOUtils.clearDirectory(outputDirectory);
        } catch (IOException e) {
            throw new CodegeneratorServiceException("Clearing output-directory failed: " + e.getMessage(), e);
        }

        try {
            generateRecursively(amlmodel, outputDirectory);
        } catch (CodegeneratorException e) {
            throw new CodegeneratorServiceException("Code-generation failed: " + e.getMessage(), e);
        }
    }

    private static void validate(final CAEXFile amlmodel) throws IllegalArgumentException {
        if (amlmodel.getInstanceHierarchy().size() != 1) {
            throw new IllegalArgumentException("Exactly one instance-hierarchy expected");
        }
        // TODO: implement validation of passed AML-model (= check, that the AML model elements are used correctly)
        // e.g. no service below a port, componentGroupId must be 'serviceGroupId.serviceArtifactId', ...
    }

    private static void generateRecursively(final CAEXFile amlmodel, final Path outputDirectory) throws CodegeneratorException {
        InstanceHierarchy instanceHierarchy = amlmodel.getInstanceHierarchy().get(0);
        for(InternalElement node : instanceHierarchy.getInternalElement()) {
            if (AmlmodelConstants.hasServiceRole(node)) {
                final GeneratedRootConfig rootConfig = new GeneratedRootConfig(outputDirectory);
                generateRecursively(node, rootConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedRootConfig rootConfig) throws CodegeneratorException {
        final GeneratedServiceConfig serviceConfig = new ServiceCodegenerator().generate(node, rootConfig);
        for(InternalElement children : node.getInternalElement()) {
            if (AmlmodelConstants.hasComponentRole(children)) {
                generateRecursively(children, serviceConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedServiceConfig serviceConfig) throws CodegeneratorException {
        final GeneratedComponentConfig componentConfig = new ComponentCodegenerator().generate(node, serviceConfig);
        for(InternalElement children : node.getInternalElement()) {
            if (AmlmodelConstants.hasPortsRole(children)) {
                generateRecursively(children, componentConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        final GeneratedPortsConfig portsConfig = new PortsCodegenerator().generate(node, componentConfig);
        for(InternalElement children : node.getInternalElement()) {
            if (AmlmodelConstants.hasPortRole(children)) {
                generateRecursively(children, portsConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedPortsConfig portsConfig) throws CodegeneratorException {
        final GeneratedPortConfig portConfig = new PortCodegenerator().generate(node, portsConfig);
        if (AmlmodelConstants.hasPortstyleRole(node)) {
            generatePortstyle(node, portConfig);
        }
        if (AmlmodelConstants.hasPortparametersRole(node)) {
            generatePortparameters(node, portConfig);
        }
        if (AmlmodelConstants.hasPorttypeRole(node)) {
            generatePorttype(node, portConfig);
        }
        if (AmlmodelConstants.hasMessagemodelRole(node)) {
            generateMessagemodel(node, portConfig);
        }
    }

    private static void generatePortstyle(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        new PortstyleCodegenerator().generate(node, portConfig);
    }

    private static void generatePortparameters(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        new PortparametersCodegenerator().generate(node, portConfig);
    }

    private static void generatePorttype(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        new PorttypeCodegenerator().generate(node, portConfig);
    }

    private static void generateMessagemodel(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        new MessagemodelCodegenerator().generate(node, portConfig);
    }
}
