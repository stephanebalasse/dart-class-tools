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

import static com.intellij.util.PlatformIcons.CLASS_ICON;


public class CreateDartClassFileAction extends CreateFileFromTemplateAction {
    public CreateDartClassFileAction() {
        super("Dart Class", "Create Dart class", DartIcons.Dart_file);
    }


    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle("New Dart Class File")
                .addKind("Class", CLASS_ICON, "Dart Class File")
                .addKind("Abstract class", CLASS_ICON, "Dart Abstract Class File");
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return DartBundle.message("title.create.dart.file.0", newName);
    }

    @Override
    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {
        return CreateFileFromTemplateAction.createFileFromTemplate(name, template,dir,getDefaultTemplateProperty(),true, Collections.emptyMap());
    }

    @Override
    protected PsiFile createFile(String name, String templateName, PsiDirectory dir) {
        return super.createFile(name, templateName, dir);
    }
}
