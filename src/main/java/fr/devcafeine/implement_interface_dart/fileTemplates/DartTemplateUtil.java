package fr.devcafeine.implement_interface_dart.fileTemplates;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.lang.dart.psi.DartClass;
import com.jetbrains.lang.dart.psi.DartClassBody;
import com.jetbrains.lang.dart.psi.DartComponent;
import com.jetbrains.lang.dart.psi.DartMethodDeclaration;
import com.jetbrains.lang.dart.util.DartUrlResolver;
import fr.devcafeine.implement_interface_dart.OverrideImplementMethod;
import fr.devcafeine.implement_interface_dart.PresentableUtil;

import java.util.ArrayList;
import java.util.List;

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

    public static Template generateTemplateImplementationAbstractClass(
            DartClass dartClass,
            DartUrlResolver dartUrlResolver,
            List<PsiElement> imports,
            TemplateManager templateManager,
            String className
    ) {
        final Template template = templateManager.createTemplate(dartClass.getClass().getName(), "Dart");
        List<DartComponent> dartComponents = new ArrayList<>(PsiTreeUtil.collectElementsOfType(PsiTreeUtil.getChildOfType(dartClass, DartClassBody.class), DartMethodDeclaration.class));
        template.setToReformat(true);
        template.addTextSegment("import '" + dartUrlResolver.getDartUrlForFile(dartClass.getContainingFile().getVirtualFile()) + "';");
        template.addTextSegment(PresentableUtil.buildImportText(imports));
        template.addTextSegment("class " + className + " implements ");
        template.addTextSegment(dartClass.getName() != null ? dartClass.getName() : "");
        template.addTextSegment("{\n");
        dartComponents.forEach((dartComponent -> {
            template.addTextSegment(new OverrideImplementMethod(dartClass).buildFunctionsText(templateManager, dartComponent).getTemplateText());
        }));
        template.addTextSegment("}");
        return template;
    }

}
