package fr.devcafeine.implement_interface_dart;

import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.lang.dart.ide.generation.BaseCreateMethodsFix;
import com.jetbrains.lang.dart.psi.*;
import com.jetbrains.lang.dart.psi.impl.DartMethodDeclarationImpl;
import com.jetbrains.lang.dart.util.DartPresentableUtil;
import org.jetbrains.annotations.NotNull;

public class OverrideImplementMethod extends BaseCreateMethodsFix<DartComponent> {

    public OverrideImplementMethod(@NotNull DartClass dartClass) {
        super(dartClass);
    }

    @Override
    protected @NotNull String getNothingFoundMessage() {
        return "No methods were found to implement";
    }

    @Override
    @NotNull
    public Template buildFunctionsText(TemplateManager templateManager, DartComponent element) {
        Template template = templateManager.createTemplate(this.getClass().getName(), "Dart");
        if (element.getFirstChild() != null
                && element.getFirstChild().getParent() != null
                && ((DartMethodDeclarationImpl) element.getFirstChild().getParent()).isConstructor()
        ) {
            return template;
        }
        template.setToReformat(true);
        template.addTextSegment("@override\n");
        boolean isField = element instanceof DartVarAccessDeclaration || element instanceof DartVarDeclarationListPart;
        if (isField && element.isFinal()) {
            template.addTextSegment("final");
            template.addTextSegment(" ");
        }

        DartReturnType returnType = PsiTreeUtil.getChildOfType(element, DartReturnType.class);
        DartType dartType = PsiTreeUtil.getChildOfType(element, DartType.class);
        if (returnType != null) {
            template.addTextSegment(DartPresentableUtil.buildTypeText(element, returnType, this.specializations));
            template.addTextSegment(" ");
        } else if (dartType != null) {
            template.addTextSegment(DartPresentableUtil.buildTypeText(element, dartType, this.specializations));
            template.addTextSegment(" ");
        }

        if (isField) {
            if (returnType == null && dartType == null) {
                template.addTextSegment("var");
                template.addTextSegment(" ");
            }

            template.addTextSegment(element.getName());
            if (element.isFinal()) {
                template.addTextSegment(" ");
                template.addTextSegment("=");
                template.addTextSegment(" ");
                template.addTextSegment("null");
            }

            template.addTextSegment("; ");
        } else {

            if (element.isOperator()) {
                template.addTextSegment("operator ");
            }

            if (element.isGetter() || element.isSetter()) {
                template.addTextSegment(element.isGetter() ? "get " : "set ");
            }

            template.addTextSegment(element.getName());
            if (!element.isGetter()) {
                template.addTextSegment("(");
                template.addTextSegment(DartPresentableUtil.getPresentableParameterList(element, this.specializations, false, true, true));
                template.addTextSegment(")");
            }

            template.addTextSegment("{\n");
            template.addTextSegment(" // TODO: implement ");
            template.addTextSegment(element.getName());
            template.addTextSegment("\n");
            template.addTextSegment("throw UnimplementedError();");
            template.addTextSegment("\n} ");
        }
        return template;
    }
}
