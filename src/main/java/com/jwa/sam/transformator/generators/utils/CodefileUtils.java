package com.jwa.sam.transformator.generators.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public final class CodefileUtils {
    private CodefileUtils() {}

    public static String processTemplate(final Path templateFile, final Map<String, String> datamodel, final Charset charset) throws IOException {
        if (!IOUtils.isValidFile(templateFile)) {
            throw new IllegalArgumentException("Passed path '" + templateFile + "' is no valid file");
        }
        if (datamodel == null) {
            throw new IllegalArgumentException("Passed datamodel is null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        String content = new String(Files.readAllBytes(templateFile), charset);
        for(Map.Entry<String, String> entry : datamodel.entrySet()) {
            final String fileTemplateExpression = "{{" + entry.getKey() + "}}";
            content = content.replace(fileTemplateExpression, entry.getValue());
        }
        return content;
    }

    public static Path processTemplate(final Template template, final Map<String, String> datamodel, final Path outputFile, final Charset charset) throws IOException {
        if (template == null) {
            throw new IllegalArgumentException("Passed template is null");
        }
        if (datamodel == null) {
            throw new IllegalArgumentException("Passed datamodel is null");
        }
        if (outputFile == null) {
            throw new IllegalArgumentException("Passed output-file is null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        try (final Writer writer = Files.newBufferedWriter(outputFile, charset)) {
            template.process(datamodel, writer);
            return outputFile;
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    public static String processTemplate(final Template template, final Map<String, Object> datamodel, final Charset charset) throws IOException {
        if (template == null) {
            throw new IllegalArgumentException("Passed template is null");
        }
        if (datamodel == null) {
            throw new IllegalArgumentException("Passed datamodel is null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        try (final Writer writer = new StringWriter()) {
            template.process(datamodel, writer);
            return writer.toString();
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    public static String rtrim(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Passed String instance is null");
        }
        int i = s.length() - 1;
        while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
            i--;
        }
        return s.substring(0, i + 1);
    }

    public static void addMavenDependency(final MavenModuleInfo dependency, final MavenModuleInfo module, final Charset charset) throws IOException {
        if (dependency == null) {
            throw new IllegalArgumentException("Passed dependency is null");
        }
        if (module == null) {
            throw new IllegalArgumentException("Passed module is null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        final Path pomFile = module.getPomFile();
        final Document pomDocument = loadXmlDocument(pomFile);
        final String dependenciesTagname = "dependencies";
        final NodeList dependenciesNodes = pomDocument.getElementsByTagName(dependenciesTagname);
        if (dependenciesNodes.getLength() != 1) {
            throw new IOException("Not exactly 1 '" + dependenciesTagname + "' element found");
        }
        final Element dependencies = (Element) dependenciesNodes.item(0);
        final Element newGroupId = pomDocument.createElement("groupId");
        newGroupId.setTextContent(dependency.getMavenProjectInfo().getGroupAndArtifactId());
        final Element newArtifactId = pomDocument.createElement("artifactId");
        newArtifactId.setTextContent(dependency.getArtifactId());
        final Element newVersion = pomDocument.createElement("version");
        newVersion.setTextContent("${project.version}");
        final Element newDependency = pomDocument.createElement("dependency");
        newDependency.appendChild(newGroupId);
        newDependency.appendChild(newArtifactId);
        newDependency.appendChild(newVersion);
        dependencies.appendChild(newDependency);
        persistXmlDocument(pomDocument, pomFile, charset);
    }

    private static Document loadXmlDocument(final Path file) throws IOException {
        if (!IOUtils.isValidFile(file)) {
            throw new IllegalArgumentException("Passed file '" + file + "' is no valid file");
        }
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(file.toFile());
            document.normalizeDocument();
            return document;
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    private static void persistXmlDocument(final Document document, final Path file, final Charset charset) throws IOException {
        if (document == null) {
            throw new IllegalArgumentException("Passed document is null");
        }
        if (!IOUtils.isValidFile(file)) {
            throw new IllegalArgumentException("Passed file '" + file + "' is no valid file");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        final XPath xPath = XPathFactory.newInstance().newXPath();
        final NodeList nodeList;
        try {
            nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new IOException(e);
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            node.getParentNode().removeChild(node);
        }
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final int indent = 4;
        transformerFactory.setAttribute("indent-number", indent);
        try {
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, charset.name());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indent));
            transformer.transform(new DOMSource(document), new StreamResult(file.toFile()));
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

    public static MavenProjectInfo createMavenProject(final String groupId, final String artifactId, final Path outputDirectory, final String pomFileContent, final Charset charset) throws IOException {
        if (!isValidMavenGroupId(groupId)) {
            throw new IllegalArgumentException("Passed group-id '" + groupId + "' is invalid");
        }
        if (!isValidMavenArtifactId(artifactId)) {
            throw new IllegalArgumentException("Passed artifact-id '" + artifactId + "' is invalid");
        }
        if (outputDirectory == null) {
            throw new IllegalArgumentException("Passed output-directory is null");
        }
        if (pomFileContent == null || pomFileContent.isEmpty()) {
            throw new IllegalArgumentException("Passed POM file-content is invalid");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        Files.createDirectories(outputDirectory);
        final Path pomFile = outputDirectory.resolve("pom.xml");
        Files.write(pomFile, pomFileContent.getBytes(charset));
        return new MavenProjectInfo(groupId, artifactId, outputDirectory, pomFile);
    }

    public static MavenModuleInfo createMavenModule(final String artifactId, final MavenProjectInfo mavenProjectInfo, final String pomFileContent, final String loggerConfigFileContent, final Charset charset) throws IOException {
        if (!isValidMavenArtifactId(artifactId)) {
            throw new IllegalArgumentException("Passed artifact-id '" + artifactId + "' is invalid");
        }
        if (mavenProjectInfo == null) {
            throw new IllegalArgumentException("Passed maven-project-info is null");
        }
        if (pomFileContent == null || pomFileContent.isEmpty()) {
            throw new IllegalArgumentException("Passed POM file-content is invalid");
        }
        if (loggerConfigFileContent == null || loggerConfigFileContent.isEmpty()) {
            throw new IllegalArgumentException("Passed logger-config file-content is invalid");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        if (isMavenModuleExisting(artifactId, mavenProjectInfo)) {
            throw new IllegalArgumentException("Module '" + artifactId + "' already existing");
        }
        final Path directory = mavenProjectInfo.getDirectory().resolve(artifactId);
        Files.createDirectory(directory);
        final Path pomFile = directory.resolve("pom.xml");
        Files.write(pomFile, pomFileContent.getBytes(charset));
        final Path mainDirectory = directory.resolve("src").resolve("main");
        Path codeDirectory = mainDirectory.resolve("java");
        for(String groupIdPart : mavenProjectInfo.getGroupAndArtifactId().split(Pattern.quote("."))) {
            codeDirectory = codeDirectory.resolve(groupIdPart);
        }
        codeDirectory = codeDirectory.resolve(artifactId);
        Files.createDirectories(codeDirectory);
        final Path resourcesDirectory = mainDirectory.resolve("resources");
        Files.createDirectories(resourcesDirectory);
        final Path loggerConfigFile = resourcesDirectory.resolve("log4j2.xml");
        Files.write(loggerConfigFile, loggerConfigFileContent.getBytes(charset));
        final MavenModuleInfo mavenModuleInfo = new MavenModuleInfo(mavenProjectInfo, artifactId, directory, pomFile, codeDirectory, resourcesDirectory);
        addMavenModuleToProject(mavenModuleInfo, charset);
        return mavenModuleInfo;
    }

    private static boolean isMavenModuleExisting(final String artifactId, final MavenProjectInfo mavenProjectInfo) {
        if (artifactId == null || artifactId.isEmpty()) {
            throw new IllegalArgumentException("Passed artifact-id is invalid");
        }
        if (mavenProjectInfo == null) {
            throw new IllegalArgumentException("Passed maven-project-info is null");
        }
        final Path moduleDirectory = mavenProjectInfo.getDirectory().resolve(artifactId);
        return Files.exists(moduleDirectory) && Files.isDirectory(moduleDirectory);
    }

    private static void addMavenModuleToProject(final MavenModuleInfo mavenModuleInfo, final Charset charset) throws IOException {
        if (mavenModuleInfo == null) {
            throw new IllegalArgumentException("Passed maven-module-info is null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        final Path pomFile = mavenModuleInfo.getMavenProjectInfo().getPomFile();
        final Document pomDocument = loadXmlDocument(pomFile);
        final String modulesTagname = "modules";
        final NodeList modulesNodes = pomDocument.getElementsByTagName(modulesTagname);
        if (modulesNodes.getLength() != 1) {
            throw new IOException("Not exactly 1 '" + modulesTagname + "' element found");
        }
        final Element modules = (Element) modulesNodes.item(0);
        final Element newModule = pomDocument.createElement("module");
        newModule.setTextContent(mavenModuleInfo.getArtifactId());
        modules.appendChild(newModule);
        persistXmlDocument(pomDocument, pomFile, charset);
    }

    public static void appendStatementsToMethod(final String statements, final String methodName, final Path file, final Charset charset) throws IOException {
        if (statements == null || statements.isEmpty()) {
            throw new IllegalArgumentException("Passed statements is invalid");
        }
        if (methodName == null || methodName.isEmpty()) {
            throw new IllegalArgumentException("Passed method-name is invalid");
        }
        if (!IOUtils.isValidFile(file)) {
            throw new IllegalArgumentException("Passed file '" + file + "' is no valid file");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        final List<String> lines = Files.readAllLines(file, charset);
        Integer indexMethodHead = null;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains(methodName) && line.contains("{")) {
                indexMethodHead = i;
                break;
            }
        }
        if (indexMethodHead == null) {
            throw new IOException("Head of method '" + methodName + "' not found");
        }
        Integer indexMethodTail = null;
        for (int i = indexMethodHead; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains("}")) {
                indexMethodTail = i;
                break;
            }
        }
        if (indexMethodTail == null) {
            throw new IOException("Tail of method '" + methodName + "' not found");
        }
        lines.add(indexMethodTail, statements);
        Files.write(file, lines, charset);
    }

    public static void insertStatementsToMethod(final String statements, final String methodName, final String headStatement, final String tailStatement, final Path file, final Charset charset) throws IOException {
        if (statements == null || statements.isEmpty()) {
            throw new IllegalArgumentException("Passed statements is invalid");
        }
        if (methodName == null || methodName.isEmpty()) {
            throw new IllegalArgumentException("Passed method-name is invalid");
        }
        if (headStatement == null || headStatement.isEmpty()) {
            throw new IllegalArgumentException("Passed head-statement is invalid");
        }
        if (tailStatement == null || tailStatement.isEmpty()) {
            throw new IllegalArgumentException("Passed tail-statement is invalid");
        }
        if (!IOUtils.isValidFile(file)) {
            throw new IllegalArgumentException("Passed file '" + file + "' is not valid file");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        final List<String> lines = Files.readAllLines(file, charset);
        Integer indexMethodHead = null;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains(methodName) && line.contains("{")) {
                indexMethodHead = i;
                break;
            }
        }
        if (indexMethodHead == null) {
            throw new IOException("Head of method '" + methodName + "' not found");
        }
        Integer indexMethodTail = null;
        for (int i = indexMethodHead; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains("}")) {
                indexMethodTail = i;
                break;
            }
        }
        if (indexMethodTail == null) {
            throw new IOException("Tail of method '" + methodName + "' not found");
        }
        // now search between method head and tail
        Integer indexHeadStatement = null;
        for (int i = (indexMethodHead + 1); i < indexMethodTail; i++) {
            final String line = lines.get(i);
            if (line.contains(headStatement)) {
                indexHeadStatement = i;
                break;
            }
        }
        if (indexHeadStatement == null) {
            throw new IOException("Head-statement not found within method '" + methodName + "' (between line " + (indexMethodHead + 1) + " and " + indexMethodTail + ")");
        }
        Integer indexTailStatement = null;
        for (int i = (indexHeadStatement + 1); i < indexMethodTail; i++) {
            final String line = lines.get(i);
            if (line.contains(tailStatement)) {
                indexTailStatement = i;
                break;
            }
        }
        if (indexTailStatement == null) {
            throw new IOException("Tail-statement not found within method '" + methodName + "' (between line " + (indexHeadStatement + 1) + " and " + indexMethodTail + ")");
        }
        lines.add(indexTailStatement, statements);
        Files.write(file, lines, charset);
    }

    public static void addImportStatement(final String importStatement, final Path file, final Charset charset) throws IOException {
        if (importStatement == null || importStatement.isEmpty()) {
            throw new IllegalArgumentException("Passed import-statement is invalid");
        }
        if (!IOUtils.isValidFile(file)) {
            throw new IllegalArgumentException("Passed file '" + file + "' is no valid file");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        final List<String> lines = Files.readAllLines(file, charset);
        final String importStatementFull = "import " + importStatement + ";";
        boolean exists = false;
        Integer indexLastImportStatement = null;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.startsWith("import ") && line.endsWith(";")) {
                indexLastImportStatement = i;
            }
            if (line.contains(importStatementFull)) {
                exists = true;
                break;
            }
        }
        if (exists) {
            return;
        }
        if (indexLastImportStatement == null) {
            throw new IOException("No import-statements found");
        }
        lines.add(indexLastImportStatement + 1, importStatementFull);
        lines.add(indexLastImportStatement + 1, "");
        Files.write(file, lines, charset);
    }

    public static void addValueToEnum(final String enumStatement, final String enumName, final Path file, final Charset charset) throws IOException {
        if (enumStatement == null || enumStatement.isEmpty()) {
            throw new IllegalArgumentException("Passed enum-statement is invalid");
        }
        if (enumName == null || enumName.isEmpty()) {
            throw new IllegalArgumentException("Passed enum-name is invalid");
        }
        if (!IOUtils.isValidFile(file)) {
            throw new IllegalArgumentException("Passed file '" + file + "' is no valid file");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        final List<String> lines = Files.readAllLines(file, charset);
        Integer indexEnumHead = null;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains("enum") && line.contains(enumName) && line.contains("{")) {
                indexEnumHead = i;
                break;
            }
        }
        if (indexEnumHead == null) {
            throw new IOException("Head of enum '" + enumName + "' not found");
        }
        Integer indexEnumValuesTail = null;
        for (int i = indexEnumHead; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.contains(";")) {
                indexEnumValuesTail = i;
                break;
            }
        }
        if (indexEnumValuesTail == null) {
            throw new IOException("Values-tail of enum '" + enumName + "' not found");
        }
        final boolean hasValues = (indexEnumValuesTail > (indexEnumHead + 1));
        if (hasValues) {
            final int indexLastEnumValue = indexEnumValuesTail - 1;
            lines.set(indexLastEnumValue, lines.get(indexLastEnumValue) + ",");
        }
        final String indent = getLeadingWhitespaces(lines.get(indexEnumValuesTail));
        lines.add(indexEnumValuesTail, indent + enumStatement);
        Files.write(file, lines, charset);
    }

    private static String getLeadingWhitespaces(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Passed String instance is null");
        }
        final StringBuilder leadingWhitespaces = new StringBuilder();
        for(Character c : s.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                break;
            }
            leadingWhitespaces.append(c);
        }
        return leadingWhitespaces.toString();
    }

    public static void adaptPackageAndImportNames(final Path baseDirectory, final String oldPackageName, final String newPackageName, Charset charset) throws IOException {
        if (!IOUtils.isValidDirectory(baseDirectory)) {
            throw new IllegalArgumentException("Passed base-directory '" + baseDirectory + "' is no valid directory");
        }
        if (oldPackageName == null || oldPackageName.isEmpty()) {
            throw new IllegalArgumentException("Passed old-package-name is invalid");
        }
        if (newPackageName == null || newPackageName.isEmpty()) {
            throw new IllegalArgumentException("Passed new-package-name is invalid");
        }
        if (charset == null) {
            throw new IllegalArgumentException("Passed charset is null");
        }
        Files.walkFileTree(baseDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path path, final BasicFileAttributes mainAtts) throws IOException {
                final List<String> lines = Files.readAllLines(path, charset);
                for (int i = 0; i < lines.size(); i++) {
                    final String line = lines.get(i);
                    if (line.startsWith("package " + oldPackageName)) {
                        lines.set(i, line.replace(oldPackageName, newPackageName));
                        break; // there should exist only one package-statement
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

    private static boolean isValidJavaIdentifier(final String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return false;
        }
        final char[] chars = identifier.toCharArray();
        if (!Character.isJavaIdentifierStart(chars[0])) {
            return false;
        }
        for (int i = 1; i < chars.length; i++) {
            if (!Character.isJavaIdentifierPart(chars[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidMavenGroupId(final String groupId) {
        return groupId != null && Pattern.matches("[a-z]+[a-z0-9.]*", groupId) && !groupId.endsWith(".");
    }

    private static boolean isValidMavenArtifactId(final String artifactId) {
        return artifactId != null && Pattern.matches("[a-z]+[a-z0-9]*", artifactId);
    }

    public static String toValidEnumValue(final String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Passed value is invalid");
        }
        String validValue = value;
        validValue = validValue.toUpperCase();
        validValue = validValue.replace(" ", "_");
        if (!isValidJavaIdentifier(validValue)) {
            validValue = "VALUE_" + value.hashCode();
        }
        return validValue;
    }
}
