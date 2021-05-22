package com.ppolivka.gitlabprojects.component;

import com.ppolivka.gitlabprojects.merge.request.EmptyUser;
import com.ppolivka.gitlabprojects.merge.request.SearchableUser;
import com.ppolivka.gitlabprojects.merge.request.SearchableUsers;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Searchable ComboBox model with autocomplete and background loading
 *
 * @author ppolivka
 * @since 1.4.0
 */
public class SearchBoxModel extends AbstractListModel implements ComboBoxModel, KeyListener, ItemListener {
    private JComboBox comboBox;
    private transient ComboBoxEditor comboBoxEditor;
    private SearchableUsers searchableUsers;

    private List<SearchableUser> data = new ArrayList<>();
    private SearchableUser selectedUser = null;

    private volatile long lastKeyPressTime = 0L;
    private transient Timer timer;

    private String lastQuery = "";

    public SearchBoxModel(JComboBox comboBox, SearchableUsers searchableUsers) {
        this.comboBox = comboBox;
        this.comboBoxEditor = comboBox.getEditor();
        this.comboBoxEditor.getEditorComponent().addKeyListener(this);
        this.searchableUsers = searchableUsers;
        this.data.addAll(searchableUsers.getInitialModel());
        timer = new Timer();
    }

    private void updateModel(String in) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if((System.currentTimeMillis() - lastKeyPressTime) > 200) {
                        if(in != null && !"".equals(in) && !in.equals(lastQuery)) {
                            data.clear();
                            data = Arrays.asList(new EmptyUser(in), new EmptyUser("loading..."));
                            dataChanged();
                            data = new ArrayList<>();
                            data.add(new EmptyUser(in));
                            data.addAll(searchableUsers.search(in));
                            lastQuery = in;
                            dataChanged();
                        } else if (in == null || "".equals(in)){
                            data.clear();
                            data.addAll(searchableUsers.getInitialModel());
                            dataChanged();
                        }
                    }
                });
            }
        }, 200);
    }

    private void dataChanged() {
        super.fireContentsChanged(this, 0, data.size());
        comboBox.hidePopup();
        comboBox.showPopup();
        if (!data.isEmpty()) {
            comboBox.setSelectedIndex(0);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        comboBoxEditor.setItem(e.getItem()); //to string was here
        comboBox.setSelectedItem(e.getItem());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //noop
    }

    @Override
    public void keyPressed(KeyEvent e) {
        lastKeyPressTime = System.currentTimeMillis();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String str = comboBoxEditor.getItem().toString();
        JTextField jtf = (JTextField) comboBoxEditor.getEditorComponent();
        int currentPosition = jtf.getCaretPosition();

        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
            if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                comboBoxEditor.setItem(str);
                jtf.setCaretPosition(currentPosition);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            comboBox.setSelectedIndex(comboBox.getSelectedIndex());
        } else {
            updateModel(comboBox.getEditor().getItem().toString());
            comboBoxEditor.setItem(str);
            jtf.setCaretPosition(currentPosition);
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if(anItem instanceof SearchableUser) {
            this.selectedUser = (SearchableUser) anItem;
        }
    }

    @Override
    public Object getSelectedItem() {
        return selectedUser;
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int index) {
        return data.get(index);
    }
}
