package com.jwa.sam.transformator.service;

import com.jwa.sam.transformator.generators.CodegeneratorException;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedComponentConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedRootConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedServiceConfig;
import com.jwa.sam.transformator.generators.constants.AmlmodelConstants;
import com.jwa.sam.transformator.generators.impl.ComponentCodegenerator;
import com.jwa.sam.transformator.generators.impl.MessagemodelCodegenerator;
import com.jwa.sam.transformator.generators.impl.PortCodegenerator;
import com.jwa.sam.transformator.generators.impl.PortparametersCodegenerator;
import com.jwa.sam.transformator.generators.impl.PortsCodegenerator;
import com.jwa.sam.transformator.generators.impl.PortstyleCodegenerator;
import com.jwa.sam.transformator.generators.impl.PorttypeCodegenerator;
import com.jwa.sam.transformator.generators.impl.ServiceCodegenerator;
import com.jwa.sam.transformator.generators.utils.AmlmodelUtils;
import com.jwa.sam.transformator.generators.utils.IOUtils;

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
            validateModel(amlmodel);
        } catch (IllegalArgumentException e) {
            throw new CodegeneratorServiceException("AML-model not valid: " + e.getMessage(), e);
        }

        try {
            IOUtils.clearDirectory(outputDirectory);
        } catch (IOException e) {
            throw new CodegeneratorServiceException("Clearing output-directory failed: " + e.getMessage(), e);
        }

        try {
            generateCode(amlmodel, outputDirectory);
        } catch (CodegeneratorException e) {
            throw new CodegeneratorServiceException("Code-generation failed: " + e.getMessage(), e);
        }
    }

    private static void validateModel(final CAEXFile amlmodel) throws IllegalArgumentException {
        if (amlmodel.getInstanceHierarchy().size() != 1) {
            throw new IllegalArgumentException("Exactly one instance-hierarchy expected");
        }
        // TODO: implement validation of passed AML-model (= check, that the AML model elements are used correctly)
        /*
         *  - no service below a port
         *  - services, components and ports must have unique names
         *  - sender-port needs a messagemodel assigned (its optional for receiver-ports)
         *  - componentGroupId must be 'serviceGroupId.serviceArtifactId'
         *  - serviceName must be valid (folder with this name will be created)
         *  - groupId and artifactId of services and components must be valid
         *    (call methods 'isValidMavenGroupId(...)' and 'isValidMavenArtifactId(...)' in CodefileUtils.java)
         *  - messagemodelName must be valid (call method 'isValidJavaIdentifier(...)' in CodefileUtils.java)
         *  - ...
         */
    }

    private static void generateCode(final CAEXFile amlmodel, final Path outputDirectory) throws CodegeneratorException {
        InstanceHierarchy instanceHierarchy = amlmodel.getInstanceHierarchy().get(0);
        for(InternalElement node : instanceHierarchy.getInternalElement()) {
            if (AmlmodelConstants.hasServiceRole(node)) {
                final GeneratedRootConfig rootConfig = new GeneratedRootConfig(outputDirectory);
                generateServiceCode(node, rootConfig);
            }
        }
    }

    private static void generateServiceCode(final InternalElement node, final GeneratedRootConfig rootConfig) throws CodegeneratorException {
        final GeneratedServiceConfig serviceConfig = new ServiceCodegenerator().generate(node, rootConfig);
        for(InternalElement childNode : node.getInternalElement()) {
            if (AmlmodelConstants.hasComponentRole(childNode)) {
                generateComponentCode(childNode, serviceConfig);
            }
        }
    }

    private static void generateComponentCode(final InternalElement node, final GeneratedServiceConfig serviceConfig) throws CodegeneratorException {
        final GeneratedComponentConfig componentConfig = new ComponentCodegenerator().generate(node, serviceConfig);
        for(InternalElement childNode : node.getInternalElement()) {
            if (AmlmodelConstants.hasPortsRole(childNode)) {
                generatePortsCode(childNode, componentConfig);
            }
        }
    }

    private static void generatePortsCode(final InternalElement node, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        final GeneratedPortsConfig portsConfig = new PortsCodegenerator().generate(node, componentConfig);
        for(InternalElement childNode : node.getInternalElement()) {
            if (AmlmodelConstants.hasPortRole(childNode)) {
                generatePortCode(childNode, portsConfig);
            }
        }
    }

    private static void generatePortCode(final InternalElement node, final GeneratedPortsConfig portsConfig) throws CodegeneratorException {
        final GeneratedPortConfig portConfig = new PortCodegenerator().generate(node, portsConfig);
        if (AmlmodelConstants.hasPortstyleRole(node)) {
            generatePortstyleCode(node, portConfig);
        }
        if (AmlmodelConstants.hasPortparametersRole(node)) {
            generatePortparametersCode(node, portConfig);
        }
        if (AmlmodelConstants.hasPorttypeRole(node)) {
            generatePorttypeCode(node, portConfig);
        }
        if (AmlmodelConstants.hasMessagemodelRole(node)) {
            generateMessagemodelCode(node, portConfig);
        }
    }

    private static void generatePortstyleCode(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        new PortstyleCodegenerator().generate(node, portConfig);
    }

    private static void generatePortparametersCode(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        new PortparametersCodegenerator().generate(node, portConfig);
    }

    private static void generatePorttypeCode(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        new PorttypeCodegenerator().generate(node, portConfig);
    }

    private static void generateMessagemodelCode(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        new MessagemodelCodegenerator().generate(node, portConfig);
    }
}
