package fr.devcafeine.implement_interface_dart;

import com.intellij.CommonBundle;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.lang.dart.DartFileType;
import fr.devcafeine.implement_interface_dart.fileTemplates.DartTemplateUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

abstract class HelperCreateTestFile {


    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Create Dart test";
    }


    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return file.getFileType().getDefaultExtension().equals(DartFileType.INSTANCE.getDefaultExtension());
    }


    public boolean startInWriteAction() {
        return false;
    }


    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (editor == null || file == null) {
            return;
        }

        try {
            VirtualFile originalFile = file.getOriginalFile().getVirtualFile();
            if (originalFile == null) {
                return;
            }

            String suggestedFileName = DartTemplateUtil.camelToSnake(getFilenameSuggestion(originalFile));
            String newDirectoryName = originalFile.getParent().getPath().replace("lib", "test");
            VirtualFile newDirectory = VfsUtil.createDirectories(newDirectoryName);

            if (newDirectory != null) {
                PsiDirectory newDirReal = PsiDirectoryFactory.getInstance(project).createDirectory(newDirectory);
                String newFilePath = suggestedFileName + "_test";

                if (fileExists(newDirReal, newFilePath)) {
                    VirtualFile existingFile = newDirReal.getVirtualFile().findChild(suggestedFileName + "_test." + DartFileType.INSTANCE.getDefaultExtension());
                    if (existingFile != null) {
                        FileEditorManager.getInstance(project).openFile(existingFile, true);
                    }
                } else {
                    createTestFile(project, newFilePath, newDirReal);
                }
            }
        } catch (IOException e) {
            Messages.showMessageDialog(project, e.getLocalizedMessage(), CommonBundle.getErrorTitle(), Messages.getErrorIcon());
        }
    }


    private boolean fileExists(PsiDirectory psiDirectory, String filename) {
        return Arrays.stream(psiDirectory.getFiles())
                .anyMatch(file -> file.getName().equals(filename + "." + DartFileType.INSTANCE.getDefaultExtension())
                );
    }

    private String getFilenameSuggestion(@NotNull VirtualFile file) {
        return file.getName().replace("." + DartFileType.INSTANCE.getDefaultExtension(), "");
    }

    abstract void createTestFile(Project project, String filename, PsiDirectory psiDirectory);
}
