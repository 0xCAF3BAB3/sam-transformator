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
import org.cdlflex.models.CAEX.util.AmlUtil;

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

        validate(amlmodel);

        try {
            IOUtils.clearDirectory(outputDirectory);
        } catch (IOException e) {
            throw new CodegeneratorServiceException(e);
        }

        try {
            generateRecursively(amlmodel, outputDirectory);
        } catch (CodegeneratorException e) {
            throw new CodegeneratorServiceException("Code-generation failed: " + e.getMessage(), e);
        }
    }

    private static void validate(final CAEXFile amlmodel) throws CodegeneratorServiceException {
        // TODO validate document

        if (amlmodel.getInstanceHierarchy().size() != 1) {
            throw new CodegeneratorServiceException("Aml-model not valid: " + "exactly one instance-hierarchy expected");
        }
    }

    private static void generateRecursively(final CAEXFile amlmodel, final Path outputDirectory) throws CodegeneratorException {
        InstanceHierarchy instanceHierarchy = amlmodel.getInstanceHierarchy().get(0);
        for(InternalElement node : instanceHierarchy.getInternalElement()) {
            boolean isServiceNode = AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_SERVICE);
            if (isServiceNode) {
                final GeneratedRootConfig rootConfig = new GeneratedRootConfig(outputDirectory);
                generateRecursively(node, rootConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedRootConfig rootConfig) throws CodegeneratorException {
        final GeneratedServiceConfig serviceConfig = new ServiceCodegenerator().generate(node, rootConfig);
        for(InternalElement children : node.getInternalElement()) {
            boolean isComponentNode = AmlUtil.hasRole(children, AmlmodelConstants.NAME_ROLE_COMPONENT);
            if (isComponentNode) {
                generateRecursively(children, serviceConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedServiceConfig serviceConfig) throws CodegeneratorException {
        final GeneratedComponentConfig componentConfig = new ComponentCodegenerator().generate(node, serviceConfig);
        for(InternalElement children : node.getInternalElement()) {
            boolean isPortsNode = AmlUtil.hasRole(children, AmlmodelConstants.NAME_ROLE_PORTS);
            if (isPortsNode) {
                generateRecursively(children, componentConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedComponentConfig componentConfig) throws CodegeneratorException {
        final GeneratedPortsConfig portsConfig = new PortsCodegenerator().generate(node, componentConfig);
        for(InternalElement children : node.getInternalElement()) {
            boolean isPortNode = AmlUtil.hasRole(children, AmlmodelConstants.NAME_ROLE_PORT);
            if (isPortNode) {
                generateRecursively(children, portsConfig);
            }
        }
    }

    private static void generateRecursively(final InternalElement node, final GeneratedPortsConfig portsConfig) throws CodegeneratorException {
        final GeneratedPortConfig portConfig = new PortCodegenerator().generate(node, portsConfig);
        if (AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_PORTSTYLE)) {
            generatePortstyle(node, portConfig);
        }
        if (AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_PORTPARAMETERS)) {
            generatePortparameters(node, portConfig);
        }
        if (AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_PORTTYPE)) {
            generatePorttype(node, portConfig);
        }
        if (AmlUtil.hasRole(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL)) {
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
