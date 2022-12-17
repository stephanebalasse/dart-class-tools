package fr.devcafeine.implement_interface_dart;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import com.intellij.psi.PsiElement;

import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.lang.dart.DartTokenTypes;

import com.jetbrains.lang.dart.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ImplementAbstractClass extends PsiElementBaseIntentionAction implements IntentionAction {
    private static final Logger LOG = Logger.getInstance(ImplementAbstractClass.class);
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        final DartClass dartClass = PsiTreeUtil.getParentOfType(element, DartClass.class);

        if (dartClass != null) {
            final List<PsiElement> imports = new ArrayList<>(PsiTreeUtil.collectElementsOfType(dartClass.getParent(), DartImportStatement.class));

            TemplateManager templateManager = TemplateManager.getInstance(project);
            editor.getCaretModel().moveToOffset(dartClass.getTextRange().getEndOffset() + 5);

            List<DartComponent> dartComponents = new ArrayList<>(PsiTreeUtil.collectElementsOfType(PsiTreeUtil.getChildOfType(dartClass, DartClassBody.class), DartMethodDeclaration.class));
            Template template = templateManager.createTemplate(dartClass.getClass().getName(), "Dart");
            template.setToReformat(true);
            template.addTextSegment(PresentableUtil.buildImportText(imports));
            template.addTextSegment("class Text implements ");
            template.addTextSegment(dartClass.getName() != null ? dartClass.getName() : "");
            template.addTextSegment("{\n");
            dartComponents.forEach((dartComponent -> {
                template.addTextSegment(new OverrideImplementMethod(dartClass).buildFunctionsText(templateManager, dartComponent).getTemplateText());
            }));


            template.addTextSegment("}");
            templateManager.startTemplate(editor, template);

        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (!element.getLanguage().getID().equals("Dart")) {
            return false;
        }
        final PsiElement id = element.getParent();
        if (id == null) {
            return false;
        }
        final PsiElement parent = id.getParent();
        if (parent == null) {
            return false;
        }
        final ASTNode node = parent.getNode();
        if (node == null || node.getTreeParent() == null || node.getTreeParent().getElementType() != DartTokenTypes.CLASS_DEFINITION) {
            return false;
        }
        final DartComponent classDefinition = PsiTreeUtil.getParentOfType(element, DartComponent.class);

        return classDefinition != null && classDefinition.isAbstract();
    }

    @NotNull
    public String getText() {
        return "Implement abstract class";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Implement abstract class";
    }
}
