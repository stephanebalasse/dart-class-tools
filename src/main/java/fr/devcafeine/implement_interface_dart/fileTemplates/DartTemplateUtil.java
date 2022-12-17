package fr.devcafeine.implement_interface_dart.fileTemplates;

public class DartTemplateUtil {

    public static String ToUpperCamelCase(String name) {
        String[] words = name.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase());
        }
        return builder.toString();
    }

}
