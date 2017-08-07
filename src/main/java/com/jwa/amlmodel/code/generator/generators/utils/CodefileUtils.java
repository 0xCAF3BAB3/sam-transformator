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

    public static void addMavenModule(final String modulname, final Path pomFile, final Charset charset) throws IOException {
        // TODO: add more exception-handling and parameter-checks
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(pomFile.toFile());
            Element modulesElement = (Element) document.getElementsByTagName("modules").item(0);

            Element newElement = document.createElement("module");
            newElement.setTextContent(modulname);
            modulesElement.appendChild(newElement);

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

    public static void addValueToEnum(final String enumValue, final String enumName, final Path file) throws IllegalArgumentException, IOException {
        if (!isValidJavaIdentifier(enumValue)) {
            throw new IllegalArgumentException("Passed enum-value is not a valid Java identifier");
        }
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
            if (line.contains("}")) {
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
        lines.add(endIndex, "        " + enumValue);
        Files.write(file, lines, usedCharset);
    }

    public static boolean isValidJavaIdentifier(final String name) {
        return name.matches("\\b[_a-zA-Z][_a-zA-Z0-9]*\\b");
    }

    public static String toValidJavaIdentifier(final String name) {
        // TODO: implement me
        return name;
    }
}
