package com.jwa.amlmodel.code.generator.generators.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

    public static Path processFileTemplate(final Path templateFile, final Path outputFile, final Map<String, String> datamodel, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        String content = new String(Files.readAllBytes(templateFile), charset);
        for(Map.Entry<String, String> entry : datamodel.entrySet()) {
            final String placeholder = "{{" + entry.getKey() + "}}";
            content = content.replace(placeholder, entry.getValue());
        }
        Files.write(outputFile, content.getBytes(charset));
        return outputFile;
    }

    public static String processFileTemplate(final Path templateFile, final Map<String, String> datamodel, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        String content = new String(Files.readAllBytes(templateFile), charset);
        for(Map.Entry<String, String> entry : datamodel.entrySet()) {
            final String placeholder = "{{" + entry.getKey() + "}}";
            content = content.replace(placeholder, entry.getValue());
        }
        return content;
    }

    public static Path processTemplate(final Template template, final Map<String, String> datamodel, final Path file, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        try (final Writer writer = Files.newBufferedWriter(file, charset)) {
            template.process(datamodel, writer);
            return file;
        } catch (IOException | TemplateException e) {
            throw new IOException("Failed to generate file '" + file + "': " + e.getMessage(), e);
        }
    }

    public static String processTemplate(final Template template, final Map<String, String> datamodel, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        try (final Writer writer = new StringWriter()) {
            template.process(datamodel, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new IOException("Failed to generate content from template: " + e.getMessage(), e);
        }
    }

    /*
    public static MavenModuleInfo generateMavenJavaDirectoryStructure(Path moduleDirectory, String groupId, String artifactId) throws IOException {
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

        return new MavenModuleInfo(codeDirectory, resourcesDirectory);
    }
    */

    public static boolean hasMavenDependancy(final String groupId, final String artifactId, final Path pomFile, final Charset charset) throws IOException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(pomFile.toFile());
            NodeList nodes = document.getElementsByTagName("dependency");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element dependency = (Element) nodes.item(i);
                final String dependencyGroupId = dependency.getElementsByTagName("groupId").item(0).getTextContent();
                if (dependencyGroupId.equals(groupId)) {
                    final String dependencyArtifactId = dependency.getElementsByTagName("artifactId").item(0).getTextContent();
                    if (dependencyArtifactId.equals(artifactId)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
            // TODO: create a good exception-message
        }
    }

    public static void addMavenDependancy(final MavenModuleInfo dependency, final MavenModuleInfo module, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        final Path modulePomFile = module.getPomFile();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(modulePomFile.toFile());
            Element dependencies = (Element) document.getElementsByTagName("dependencies").item(0);

            final Element newGroupId = document.createElement("groupId");
            newGroupId.setTextContent(dependency.getMavenProjectInfo().getGroupAndArtifactId());
            final Element newArtifactId = document.createElement("artifactId");
            newArtifactId.setTextContent(dependency.getArtifactId());
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
            transformer.setOutputProperty(OutputKeys.ENCODING, charset.name());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(modulePomFile.toFile()));
        } catch (TransformerException | SAXException | ParserConfigurationException e) {
            throw new IOException(e);
            // TODO: create a good exception-message
        }
    }

    public static MavenProjectInfo createMavenProject(final String groupId, final String artifactId, final Path projectDirectory, final String pomFileContent, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        // create directory
        Files.createDirectory(projectDirectory);
        // create pom.xml
        final Path pomFile = projectDirectory.resolve("pom.xml");
        Files.write(pomFile, pomFileContent.getBytes(charset));
        return new MavenProjectInfo(groupId, artifactId, projectDirectory, pomFile);
    }

    public static MavenModuleInfo createMavenModule(final String artifactId, final MavenProjectInfo mavenProjectInfo, final String pomFileContent, final String logconfigFileContent, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        if (isMavenModuleExisting(artifactId, mavenProjectInfo)) {
            throw new IllegalArgumentException("Module already existing");
        }
        // create directory
        final Path directory = mavenProjectInfo.getDirectory().resolve(artifactId);
        Files.createDirectory(directory);
        // create pom.xml
        final Path pomFile = directory.resolve("pom.xml");
        Files.write(pomFile, pomFileContent.getBytes(charset));
        // generate code-folder
        final Path mainDirectory = directory.resolve("src").resolve("main");
        Path codeDirectory = mainDirectory.resolve("java");
        for(String groupIdPart : mavenProjectInfo.getGroupAndArtifactId().split(Pattern.quote("."))) {
            codeDirectory = codeDirectory.resolve(groupIdPart);
        }
        codeDirectory = codeDirectory.resolve(artifactId); // TODO: can an artifact-id have multiple parts?
        Files.createDirectories(codeDirectory);
        // generate resource-folder with log-config
        final Path resourcesDirectory = mainDirectory.resolve("resources");
        Files.createDirectories(resourcesDirectory);
        final Path logconfigFile = resourcesDirectory.resolve("log4j2.xml");
        Files.write(logconfigFile, logconfigFileContent.getBytes(charset));
        MavenModuleInfo mavenModuleInfo = new MavenModuleInfo(mavenProjectInfo, artifactId, directory, pomFile, codeDirectory, resourcesDirectory);
        addMavenModuleToProject(mavenModuleInfo, charset);
        return mavenModuleInfo;
    }

    private static boolean isMavenModuleExisting(final String moduleName, final MavenProjectInfo mavenProjectInfo) {
        if (moduleName == null || moduleName.isEmpty()) {
            throw new IllegalArgumentException("Passed module-name is invalid");
        }
        if (mavenProjectInfo == null) {
            throw new IllegalArgumentException("Passed maven-project-info is invalid");
        }
        final Path moduleDirectory = mavenProjectInfo.getDirectory().resolve(moduleName);
        return Files.exists(moduleDirectory) && Files.isDirectory(moduleDirectory);
    }

    private static void addMavenModuleToProject(final MavenModuleInfo mavenModuleInfo, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        final Path projectPomFile = mavenModuleInfo.getMavenProjectInfo().getPomFile();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(projectPomFile.toFile());
            final Element modules = (Element) document.getElementsByTagName("modules").item(0);

            final Element newModule = document.createElement("module");
            newModule.setTextContent(mavenModuleInfo.getArtifactId());
            modules.appendChild(newModule);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, charset.name());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(projectPomFile.toFile()));
        } catch (TransformerException | SAXException | ParserConfigurationException e) {
            throw new IOException(e);
            // TODO: create a good exception-message
        }
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

    // TODO: refactor this method (port should not be part of its name)
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

    public static void addValueToEnum(final String enumStatement, final String enumName, final Path file, final Charset charset) throws IllegalArgumentException, IOException {
        // TODO: add more exception-handling and parameter-checks
        /*
        if (!isValidJavaIdentifier(enumValue)) {
            throw new IllegalArgumentException("Passed enum-value is not a valid Java identifier");
        }
        */
        final List<String> lines = Files.readAllLines(file, charset);
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
        Files.write(file, lines, charset);
    }

    public static void adaptPackageAndImportNames(final Path baseDirectory, final String oldPackageName, final String newPackageName, Charset charset) throws IOException {
        Files.walkFileTree(baseDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path path, final BasicFileAttributes mainAtts) throws IOException {
                final List<String> lines = Files.readAllLines(path, charset);
                for (int i = 0; i < lines.size(); i++) {
                    final String line = lines.get(i);
                    if (line.startsWith("package " + oldPackageName)) {
                        lines.set(i, line.replace(oldPackageName, newPackageName));
                        break; // there should exists only one package-statement
                    }
                }
                for (int i = 0; i < lines.size(); i++) {
                    final String line = lines.get(i);
                    if (line.startsWith("import " + oldPackageName)) {
                        lines.set(i, line.replace(oldPackageName, newPackageName));
                    }
                }
                Files.write(path, lines, charset);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(final Path path, final IOException exc) throws IOException {
                return FileVisitResult.TERMINATE;
            }
        });
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
