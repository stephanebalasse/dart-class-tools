package fr.devcafeine.implement_interface_dart.fileTemplates;

import com.intellij.ide.fileTemplates.DefaultCreateFromTemplateHandler;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.JavaTemplateUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.util.ArrayUtil;
import com.jetbrains.lang.dart.DartFileType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DartCreateFromTemplateHandler extends DefaultCreateFromTemplateHandler {
    public static final String ATTRIBUT_CLASS_NAME_DART_UPPER = "CLASS_NAME_UPPER";

    @Override
    public boolean handlesTemplate(@NotNull FileTemplate template) {
        FileType fileType = FileTypeManagerEx.getInstanceEx().getFileTypeByExtension(template.getExtension());
        return fileType.equals(DartFileType.INSTANCE) && !ArrayUtil.contains(template.getName(), JavaTemplateUtil.INTERNAL_FILE_TEMPLATES);
    }

    @Override
    public void prepareProperties(@NotNull Map<String, Object> props,
                                  String filename,
                                  @NotNull FileTemplate template,
                                  @NotNull Project project) {
        props.put(ATTRIBUT_CLASS_NAME_DART_UPPER, DartTemplateUtil.ToUpperCamelCase(filename));
        super.prepareProperties(props, filename, template, project);
    }


}
