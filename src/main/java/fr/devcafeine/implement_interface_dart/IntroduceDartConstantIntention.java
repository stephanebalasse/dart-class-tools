package fr.devcafeine.implement_interface_dart;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.lang.dart.DartFileType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.devcafeine.implement_interface_dart.fileTemplates.DartTemplateUtil.toLowerCamelCase;

public class IntroduceDartConstantIntention implements IntentionAction {

    @Override
    public @NotNull String getText() {
        return "Introduce constant";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Introduce constant";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (file == null || editor == null) return false;
        if (!file.getFileType().getDefaultExtension().equals(DartFileType.INSTANCE.getDefaultExtension())) return false;
        SelectionModel selectionModel = editor.getSelectionModel();
        return selectionModel.hasSelection();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (!isAvailable(project, editor, file)) return;

        SelectionModel selectionModel = editor.getSelectionModel();
        if (!selectionModel.hasSelection()) return;

        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null || selectedText.trim().isEmpty()) return;

        String suggestedName = suggestConstName(selectedText);
        String constName = Messages.showInputDialog(project,
                "Const name (lowerCamelCase):",
                "Introduce Constant",
                Messages.getQuestionIcon(),
                suggestedName,
                null);

        if (constName == null) return;
        constName = toLowerCamelCase(constName);
        if (!constName.matches("[a-z][A-Za-z0-9]*")) {
            Messages.showErrorDialog(project, "Invalid constant name.", "Introduce Constant");
            return;
        }

        String finalConstName = constName;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = editor.getDocument();
            PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);

            int insertOffset = findInsertOffset(document.getText());
            String prefix = insertOffset > 0 ? "\n" : "";
            String constLine = String.format(Locale.ROOT, "%sconst %s = %s;\n", prefix, finalConstName, selectedText);
            document.insertString(insertOffset, constLine);

            int start = selectionModel.getSelectionStart();
            int end = selectionModel.getSelectionEnd();
            document.replaceString(start, end, finalConstName);

            psiDocumentManager.commitDocument(document);
        });
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    private static int findInsertOffset(String text) {
        Pattern p = Pattern.compile("^(\n|\r|\r\n)*((import|export|part) .*$\n?)+", Pattern.MULTILINE);
        Matcher m = p.matcher(text);
        if (m.find() && m.start() == 0) {
            return m.end();
        }
        return 0;
    }

    private static String suggestConstName(String selectedText) {
        String t = selectedText.trim();
        if (t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length() - 1);
        } else if (t.startsWith("'") && t.endsWith("'")) {
            t = t.substring(1, t.length() - 1);
        }
        if (t.isEmpty()) return "newConstant";
        String camel = toLowerCamelCase(t);
        if (camel.matches("[0-9].*")) {
            camel = "c" + camel;
        }
        return camel;
    }
}
