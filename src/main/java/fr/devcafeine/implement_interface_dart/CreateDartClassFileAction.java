package fr.devcafeine.implement_interface_dart;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.jetbrains.lang.dart.DartBundle;
import icons.DartIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static com.intellij.util.PlatformIcons.*;


public class CreateDartClassFileAction extends CreateFileFromTemplateAction {
    public CreateDartClassFileAction() {
        super("Dart Class", "Create Dart class", DartIcons.Dart_file);
    }


    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle("New Dart Class File")
                .addKind("Class", CLASS_ICON, "Dart Class File")
                .addKind("Base class", CLASS_ICON, "Dart Base Class File")
                .addKind("Interface class", INTERFACE_ICON, "Dart Interface Class File")
                .addKind("Final class",  CLASS_ICON, "Dart Final Class File")
                .addKind("Sealed class", CLASS_ICON, "Dart Sealed Class File")
                .addKind("Abstract class", ABSTRACT_CLASS_ICON, "Dart Abstract Class File")
                .addKind("Abstract base class", ABSTRACT_CLASS_ICON, "Dart Abstract Base Class File")
                .addKind("Abstract interface class", ABSTRACT_CLASS_ICON, "Dart Abstract Interface Class File")
                .addKind("Abstract final class", ABSTRACT_CLASS_ICON, "Dart Abstract Final Class File");
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return DartBundle.message("title.create.dart.file.0", newName);
    }

    @Override
    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {
        return CreateFileFromTemplateAction.createFileFromTemplate(name, template, dir, getDefaultTemplateProperty(), true, Collections.emptyMap());
    }

    @Override
    protected PsiFile createFile(String name, String templateName, PsiDirectory dir) {
        return super.createFile(name, templateName, dir);
    }
}
