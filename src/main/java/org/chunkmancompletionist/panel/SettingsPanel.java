package org.chunkmancompletionist.panel;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.FontManager;
import org.chunkmancompletionist.managers.ChunkTasksManager;
import org.chunkmancompletionist.types.Profile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Getter
@Singleton
@Slf4j
public class SettingsPanel extends JPanel {
    private ChunkTasksManager manager;

    private final JTextField uiFieldStartingChunk;

    @Inject
    SettingsPanel(ChunkTasksManager manager) {
        this.manager = manager;

        setLayout(new GridLayout(4, 1, 7, 7));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        uiFieldStartingChunk = addTextField("Starting chunk");

        updateFields();
    }

    public void updateFields() {
        Profile profile = manager.getProfile();
        if(profile != null) {
            setStartingChunk(profile.startingChunk);
        }

        revalidate();
        repaint();
    }

    public int getStartingChunk() {
        return getIntegerInput(uiFieldStartingChunk);
    }

    public void setStartingChunk(Object value) {
        setTextInput(uiFieldStartingChunk, value);
    }

    private int getIntegerInput(JTextField field) {
        try {
            return Integer.parseInt(field.getText().replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String getStringInput(JTextField field) {
        return field.getText();
    }

    private void setTextInput(JTextField field, Object value) {
        field.setText(String.valueOf(value));
    }

    private JTextField addTextField(String label) {
        final JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        final JLabel uiLabel = new JLabel(label);
        final FlatTextField uiInput = new FlatTextField();

        uiInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        uiInput.setHoverBackgroundColor(ColorScheme.DARKER_GRAY_HOVER_COLOR);
        uiInput.setBorder(new EmptyBorder(5, 7, 5, 7));

        uiLabel.setFont(FontManager.getRunescapeSmallFont());
        uiLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
        uiLabel.setForeground(Color.WHITE);
        uiLabel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        container.add(uiLabel, BorderLayout.NORTH);
        container.add(uiInput, BorderLayout.CENTER);

        add(container);

        return uiInput.getTextField();
    }
}
