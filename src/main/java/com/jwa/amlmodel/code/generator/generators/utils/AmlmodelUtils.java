package com.jwa.amlmodel.code.generator.generators.utils;

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

public final class AmlmodelUtils {
    private AmlmodelUtils() {}

    public static CAEXFile deserialize(final Path amlmodelFile) throws IOException {
        final Deserializer<DocumentRoot> deserializer = new AmlDeserializer();
        final DocumentRoot documentRoot;
        try (final InputStream in = Files.newInputStream(amlmodelFile)) {
            documentRoot = deserializer.deserialize(in);
        }
        return documentRoot.getCAEXFile();
    }

    public static boolean hasRoleStartingWith(final InternalElement element, final String roleStartingWith) {
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

    public static String getRoleStartingWith(final InternalElement element, final String roleStartingWith) {
        final RoleRequirements roleRequirements = element.getRoleRequirements();
        if (roleRequirements != null && roleRequirements.getRefBaseRoleClassPath().startsWith(roleStartingWith)) {
            return roleRequirements.getRefBaseRoleClassPath().replace(roleStartingWith, "");
        }
        for(SupportedRoleClass supportedRoleClass : element.getSupportedRoleClass()) {
            if (supportedRoleClass.getRefRoleClassPath().startsWith(roleStartingWith)) {
                return supportedRoleClass.getRefRoleClassPath().replace(roleStartingWith, "");
            }
        }
        return null;
    }
}
