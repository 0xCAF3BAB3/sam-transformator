package com.jwa.amlmodel.code.generator.generators.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CodefilesUtils {
    private CodefilesUtils() {}

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
