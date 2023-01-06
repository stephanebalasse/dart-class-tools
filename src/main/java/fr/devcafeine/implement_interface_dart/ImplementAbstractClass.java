package fr.devcafeine.implement_interface_dart;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.lang.dart.DartTokenTypes;
import com.jetbrains.lang.dart.psi.DartClass;
import com.jetbrains.lang.dart.psi.DartComponent;
import com.jetbrains.lang.dart.psi.DartImportStatement;
import com.jetbrains.lang.dart.util.DartUrlResolver;
import fr.devcafeine.implement_interface_dart.fileTemplates.DartTemplateUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImplementAbstractClass extends PsiElementBaseIntentionAction implements IntentionAction {

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        final DartClass dartClass = PsiTreeUtil.getParentOfType(element, DartClass.class);
        if (dartClass != null) {
            CreateDartClassDialog dialog = new CreateDartClassDialog(
                    project,
                    "Implement Abstract Class",
                    dartClass.getName() + "Impl",
                    dartClass.getContainingFile().getVirtualFile().getParent().getCanonicalPath()
            );

            if (dialog.showAndGet()) {
                try {
                    final PsiManager psiManager = PsiManager.getInstance(project);
                    final DartUrlResolver dartUrlResolver = DartUrlResolver.getInstance(project, dartClass.getContainingFile().getVirtualFile());
                    final List<PsiElement> imports = new ArrayList<>(PsiTreeUtil.collectElementsOfType(dartClass.getParent(), DartImportStatement.class));
                    final TemplateManager templateManager = TemplateManager.getInstance(project);
                    final String className = dialog.getClassName();
                    final String dirPath = dialog.getBaseDir();

                    VirtualFile virtualFile = VfsUtil.createDirectories(dirPath);
                    final PsiDirectory psiDirectory = psiManager.findDirectory(virtualFile);

                    Template template = DartTemplateUtil.generateTemplateImplementationAbstractClass(dartClass, dartUrlResolver, imports, templateManager, className);

                    FileTemplate fileTemplate = FileTemplateUtil.createTemplate("template", "dart", template.getTemplateText(), new FileTemplate[]{});
                    if (psiDirectory != null) {
                        CreateDartClassFileAction.createFileFromTemplate(DartTemplateUtil.camelToSnake(className), fileTemplate, psiDirectory, "", true);
                    }

                } catch (IncorrectOperationException | IOException e) {
                    Messages.showMessageDialog(project, e.getLocalizedMessage(), CommonBundle.getErrorTitle(), Messages.getErrorIcon());
                }
            }
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

        return classDefinition != null;
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

