package org.chunkmancompletionist.panel;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;
import org.chunkmancompletionist.types.Challenge;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class UITaskSlot extends JPanel {
    private static final Border GREEN = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, (ColorScheme.PROGRESS_COMPLETE_COLOR).darker()),
            BorderFactory.createEmptyBorder(7, 12, 7, 7)
    );

    private static final Border RED = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, (ColorScheme.PROGRESS_ERROR_COLOR).darker()),
            BorderFactory.createEmptyBorder(7, 12, 7, 7)
    );

    private static final Border ORANGE = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, (ColorScheme.PROGRESS_INPROGRESS_COLOR).darker()),
            BorderFactory.createEmptyBorder(7, 12, 7, 7)
    );

    private static final Dimension ICON_SIZE = new Dimension(32, 32);

    private final JShadowedLabel uiLabelName;
    private final JShadowedLabel uiLabelAction;
    private final JPanel uiInfo;

    @Getter(AccessLevel.PACKAGE)
    private boolean isAvailable;

    @Getter(AccessLevel.PACKAGE)
    private boolean isSelected;

    @Getter(AccessLevel.PACKAGE)
    private boolean isOverlapping;

    @Getter(AccessLevel.PACKAGE)
    private final Challenge challenge;

    public UITaskSlot(Challenge task, ClientThread clientThread, ItemManager itemManager, JLabel icon) {
        this.challenge = task;

        setLayout(new BorderLayout());
        setBorder(RED);
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        MouseListener hover = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if(!isSelected) {
                    setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if(!isSelected) {
                    updateBackground();
                }
            }
        };

        addMouseListener(hover);

        uiInfo = new JPanel(new GridLayout(2, 1));
        uiInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        uiInfo.setBorder(new EmptyBorder(0, 5, 0, 0));

        uiLabelName = new JShadowedLabel();
        uiLabelName.setForeground(Color.WHITE);

        uiLabelAction = new JShadowedLabel("Unknown");
        uiLabelAction.setFont(FontManager.getRunescapeSmallFont());
        uiLabelAction.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

        uiInfo.add(uiLabelName);
        uiInfo.add(uiLabelAction);

        if(icon != null) {
            icon.setMinimumSize(ICON_SIZE);
            icon.setMaximumSize(ICON_SIZE);
            icon.setPreferredSize(ICON_SIZE);
            icon.setHorizontalAlignment(JLabel.CENTER);

            add(icon, BorderLayout.LINE_START);
        }

        add(uiInfo, BorderLayout.CENTER);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        this.updateBackground();
    }

    public void setLabel(String text) {
        uiLabelName.setText(text);
    }

    public void setText(String text) {
        uiLabelAction.setText(text);
    }

    private void updateBackground() {
        if (isAvailable) {
            this.setBorder(GREEN);
        } else if (isOverlapping) {
            this.setBorder(ORANGE);
        } else {
            this.setBorder(RED);
        }

        setBackground(this.isSelected() ? ColorScheme.DARKER_GRAY_HOVER_COLOR.brighter() : ColorScheme.DARKER_GRAY_COLOR);
    }
}
