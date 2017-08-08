package com.jwa.amlmodel.code.generator.generators.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class CodefileUtils {
    private CodefileUtils() {}

    public static void processFileTemplate(final Path templateFile, final Path outputFile, final Map<String, String> datamodel, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        String content = new String(Files.readAllBytes(templateFile), charset);
        for(Map.Entry<String, String> entry : datamodel.entrySet()) {
            final String placeholder = "{{" + entry.getKey() + "}}";
            content = content.replace(placeholder, entry.getValue());
        }
        Files.write(outputFile, content.getBytes(charset));
    }

    public static class MavenDirectoryStructure {
        private final Path codeDirectory;
        private final Path resourcesDirectory;

        public MavenDirectoryStructure(final Path codeDirectory, final Path resourcesDirectory) {
            this.codeDirectory = codeDirectory;
            this.resourcesDirectory = resourcesDirectory;
        }

        public final Path getCodeDirectory() {
            return codeDirectory;
        }

        public final Path getResourcesDirectory() {
            return resourcesDirectory;
        }
    }
    public static MavenDirectoryStructure generateMavenJavaDirectoryStructure(Path moduleDirectory, String groupId, String artifactId) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        final Path mainDirectory = moduleDirectory.resolve("src").resolve("main");

        Path codeDirectory = mainDirectory.resolve("java");
        for(String groupIdPart : groupId.split(Pattern.quote("."))) {
            codeDirectory = codeDirectory.resolve(groupIdPart);
        }
        codeDirectory = codeDirectory.resolve(artifactId); // TODO: can an artifact-id have multiple parts?
        Files.createDirectories(codeDirectory);

        final Path resourcesDirectory = mainDirectory.resolve("resources");
        Files.createDirectories(resourcesDirectory);

        return new MavenDirectoryStructure(codeDirectory, resourcesDirectory);
    }

    public static void addMavenModule(final String moduleName, final Path pomFile, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(pomFile.toFile());
            final Element modules = (Element) document.getElementsByTagName("modules").item(0);

            final Element newModule = document.createElement("module");
            newModule.setTextContent(moduleName);
            modules.appendChild(newModule);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, charset.displayName()); // TODO: displayname?
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(pomFile.toFile()));
        } catch (TransformerException | SAXException | ParserConfigurationException e) {
            throw new IOException(e);
            // TODO: create a good exception-message
        }
    }

    public static void addMavenDependancy(final String groupId, final String artifactId, final Path pomFile, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(pomFile.toFile());
            Element dependencies = (Element) document.getElementsByTagName("dependencies").item(0);

            final Element newGroupId = document.createElement("groupId");
            newGroupId.setTextContent(groupId);
            final Element newArtifactId = document.createElement("artifactId");
            newArtifactId.setTextContent(artifactId);
            final Element newVersion = document.createElement("version");
            newVersion.setTextContent("${project.version}");
            Element newDependency = document.createElement("dependency");
            newDependency.appendChild(newGroupId);
            newDependency.appendChild(newArtifactId);
            newDependency.appendChild(newVersion);
            dependencies.appendChild(newDependency);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, charset.displayName()); // TODO: displayname?
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(pomFile.toFile()));
        } catch (TransformerException | SAXException | ParserConfigurationException e) {
            throw new IOException(e);
            // TODO: create a good exception-message
        }
    }

    public static boolean mavenModuleExists(final String moduleName, final Path projectDirectory) {
        if (moduleName == null || moduleName.isEmpty()) {
            throw new IllegalArgumentException("Passed module-name is invalid");
        }
        if (!IOUtils.isValidDirectory(projectDirectory)) {
            throw new IllegalArgumentException("Passed project-directory '" + projectDirectory + " doesn't exists or is invalid");
        }
        final Path moduleDirectory = projectDirectory.resolve(moduleName);
        return Files.exists(moduleDirectory) && Files.isDirectory(moduleDirectory);
    }

    public static class MavenModuleStructure {
        private final Path directory;
        private final Path codeDirectory;

        public MavenModuleStructure(final Path directory, final Path codeDirectory) {
            this.directory = directory;
            this.codeDirectory = codeDirectory;
        }

        public final Path getDirectory() {
            return directory;
        }

        public final Path getCodeDirectory() {
            return codeDirectory;
        }
    }
    public static MavenModuleStructure createMavenModule(final String groupId, final String artifactId, final Path projectDirectory, final String pomFileContent, final String logconfigFileContent, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        // create module directory
        final Path moduleDirectory = projectDirectory.resolve(artifactId);
        Files.createDirectory(moduleDirectory);
        // create module pom.xml
        final Path pomFile = moduleDirectory.resolve("pom.xml");
        Files.write(pomFile, pomFileContent.getBytes(charset));
        // generate code-folder
        final Path mainDirectory = moduleDirectory.resolve("src").resolve("main");
        Path codeDirectory = mainDirectory.resolve("java");
        for(String groupIdPart : groupId.split(Pattern.quote("."))) {
            codeDirectory = codeDirectory.resolve(groupIdPart);
        }
        codeDirectory = codeDirectory.resolve(artifactId); // TODO: can an artifact-id have multiple parts?
        Files.createDirectories(codeDirectory);
        // generate resource-folder with log-config
        final Path resourcesDirectory = mainDirectory.resolve("resources");
        Files.createDirectories(resourcesDirectory);
        final Path logconfigFile = resourcesDirectory.resolve("log4j2.xml");
        Files.write(logconfigFile, logconfigFileContent.getBytes(charset));
        // add module to project pom.xml
        final Path projectPomFile = projectDirectory.resolve("pom.xml");
        addMavenModule(artifactId, projectPomFile, charset);
        return new MavenModuleStructure(moduleDirectory, codeDirectory);
    }

    public static void addToMethod(final String statements, final String methodName, final Path file, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        final List<String> lines = Files.readAllLines(file, charset);
        Integer startIndex = null;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains(methodName)) {
                startIndex = i;
                break;
            }
        }
        if (startIndex == null) {
            throw new IOException("Start of method not found");
        }
        Integer endIndex = null;
        for (int i = startIndex; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains("}")) {
                endIndex = i;
                break;
            }
        }
        if (endIndex == null) {
            throw new IOException("End of method not found");
        }
        lines.add(endIndex, statements);
        Files.write(file, lines, charset);
    }

    public static void addToPortConfig(final String content, final String portName, final Path file, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        final List<String> lines = Files.readAllLines(file, charset);
        Integer builderStart = null;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.equals("                \"" + portName + "\",")) {
                builderStart = i + 1;
            }
        }
        if (builderStart == null) {
            throw new IOException("Start of port-config not found");
        }
        Integer builderEnd = null;
        for (int i = builderStart; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.equals("                        .build()")) {
                builderEnd = i;
                break;
            }
        }
        if (builderEnd == null) {
            throw new IOException("End of port-config not found");
        }
        lines.add(builderEnd, content);
        Files.write(file, lines, charset);
    }

    public static void addImport(final String importStatement, final Path file, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        final List<String> lines = Files.readAllLines(file, charset);
        final String importStatementFull = "import " + importStatement + ";";
        boolean exists = false;
        Integer insertPosition = null;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.startsWith("import ")) {
                insertPosition = i + 1;
            }
            if (line.contains(importStatementFull)) {
                exists = true;
                break;
            }
        }
        if (exists) {
            return;
        }
        if (insertPosition == null) {
            throw new IllegalArgumentException("...");
        }
        lines.add(insertPosition, importStatementFull);
        Files.write(file, lines, charset);
    }

    public static void addValueToEnum(final String enumStatement, final String enumName, final Path file) throws IllegalArgumentException, IOException {
        // TODO: add more exception-handling and parameter-checks
        /*
        if (!isValidJavaIdentifier(enumValue)) {
            throw new IllegalArgumentException("Passed enum-value is not a valid Java identifier");
        }
        */
        final Charset usedCharset = StandardCharsets.UTF_8;
        final List<String> lines = Files.readAllLines(file, usedCharset);
        Integer startIndex = null;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains("enum") && line.contains(enumName) && line.contains("{")) {
                startIndex = i;
                break;
            }
        }
        if (startIndex == null) {
            throw new IOException("Start of enum not found");
        }
        Integer endIndex = null;
        for (int i = startIndex; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains(";")) {
                endIndex = i;
                break;
            }
        }
        if (endIndex == null) {
            throw new IOException("End of enum not found");
        }
        final boolean empty = (endIndex == (startIndex + 1));
        if (!empty) {
            final int lastElementIndex = endIndex - 1;
            lines.set(lastElementIndex, lines.get(lastElementIndex) + ",");
        }
        lines.add(endIndex, "        " + enumStatement);
        Files.write(file, lines, usedCharset);
    }

    /*
    // TODO:
    private static boolean isValidJavaIdentifier(final String name) {
        return name.matches("\\b[_a-zA-Z][_a-zA-Z0-9]*\\b");
    }
    */

    public static String toValidJavaIdentifier(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Passed name is invalid");
        }
        // TODO: implement me
        return name.toUpperCase();
    }
}
