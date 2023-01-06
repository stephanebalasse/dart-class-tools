package fr.devcafeine.implement_interface_dart;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class PresentableUtil {
    public static @NotNull String buildImportText(@Nullable List<PsiElement> imports) {
        StringBuilder result = new StringBuilder();
        if(imports != null && !imports.isEmpty()){
            imports.forEach((_import) -> {
                result.append(_import.getText());
            });
        }
        return result.toString();
    }
}
