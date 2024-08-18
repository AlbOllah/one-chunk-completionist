package org.chunkmancompletionist.panel;

import com.google.inject.Singleton;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.plugins.hiscore.HiscorePlugin;
import net.runelite.client.plugins.info.InfoPlugin;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;
import org.chunkmancompletionist.*;
import com.google.inject.Inject;
import org.chunkmancompletionist.managers.ChunkTasksManager;
import org.chunkmancompletionist.types.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Singleton
@Slf4j
public class ChunkmanCompletionistPanel extends PluginPanel {
    private final ChunkmanCompletionist completionist;
    private final SkillIconManager iconManager;
    @Getter private final MaterialTabGroup tabGroup;
    private final ImageIcon configIcon = new ImageIcon(ImageUtil.loadImageResource(ConfigPlugin.class, "pluginhub_configure.png"));
    private final ImageIcon rulesIcon = new ImageIcon(ImageUtil.loadImageResource(InfoPlugin.class, "wiki_icon.png"));
    private final ImageIcon chunkIcon = new ImageIcon(ImageUtil.loadImageResource(WorldHopperPlugin.class, "icon.png"));
    private final ImageIcon tasksIcon = new ImageIcon(ImageUtil.loadImageResource(LootTrackerPlugin.class, "single_loot_icon.png"));
    private final ImageIcon backlogIcon = new ImageIcon(ImageUtil.loadImageResource(TimeTrackingPlugin.class, "lap_icon.png"));
    private final ImageIcon infoIcon = new ImageIcon(ImageUtil.loadImageResource(InfoPlugin.class, "info_icon.png"));

    private MaterialTab startTab;
    private MaterialTab infoTab;
    private boolean shouldForceReload;

    @Inject private ChunkMapWindow mapWindow;
    @Inject private ChunkTaskCalculator calculator;

    @Inject
    ChunkmanCompletionistPanel(ChunkmanCompletionist chunkmanCompletionist, SkillIconManager iconManager) {
        super();
        getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.iconManager = iconManager;
        this.completionist = chunkmanCompletionist;

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        tabGroup = new MaterialTabGroup();
        tabGroup.setLayout(new GridLayout(0, 6, 10, 10));

        addTabButtons();

        add(tabGroup, c);
        c.gridy++;

        completionist.setBorder(new EmptyBorder(15,0,15,0));
        completionist.setBackground(ColorScheme.DARK_GRAY_COLOR);

        add(completionist, c);
        c.gridy++;

        revalidate();
        repaint();
    }

    private void addTabButtons() {
        tabGroup.addTab(createTab(ChunkmanTabType.TASKS, tasksIcon));
        tabGroup.addTab(createTab(ChunkmanTabType.BACKLOG, backlogIcon));
        tabGroup.addTab(createTab(ChunkmanTabType.RULES, rulesIcon));
        tabGroup.addTab(createTab(ChunkmanTabType.CHUNK, chunkIcon));
        tabGroup.addTab(createTab(ChunkmanTabType.SETTINGS, configIcon));
        tabGroup.addTab(createTab(ChunkmanTabType.INFO, infoIcon));
    }

    private MaterialTab createTab(ChunkmanTabType type, ImageIcon icon) {
        MaterialTab tab = new MaterialTab(icon, tabGroup, null);
        tab.setName(type.name());
        tab.setOnSelectEvent(() -> {
            completionist.openTab(type, shouldForceReload);
            shouldForceReload = false;

            if (type == ChunkmanTabType.CHUNK) {
                mapWindow.setVisible(true);
            }

            return true;
        });

        if(type == ChunkmanTabType.TASKS) {
            startTab = tab;
        } else if(type == ChunkmanTabType.INFO) {
            infoTab = tab;
        }

        return tab;
    }

    public void reload(boolean isLoggedIn) {
        shouldForceReload = true;
        if(isLoggedIn)
            SwingUtilities.invokeLater(() -> tabGroup.select(startTab));
        else
            SwingUtilities.invokeLater(() -> tabGroup.select(infoTab));
    }

    public ChunkmanCompletionist getCompletionist() {
        return completionist;
    }
}
