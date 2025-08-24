package fr.devcafeine.implement_interface_dart.fileTemplates;

import java.util.Locale;

public class DartTemplateUtil {

    public static String ToUpperCamelCase(String name) {
        String[] words = name.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase());
        }
        return builder.toString();
    }

    public static String camelToSnake(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append('_');
                builder.append(Character.toLowerCase(ch));
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    public static String toLowerCamelCase(String name) {
        if (name == null) return "";
        String s = name.trim();
        s = s.replaceAll("[^A-Za-z0-9]+", " ");
        s = s.replaceAll("([a-z\\d])([A-Z])", "$1 $2");
        s = s.replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2");
        String[] parts = s.trim().isEmpty() ? new String[0] : s.trim().split("\\s+");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.isEmpty()) continue;
            if (i == 0) {
                b.append(p.toLowerCase(Locale.ROOT));
            } else {
                b.append(Character.toUpperCase(p.charAt(0)));
                if (p.length() > 1) b.append(p.substring(1).toLowerCase(Locale.ROOT));
            }
        }

        return b.toString().replaceAll("[^A-Za-z0-9]", "");
    }

}
