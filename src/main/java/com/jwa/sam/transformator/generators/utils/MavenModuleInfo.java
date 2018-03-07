package com.jwa.sam.transformator.generators.utils;

import java.nio.file.Path;

public final class MavenModuleInfo {
    private final MavenProjectInfo mavenProjectInfo;
    private final String artifactId;
    private final Path directory;
    private final Path pomFile;
    private final Path codeDirectory;
    private final Path resourcesDirectory;

    MavenModuleInfo(final MavenProjectInfo mavenProjectInfo, final String artifactId, final Path directory, final Path pomFile, final Path codeDirectory, final Path resourcesDirectory) {
        this.mavenProjectInfo = mavenProjectInfo;
        this.artifactId = artifactId;
        this.directory = directory;
        this.pomFile = pomFile;
        this.codeDirectory = codeDirectory;
        this.resourcesDirectory = resourcesDirectory;
    }

    public final MavenProjectInfo getMavenProjectInfo() {
        return mavenProjectInfo;
    }

    public final String getArtifactId() {
        return artifactId;
    }

    public final String getGroupAndArtifactId() {
        return mavenProjectInfo.getGroupAndArtifactId() + "." + artifactId;
    }

    public final Path getDirectory() {
        return directory;
    }

    public final Path getPomFile() {
        return pomFile;
    }

    public final Path getCodeDirectory() {
        return codeDirectory;
    }

    public final Path getResourcesDirectory() {
        return resourcesDirectory;
    }
}
