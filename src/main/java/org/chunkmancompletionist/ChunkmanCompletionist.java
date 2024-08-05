package org.chunkmancompletionist;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import org.chunkmancompletionist.types.ChunkmanTabType;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class ChunkmanCompletionist extends JPanel {
    private final Client client;
    private final ClientThread clientThread;
    private final SpriteManager spriteManager;
    private final ItemManager itemManager;

    private final IconTextField searchBar = new IconTextField();

    private ChunkmanTabType currentType;

    @Inject
    ChunkmanCompletionist(Client client, ClientThread clientThread, SpriteManager spriteManager, ItemManager itemManager) {
        this.client = client;
        this.clientThread = clientThread;
        this.spriteManager = spriteManager;
        this.itemManager = itemManager;

        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);

        setLayout(new DynamicGridLayout(0, 1, 0, 5));
    }

    public void openTab(ChunkmanTabType type, boolean forceReload) {
        if(forceReload || currentType != type) {
            currentType = type;

            removeAll();
            searchBar.setText(null);

            add(searchBar);

            revalidate();
            repaint();
        }
    }
}
