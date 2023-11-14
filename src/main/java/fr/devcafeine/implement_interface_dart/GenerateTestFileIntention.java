package fr.devcafeine.implement_interface_dart;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class GenerateTestFileIntention extends HelperCreateTestFile implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Create or open test file";
    }


    @Override
    void createTestFile(Project project, String filename, PsiDirectory psiDirectory) {
        final FileTemplateManager templateManager = FileTemplateManager.getInstance(project);
        FileTemplate template = templateManager.getInternalTemplate("Dart Test File");
        Properties properties = templateManager.getDefaultProperties();
        try {
            FileTemplateUtil.createFromTemplate(template, filename, properties, psiDirectory);
        } catch (Exception exception) {
            System.out.println("Error creating the test file");
        }
    }
}
