package com.jwa.amlmodel.code.generator.generators.utils;

import org.cdlflex.models.CAEX.Attribute;
import org.cdlflex.models.CAEX.CAEXFile;
import org.cdlflex.models.CAEX.DocumentRoot;
import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.RoleRequirements;
import org.cdlflex.models.CAEX.SupportedRoleClass;
import org.cdlflex.models.CAEX.util.AmlDeserializer;
import org.openengsb.api.serialize.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class AmlmodelUtils {
    private AmlmodelUtils() {}

    public static CAEXFile deserialize(final Path amlmodelFile) throws IOException {
        if (!IOUtils.isValidFile(amlmodelFile)) {
            throw new IllegalArgumentException("Passed amlmodel-file '" + amlmodelFile + "' doesn't exists or is not valid");
        }
        final Deserializer<DocumentRoot> deserializer = new AmlDeserializer();
        final DocumentRoot documentRoot;
        try (final InputStream in = Files.newInputStream(amlmodelFile)) {
            documentRoot = deserializer.deserialize(in);
        }
        return documentRoot.getCAEXFile();
    }

    public static boolean hasRoleStartingWith(final InternalElement element, final String roleStartingWith) {
        if (element == null) {
            throw new IllegalArgumentException("Passed element is null");
        }
        if (roleStartingWith == null) {
            throw new IllegalArgumentException("Passed role-starting-with is null");
        }
        final RoleRequirements roleRequirements = element.getRoleRequirements();
        if (roleRequirements != null && roleRequirements.getRefBaseRoleClassPath().startsWith(roleStartingWith)) {
            return true;
        }
        for(SupportedRoleClass supportedRoleClass : element.getSupportedRoleClass()) {
            if (supportedRoleClass.getRefRoleClassPath().startsWith(roleStartingWith)) {
                return true;
            }
        }
        return false;
    }

    public static Optional<String> getRoleStartingWith(final InternalElement element, final String roleStartingWith) {
        if (element == null) {
            throw new IllegalArgumentException("Passed element is null");
        }
        if (roleStartingWith == null) {
            throw new IllegalArgumentException("Passed role-starting-with is null");
        }
        final RoleRequirements roleRequirements = element.getRoleRequirements();
        if (roleRequirements != null && roleRequirements.getRefBaseRoleClassPath().startsWith(roleStartingWith)) {
            return Optional.of(roleRequirements.getRefBaseRoleClassPath());
        }
        for(SupportedRoleClass supportedRoleClass : element.getSupportedRoleClass()) {
            if (supportedRoleClass.getRefRoleClassPath().startsWith(roleStartingWith)) {
                return Optional.of(supportedRoleClass.getRefRoleClassPath());
            }
        }
        return Optional.empty();
    }

    public static List<Attribute> getAttributesStartingWith(final InternalElement element, final String attributeName) {
        if (element == null) {
            throw new IllegalArgumentException("Passed element is null");
        }
        if (attributeName == null) {
            throw new IllegalArgumentException("Passed attribute-name is null");
        }
        return element.getAttribute().stream().filter((attr) -> attr.getName().startsWith(attributeName)).collect(Collectors.toList());
    }
}
