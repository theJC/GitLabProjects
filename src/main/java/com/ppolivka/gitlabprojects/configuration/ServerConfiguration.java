package com.ppolivka.gitlabprojects.configuration;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.ppolivka.gitlabprojects.dto.GitlabServer;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class ServerConfiguration extends DialogWrapper {

    private GitlabServer gitlabServer;
    private SettingsState settingsState = SettingsState.getInstance();
    private ExecutorService executor = Executors.newSingleThreadExecutor();


    private JPanel panel;
    private JTextField apiURl;
    private JTextField repositoryUrl;
    private JTextField token;
    private JButton tokenPage;
    private JComboBox checkoutMethod;
    private JCheckBox removeOnMerge;

    protected ServerConfiguration(@Nullable GitlabServer gitlabServer) {
        super(false);
        if (gitlabServer == null) {
            this.gitlabServer = new GitlabServer();
        } else {
            this.gitlabServer = gitlabServer;
        }
        init();
        setTitle("GitLab Server Details");
    }

    @Override
    protected void init() {
        super.init();

        setupModel();
        fillFormFromDto();
        setupListeners();

    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        final String apiUrl = apiURl.getText();
        final String tokenString = token.getText();
        if (StringUtils.isBlank(apiUrl) && StringUtils.isBlank(tokenString)) {
            return null;
        }
        try {
            if (isNotBlank(apiUrl) && isNotBlank(tokenString)) {
                if (!isValidUrl(apiUrl)) {
                    return new ValidationInfo(SettingError.NOT_A_URL.message(), apiURl);
                } else {

                    Future<ValidationInfo> infoFuture = executor.submit(() -> {
                        try {
                            settingsState.isApiValid(apiUrl, tokenString);
                            return null;
                        } catch (UnknownHostException e) {
                            return new ValidationInfo(SettingError.SERVER_CANNOT_BE_REACHED.message(), apiURl);
                        } catch (IOException e) {
                            return new ValidationInfo(SettingError.INVALID_API_TOKEN.message(), apiURl);
                        }
                    });
                    try {
                        ValidationInfo info = infoFuture.get(5000, TimeUnit.MILLISECONDS);
                        return info;
                    } catch (Exception e) {
                        return new ValidationInfo(SettingError.GENERAL_ERROR.message());
                    }
                }
            }
        } catch (Exception e) {
            return new ValidationInfo(SettingError.GENERAL_ERROR.message());
        }
        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        gitlabServer.setApiUrl(apiURl.getText());
        gitlabServer.setApiToken(token.getText());
        if (StringUtils.isNotBlank(repositoryUrl.getText())) {
            gitlabServer.setRepositoryUrl(repositoryUrl.getText());
        } else {
            gitlabServer.setRepositoryUrl(ApiToRepoUrlConverter.convertApiUrlToRepoUrl(apiURl.getText()));
        }
        gitlabServer.setPreferredConnection(GitlabServer.CheckoutType.values()[checkoutMethod.getSelectedIndex()]);
        gitlabServer.setRemoveSourceBranch(removeOnMerge.isSelected());
        settingsState.addServer(gitlabServer);
    }

    private static boolean isValidUrl(String s) {
        try {
            URI uri = new URI(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void setupListeners() {
        tokenPage.addActionListener(e -> openWebPage(generateHelpUrl()));
        onServerChange();
        apiURl.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onServerChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onServerChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onServerChange();
            }
        });
    }

    private void setupModel() {
        checkoutMethod.setModel(new EnumComboBoxModel(GitlabServer.CheckoutType.class));
    }

    private void fillFormFromDto() {
        checkoutMethod.setSelectedIndex(gitlabServer.getPreferredConnection().ordinal());
        removeOnMerge.setSelected(gitlabServer.isRemoveSourceBranch());
        apiURl.setText(gitlabServer.getApiUrl());
        repositoryUrl.setText(gitlabServer.getRepositoryUrl());
        token.setText(gitlabServer.getApiToken());
    }

    private void openWebPage(String uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(uri));
            } catch (Exception ignored) {
            }
        }
    }

    private String generateHelpUrl() {
        final String hostText = apiURl.getText();
        StringBuilder helpUrl = new StringBuilder();
        helpUrl.append(hostText);
        if (!hostText.endsWith("/")) {
            helpUrl.append("/");
        }
        helpUrl.append("profile/personal_access_tokens");
        return helpUrl.toString();
    }

    private void onServerChange() {
        ValidationInfo validationInfo = doValidate();
        if (validationInfo == null || (!validationInfo.message.equals(SettingError.NOT_A_URL.message))) {
            tokenPage.setEnabled(true);
            tokenPage.setToolTipText("API Key can be find in your profile setting inside GitLab Server: \n" + generateHelpUrl());
        } else {
            tokenPage.setEnabled(false);
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(11, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("GitLab UI Server Url (Example: https://gitlab.com/)");
        panel.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        apiURl = new JTextField();
        panel.add(apiURl, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("URL thst is used for repositories (Example: gitlab.com)");
        label2.setToolTipText(" Leave the generated value if same as above.");
        panel.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        repositoryUrl = new JTextField();
        panel.add(repositoryUrl, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("GitLab Personal Access Token (Needs api access scope)");
        panel.add(label3, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        token = new JTextField();
        panel.add(token, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        tokenPage = new JButton();
        tokenPage.setIcon(new ImageIcon(getClass().getResource("/general/web.png")));
        tokenPage.setText("Token Page");
        tokenPage.setToolTipText("API Key can be find in your profile setting inside GitLab Server");
        panel.add(tokenPage, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Prefferred checkout method");
        panel.add(label4, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkoutMethod = new JComboBox();
        panel.add(checkoutMethod, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Default Remove Source Branch when merged");
        panel.add(label5, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeOnMerge = new JCheckBox();
        removeOnMerge.setText("Default Remove Source Branch when merged");
        panel.add(removeOnMerge, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("!!! This is advanced setting. Leave blank in most cases. !!!");
        panel.add(label6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
