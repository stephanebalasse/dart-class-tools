package fr.devcafeine.implement_interface_dart;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.lang.dart.DartFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Action to expose "Introduce constant" under Refactor > Introduce, just after "Introduce Variable".
 * It delegates to the existing ExtractDartConstantIntention implementation.
 */
public class IntroduceDartConstantAction extends AnAction implements DumbAware {

    private final IntroduceDartConstantIntention intention = new IntroduceDartConstantIntention();

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setText("Introduce Constant...");
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        boolean enabled = project != null
                && editor != null
                && file != null
                && file.getFileType().getDefaultExtension().equals(DartFileType.INSTANCE.getDefaultExtension())
                && intention.isAvailable(project, editor, file);
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (project == null || editor == null || file == null) return;
        if (!file.getFileType().getDefaultExtension().equals(DartFileType.INSTANCE.getDefaultExtension())) return;

        intention.invoke(project, editor, file);
    }
}
