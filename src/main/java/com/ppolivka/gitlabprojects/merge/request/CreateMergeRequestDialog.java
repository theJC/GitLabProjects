package com.ppolivka.gitlabprojects.merge.request;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.SortedComboBoxModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.ppolivka.gitlabprojects.component.SearchBoxModel;
import com.ppolivka.gitlabprojects.configuration.ProjectState;
import com.ppolivka.gitlabprojects.merge.info.BranchInfo;
import org.apache.commons.lang.StringUtils;
import org.gitlab.api.models.GitlabUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for creating merge requests
 *
 * @author ppolivka
 * @since 30.10.2015
 */
public class CreateMergeRequestDialog extends DialogWrapper {

    private Project project;

    private JPanel mainView;
    private JComboBox targetBranch;
    private JLabel currentBranch;
    private JTextField mergeTitle;
    private JTextArea mergeDescription;
    private JButton diffButton;
    private JComboBox assigneeBox;
    private JCheckBox removeSourceBranch;
    private JCheckBox wip;

    private SortedComboBoxModel<BranchInfo> myBranchModel;
    private BranchInfo lastSelectedBranch;

    final ProjectState projectState;

    @NotNull
    final GitLabCreateMergeRequestWorker mergeRequestWorker;

    public CreateMergeRequestDialog(@Nullable Project project, @NotNull GitLabCreateMergeRequestWorker gitLabMergeRequestWorker) {
        super(project);
        this.project = project;
        projectState = ProjectState.getInstance(project);
        mergeRequestWorker = gitLabMergeRequestWorker;
        init();

    }

    @Override
    protected void init() {
        super.init();
        setTitle("Create Merge Request");
        setVerticalStretch(2f);

        SearchBoxModel searchBoxModel = new SearchBoxModel(assigneeBox, mergeRequestWorker.getSearchableUsers());
        assigneeBox.setModel(searchBoxModel);
        assigneeBox.setEditable(true);
        assigneeBox.addItemListener(searchBoxModel);
        assigneeBox.setBounds(140, 170, 180, 20);

        currentBranch.setText(mergeRequestWorker.getGitLocalBranch().getName());

        myBranchModel = new SortedComboBoxModel<>((o1, o2) -> StringUtil.naturalCompare(o1.getName(), o2.getName()));
        myBranchModel.setAll(mergeRequestWorker.getBranches());
        targetBranch.setModel(myBranchModel);
        targetBranch.setSelectedIndex(0);
        if (mergeRequestWorker.getLastUsedBranch() != null) {
            targetBranch.setSelectedItem(mergeRequestWorker.getLastUsedBranch());
        }
        lastSelectedBranch = getSelectedBranch();

        targetBranch.addActionListener(e -> {
            prepareTitle();
            lastSelectedBranch = getSelectedBranch();
            projectState.setLastMergedBranch(getSelectedBranch().getName());
            mergeRequestWorker.getDiffViewWorker().launchLoadDiffInfo(mergeRequestWorker.getLocalBranchInfo(), getSelectedBranch());
        });

        prepareTitle();

        Boolean deleteMergedBranch = projectState.getDeleteMergedBranch();
        if (deleteMergedBranch != null && deleteMergedBranch) {
            this.removeSourceBranch.setSelected(true);
        }

        Boolean mergeAsWorkInProgress = projectState.getMergeAsWorkInProgress();
        if (mergeAsWorkInProgress != null && mergeAsWorkInProgress) {
            this.wip.setSelected(true);
        }

        diffButton.addActionListener(e -> mergeRequestWorker.getDiffViewWorker().showDiffDialog(mergeRequestWorker.getLocalBranchInfo(), getSelectedBranch()));
    }

    @Override
    protected void doOKAction() {
        BranchInfo branch = getSelectedBranch();
        if (mergeRequestWorker.checkAction(branch)) {
            String title = mergeTitle.getText();
            if (wip.isSelected()) {
                title = "WIP:" + title;
            }
            mergeRequestWorker.createMergeRequest(branch, getAssignee(), title, mergeDescription.getText(), removeSourceBranch.isSelected());
            super.doOKAction();
        }
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(mergeTitle.getText())) {
            return new ValidationInfo("Merge title cannot be empty", mergeTitle);
        }
        if (getSelectedBranch().getName().equals(currentBranch.getText())) {
            return new ValidationInfo("Target branch must be different from current branch.", targetBranch);
        }
        return null;
    }

    private BranchInfo getSelectedBranch() {
        return (BranchInfo) targetBranch.getSelectedItem();
    }

    @Nullable
    private GitlabUser getAssignee() {
        SearchableUser searchableUser = (SearchableUser) this.assigneeBox.getSelectedItem();
        if (searchableUser != null) {
            return searchableUser.getGitLabUser();
        }
        return null;
    }

    private void prepareTitle() {
        if (StringUtils.isBlank(mergeTitle.getText()) || mergeTitleGenerator(lastSelectedBranch).equals(mergeTitle.getText())) {
            mergeTitle.setText(mergeTitleGenerator(getSelectedBranch()));
        }
    }

    private String mergeTitleGenerator(BranchInfo branchInfo) {
        return "Merge of " + currentBranch.getText() + " to " + branchInfo;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainView;
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
        mainView.setLayout(new GridLayoutManager(7, 4, new Insets(0, 0, 0, 0), -1, -1));
        targetBranch = new JComboBox();
        mainView.add(targetBranch, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("To Branch:");
        label1.setDisplayedMnemonic('B');
        label1.setDisplayedMnemonicIndex(3);
        mainView.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("From Branch:");
        mainView.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Title:");
        label3.setDisplayedMnemonic('T');
        label3.setDisplayedMnemonicIndex(0);
        mainView.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mergeTitle = new JTextField();
        mainView.add(mergeTitle, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Description:");
        label4.setDisplayedMnemonic('E');
        label4.setDisplayedMnemonicIndex(1);
        mainView.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainView.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        diffButton = new JButton();
        diffButton.setIcon(new ImageIcon(getClass().getResource("/diff/Diff.png")));
        diffButton.setText("Diff");
        diffButton.setMnemonic('D');
        diffButton.setDisplayedMnemonicIndex(0);
        mainView.add(diffButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainView.add(scrollPane1, new GridConstraints(4, 1, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        mergeDescription = new JTextArea();
        scrollPane1.setViewportView(mergeDescription);
        currentBranch = new JLabel();
        currentBranch.setText("current branch name placeholder");
        mainView.add(currentBranch, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainView.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Assignee:");
        label5.setDisplayedMnemonic('A');
        label5.setDisplayedMnemonicIndex(0);
        mainView.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        assigneeBox = new JComboBox();
        mainView.add(assigneeBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeSourceBranch = new JCheckBox();
        removeSourceBranch.setText("Remove Source Branch (After Merge)");
        removeSourceBranch.setMnemonic('R');
        removeSourceBranch.setDisplayedMnemonicIndex(0);
        mainView.add(removeSourceBranch, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wip = new JCheckBox();
        wip.setText("WIP");
        wip.setMnemonic('W');
        wip.setDisplayedMnemonicIndex(0);
        wip.setToolTipText("Work In Progress");
        mainView.add(wip, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1.setLabelFor(targetBranch);
        label3.setLabelFor(mergeTitle);
        label4.setLabelFor(mergeDescription);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainView;
    }
}
