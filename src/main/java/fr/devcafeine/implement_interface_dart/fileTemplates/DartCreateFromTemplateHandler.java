package fr.devcafeine.implement_interface_dart.fileTemplates;

import com.intellij.ide.fileTemplates.DefaultCreateFromTemplateHandler;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.project.Project;
import com.jetbrains.lang.dart.DartFileType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DartCreateFromTemplateHandler extends DefaultCreateFromTemplateHandler {
    public static final String ATTRIBUT_CLASS_NAME_DART_UPPER = "CLASS_NAME_UPPER";

    @Override
    public boolean handlesTemplate(@NotNull FileTemplate template) {
        FileType fileType = FileTypeManagerEx.getInstanceEx().getFileTypeByExtension(template.getExtension());
        List<String> internalTemplates = getInternalFileTemplateNames();

        return fileType.equals(DartFileType.INSTANCE) && !internalTemplates.contains(template.getName());

    }

    @Override
    public void prepareProperties(@NotNull Map<String, Object> props,
                                  String filename,
                                  @NotNull FileTemplate template,
                                  @NotNull Project project) {
        props.put(ATTRIBUT_CLASS_NAME_DART_UPPER, DartTemplateUtil.ToUpperCamelCase(filename));
    }

    private List<String> getInternalFileTemplateNames() {
        FileTemplateManager templateManager = FileTemplateManager.getDefaultInstance();
        FileTemplate[] allTemplates = templateManager.getAllTemplates();

        return Arrays.asList(
                Arrays.stream(allTemplates)
                        .map(FileTemplate::getName)
                        .toArray(String[]::new)
        );
    }


}
