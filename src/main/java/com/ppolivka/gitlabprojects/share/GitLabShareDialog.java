package com.ppolivka.gitlabprojects.share;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.ppolivka.gitlabprojects.api.dto.NamespaceDto;
import com.ppolivka.gitlabprojects.configuration.SettingsState;
import com.ppolivka.gitlabprojects.dto.GitlabServer;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ppolivka.gitlabprojects.util.MessageUtil.showErrorDialog;

/**
 * Dialog that is displayed when sharing project to git lab
 *
 * @author ppolivka
 * @since 28.10.2015
 */
public class GitLabShareDialog extends DialogWrapper {

    private static SettingsState settingsState = SettingsState.getInstance();

    private JPanel mainView;
    private JRadioButton isPrivate;
    private JRadioButton isPublic;
    private JTextField projectName;
    private JTextArea commitMessage;
    private JRadioButton isInternal;
    private JComboBox groupList;
    private JButton refreshButton;
    private JRadioButton isSSHAuth;
    private JRadioButton isHTTPAuth;
    private JComboBox serverList;
    private final Project project;

    public GitLabShareDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        init();
    }

    @Override
    protected void init() {
        super.init();
        setTitle("Share on GitLab");
        setOKButtonText("Share");

        ArrayList<GitlabServer> servers = new ArrayList<>(settingsState.getGitlabServers());
        CollectionComboBoxModel collectionComboBoxModel = new CollectionComboBoxModel(servers, servers.get(0));
        serverList.setModel(collectionComboBoxModel);

        Border emptyBorder = BorderFactory.createCompoundBorder();
        refreshButton.setBorder(emptyBorder);

        commitMessage.setText("Initial commit");

        isInternal.setSelected(true);

        ButtonGroup visibilityGroup = new ButtonGroup();
        visibilityGroup.add(isPrivate);
        visibilityGroup.add(isInternal);
        visibilityGroup.add(isPublic);

        isSSHAuth.setSelected(true);

        ButtonGroup authGroup = new ButtonGroup();
        authGroup.add(isHTTPAuth);
        authGroup.add(isSSHAuth);

        reloadGroupList();

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadGroupList();
            }
        });

        serverList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadGroupList();
            }
        });
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(projectName.getText())) {
            return new ValidationInfo("Project name cannot be empty", projectName);
        }
        if (StringUtils.isBlank(commitMessage.getText())) {
            return new ValidationInfo("Initial commit message cannot be empty", commitMessage);
        }
        return null;
    }

    private void reloadGroupList() {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Refreshing group list..") {
            boolean isError = false;

            @Override
            public void run(ProgressIndicator progressIndicator) {
                try {
                    List<NamespaceDto> namespaces = new ArrayList<>();
                    namespaces.add(new NamespaceDto() {{
                        setId(0);
                        setPath("Default");
                    }});
                    List<NamespaceDto> remoteNamespaces = settingsState.api((GitlabServer) serverList.getSelectedItem()).getNamespaces();
                    if (remoteNamespaces != null) {
                        namespaces.addAll(remoteNamespaces);
                    }
                    CollectionComboBoxModel collectionComboBoxModel = new CollectionComboBoxModel(namespaces, namespaces.get(0));
                    groupList.setModel(collectionComboBoxModel);

                } catch (IOException e) {
                    isError = true;
                }
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                if (isError) {
                    showErrorDialog(project, "Groups cannot be refreshed", "Error Loading Groups");
                    close(CLOSE_EXIT_CODE);
                }
            }
        });


    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainView;
    }

    public JRadioButton getIsPrivate() {
        return isPrivate;
    }

    public JRadioButton getIsPublic() {
        return isPublic;
    }

    public JTextField getProjectName() {
        return projectName;
    }

    public JTextArea getCommitMessage() {
        return commitMessage;
    }

    public JRadioButton getIsInternal() {
        return isInternal;
    }

    public JComboBox getGroupList() {
        return groupList;
    }

    public JRadioButton getIsSSHAuth() {
        return isSSHAuth;
    }

    public JRadioButton getIsHTTPAuth() {
        return isHTTPAuth;
    }

    public JComboBox getServerList() {
        return serverList;
    }

    public void setServerList(JComboBox serverList) {
        this.serverList = serverList;
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
        mainView = new JPanel();
        mainView.setLayout(new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1));
        projectName = new JTextField();
        mainView.add(projectName, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Group:");
        mainView.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        commitMessage = new JTextArea();
        mainView.add(commitMessage, new GridConstraints(4, 1, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Name:");
        mainView.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Message:");
        label3.setToolTipText("Initial Commit Message");
        mainView.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isPrivate = new JRadioButton();
        isPrivate.setText("Private");
        mainView.add(isPrivate, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isInternal = new JRadioButton();
        isInternal.setText("Internal");
        mainView.add(isInternal, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainView.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainView.add(panel1, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        groupList = new JComboBox();
        panel1.add(groupList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, -1), new Dimension(300, -1), null, 0, false));
        refreshButton = new JButton();
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/actions/refresh.png")));
        refreshButton.setText("");
        refreshButton.setToolTipText("Resfresh groups");
        panel1.add(refreshButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        isPublic = new JRadioButton();
        isPublic.setText("Public");
        mainView.add(isPublic, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isSSHAuth = new JRadioButton();
        isSSHAuth.setText("SSH");
        mainView.add(isSSHAuth, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isHTTPAuth = new JRadioButton();
        isHTTPAuth.setText("HTTP");
        mainView.add(isHTTPAuth, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainView.add(spacer3, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Auth Type:");
        mainView.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Gitlab Server:");
        mainView.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serverList = new JComboBox();
        mainView.add(serverList, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainView;
    }
}
