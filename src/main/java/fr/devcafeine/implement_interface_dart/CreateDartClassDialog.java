package fr.devcafeine.implement_interface_dart;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBInsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;


public class CreateDartClassDialog extends DialogWrapper {

    private final JLabel myInformationLabel = new JLabel("#");
    private final JTextField myTfClassName = new MyTextField();
    private final String myClassName;
    private final String myCanonicalPath;

    private final TextFieldWithBrowseButton myInputFile = new TextFieldWithBrowseButton(new MyTextField());


    protected CreateDartClassDialog(@NotNull Project project,
                                    @NotNull String title,
                                    @NotNull String targetClassName,
                                    String canonicalPath
    ) {
        super(project, true);

        myClassName = targetClassName;
        myCanonicalPath = canonicalPath;
        init();
        setTitle(title);
        myInformationLabel.setText("Create class");
        myTfClassName.setText(myClassName);
        myInputFile.setText(canonicalPath);
        myInputFile.addBrowseFolderListener(project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new JPanel(new BorderLayout());
    }

    @Override
    protected JComponent createNorthPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbConstraints = new GridBagConstraints();

        gbConstraints.insets = JBInsets.create(4, 8);
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;


        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        panel.add(myInformationLabel, gbConstraints);
        gbConstraints.insets = JBInsets.create(4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(myTfClassName, gbConstraints);

        myTfClassName.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                getOKAction().setEnabled(StringUtil.isNotEmpty(myTfClassName.getText()) && StringUtil.isNotEmpty(myCanonicalPath));
            }
        });


        gbConstraints.gridy = 3;
        gbConstraints.gridx = 0;
        gbConstraints.gridwidth = 2;
        gbConstraints.insets.top = 12;
        gbConstraints.anchor = GridBagConstraints.WEST;
        gbConstraints.fill = GridBagConstraints.NONE;
        final JBLabel label = new JBLabel("To directory: ");
        panel.add(label, gbConstraints);

        gbConstraints.gridy = 3;
        gbConstraints.gridx = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.insets.top = 12;
        panel.add(myInputFile, gbConstraints);
        myInputFile.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                getOKAction().setEnabled(StringUtil.isNotEmpty(myInputFile.getText()) && StringUtil.isNotEmpty(myClassName));
            }
        });
        getOKAction().setEnabled(StringUtil.isNotEmpty(myClassName) && StringUtil.isNotEmpty(myCanonicalPath));
        return panel;
    }

    private static class MyTextField extends JTextField {
        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            FontMetrics fontMetrics = getFontMetrics(getFont());
            size.width = fontMetrics.charWidth('a') * 80;
            return size;
        }
    }

    protected String getBaseDir() {
        return myInputFile.getText();
    }

    protected String getClassName() {
        return myTfClassName.getText();
    }

}
