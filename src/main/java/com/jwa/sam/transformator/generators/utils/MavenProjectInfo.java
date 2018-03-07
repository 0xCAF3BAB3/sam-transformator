package com.jwa.sam.transformator.generators.utils;

import java.nio.file.Path;

public final class MavenProjectInfo {
    private final String groupId;
    private final String artifactId;
    private final Path directory;
    private final Path pomFile;

    MavenProjectInfo(final String groupId, final String artifactId, final Path directory, final Path pomFile) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.directory = directory;
        this.pomFile = pomFile;
    }

    public final String getGroupId() {
        return groupId;
    }

    public final String getArtifactId() {
        return artifactId;
    }

    public final String getGroupAndArtifactId() {
        return groupId + "." + artifactId;
    }

    public final Path getDirectory() {
        return directory;
    }

    public final Path getPomFile() {
        return pomFile;
    }
}
