package fr.devcafeine.implement_interface_dart;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateDartDialog extends DialogWrapper {

    private final JPanel panel = new JPanel();
    private final JTextField classNameField = new JTextField();
    private final JTextField baseDirField = new JTextField();
    private final Map<String, JTextField> genericFields = new HashMap<>();

    public CreateDartDialog(Project project, String title, String defaultClassName, String baseDir, List<String> genericTypes) {
        super(project);
        setTitle(title);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Nom de la classe :"));
        classNameField.setText(defaultClassName);
        panel.add(classNameField);

        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Répertoire de destination :"));
        baseDirField.setText(baseDir);
        panel.add(baseDirField);

        if (!genericTypes.isEmpty()) {
            panel.add(Box.createVerticalStrut(15));
            panel.add(new JLabel("Types génériques à remplacer :"));
            for (String type : genericTypes) {
                JPanel row = new JPanel(new BorderLayout());
                JLabel label = new JLabel(type + " → ");
                JTextField field = new JTextField("My" + type);
                genericFields.put(type, field);
                row.add(label, BorderLayout.WEST);
                row.add(field, BorderLayout.CENTER);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                panel.add(row);
                panel.add(Box.createVerticalStrut(5));
            }
        }

        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    public String getClassName() {
        return classNameField.getText().trim();
    }

    public String getBaseDir() {
        return baseDirField.getText().trim();
    }

    public Map<String, String> getGenericTypeMappings() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, JTextField> entry : genericFields.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getText().trim());
        }
        return result;
    }
}
