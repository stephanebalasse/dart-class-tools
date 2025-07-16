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
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.lang.dart.DartFileType;
import com.jetbrains.lang.dart.DartTokenTypes;
import com.jetbrains.lang.dart.psi.*;
import com.jetbrains.lang.dart.util.DartUrlResolver;
import fr.devcafeine.implement_interface_dart.fileTemplates.DartTemplateUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ImplementAbstractClass extends PsiElementBaseIntentionAction implements IntentionAction {

    private static final Logger log = LoggerFactory.getLogger(ImplementAbstractClass.class);

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        final DartClass dartClass = PsiTreeUtil.getParentOfType(element, DartClass.class);
        if (dartClass != null) {

            List<String> genericTypes = getGenericTypes(dartClass);

            CreateDartDialog dialog = new CreateDartDialog(
                    project,
                    "Implement Class",
                    dartClass.getName() + "Impl",
                    dartClass.getContainingFile().getVirtualFile().getParent().getCanonicalPath(),
                    genericTypes
            );

            if (dialog.showAndGet()) {
                try {
                    final PsiManager psiManager = PsiManager.getInstance(project);
                    final DartUrlResolver dartUrlResolver = DartUrlResolver.getInstance(project, dartClass.getContainingFile().getVirtualFile());
                    final List<PsiElement> imports = new ArrayList<>(PsiTreeUtil.collectElementsOfType(dartClass.getParent(), DartImportStatement.class));
                    final TemplateManager templateManager = TemplateManager.getInstance(project);
                    final String className = dialog.getClassName();
                    final String dirPath = dialog.getBaseDir();
                    final Map<String, String> genericTypeMappings = dialog.getGenericTypeMappings();

                    VirtualFile virtualFile = VfsUtil.createDirectories(dirPath);
                    final PsiDirectory psiDirectory = psiManager.findDirectory(virtualFile);

                    Template template = generateTemplateImplementationAbstractClass(
                            project,
                            dartClass,
                            dartUrlResolver,
                            imports,
                            templateManager,
                            className,
                            genericTypeMappings
                    );

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

    public static List<String> getGenericTypes(DartClass dartClass) {
        List<String> result = new ArrayList<>();
        PsiElement[] children = dartClass.getChildren();

        for (PsiElement child : children) {
            if (child instanceof DartTypeParameters) {
                for (PsiElement param : child.getChildren()) {
                    if (param instanceof DartTypeParameter) {
                        PsiElement identifier = param.getFirstChild();
                        if (identifier != null) {
                            result.add(identifier.getText());
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Template generateTemplateImplementationAbstractClass(
            Project project,
            DartClass dartClass,
            DartUrlResolver dartUrlResolver,
            List<PsiElement> imports,
            TemplateManager templateManager,
            String className,
            Map<String, String> genericTypeMappings
    ) {
        final Template template = templateManager.createTemplate(dartClass.getClass().getName(), "Dart");
        DartClassBody classBody = PsiTreeUtil.getChildOfType(dartClass, DartClassBody.class);

        List<String> genericReplacements = new ArrayList<>();
        List<String> originalOrder = getGenericTypes(dartClass);
        for (String key : originalOrder) {
            if (genericTypeMappings.containsKey(key)) {
                genericReplacements.add(genericTypeMappings.get(key));
            }
        }
        for (Map.Entry<String, String> entry : genericTypeMappings.entrySet()) {
            Collection<VirtualFile> dartFiles = FilenameIndex.getAllFilesByExt(project, "dart", GlobalSearchScope.projectScope(project));
            for (VirtualFile file : dartFiles) {
                String fileName = file.getNameWithoutExtension();
                if (fileName.equalsIgnoreCase(toSnakeCase(entry.getValue()))) {
                    String dartUrl = DartUrlResolver.getInstance(project, file).getDartUrlForFile(file);
                    template.addTextSegment("import '" + dartUrl + "';\n");
                    break;
                }
            }
        }

        template.addTextSegment("import '" + dartUrlResolver.getDartUrlForFile(dartClass.getContainingFile().getVirtualFile()) + "';\n");
        template.addTextSegment(PresentableUtil.buildImportText(imports));

        template.addTextSegment("class " + className + " implements " + dartClass.getName());
        if (!genericReplacements.isEmpty()) {
            template.addTextSegment("<" + String.join(", ", genericReplacements) + ">");
        }
        template.addTextSegment(" {\n");

        if (classBody != null) {
            List<DartMethodDeclaration> methods = PsiTreeUtil.getChildrenOfTypeAsList(classBody, DartMethodDeclaration.class);
            for (DartMethodDeclaration method : methods) {
                String methodName = Objects.requireNonNull(method.getComponentName()).getText();
                String returnType = method.getReturnType() != null ? method.getReturnType().getText() : "void";
                String parameters = method.getFormalParameterList().getText();

                template.addTextSegment("  @override\n");
                template.addTextSegment("  " + returnType + " " + methodName + parameters + " {\n");
                template.addTextSegment("    // TODO: implement " + methodName + "\n");
                template.addTextSegment("    throw UnimplementedError();\n");
                template.addTextSegment("  }\n");
            }
        }

        template.addTextSegment("}\n");
        return template;
    }



    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {

        if (!element.getLanguage().getID().equalsIgnoreCase(DartFileType.INSTANCE.getDefaultExtension())) {
            return false;
        }
        final PsiElement id = element.getParent();
        if (id == null || id.getParent() == null) {
            return false;
        }
        final PsiElement parent = id.getParent();
        final ASTNode node = parent.getNode();
        if (node == null || node.getTreeParent() == null || node.getTreeParent().getFirstChildNode() == null || node.getTreeParent().getFirstChildNode().getPsi() == null) {
            return false;
        }
        return canBeImplemented(node.getTreeParent().getFirstChildNode().getPsi());

    }

    @NotNull
    public String getText() {
        return "Implement class";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Implement class";
    }

    private boolean canBeImplemented(PsiElement element) {
        if (element instanceof LeafPsiElement) {
            final IElementType elementType = ((LeafPsiElement) element).getElementType();
            if (elementType == DartTokenTypes.BASE || elementType == DartTokenTypes.FINAL || elementType == DartTokenTypes.SEALED) {
                return false;
            }
        }
        if (element.getNextSibling() != null) {
            return canBeImplemented(element.getNextSibling());
        }
        return true;
    }

    private static String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}

