package com.jwa.amlmodel.code.generator.generators.utils;

import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.RoleRequirements;
import org.cdlflex.models.CAEX.SupportedRoleClass;

public final class AmlmodelUtils {
    private AmlmodelUtils() {}

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
