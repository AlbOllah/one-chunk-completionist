package org.chunkmancompletionist;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import org.chunkmancompletionist.managers.ChunkTasksManager;
import org.chunkmancompletionist.panel.ChunkMapWindow;
import org.chunkmancompletionist.panel.ChunkmanCompletionistPanel;
import org.chunkmancompletionist.panel.SettingsPanel;
import org.chunkmancompletionist.panel.UITaskSlot;
import org.chunkmancompletionist.types.*;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
public class ChunkmanCompletionist extends JPanel {
    @Inject private ChunkTasksManager manager;
    @Inject private Provider<SettingsPanel> settingsPanelProvider;

    @Inject private Provider<ChunkmanCompletionistPanel> completionistPanelProvider;

    private Map<String, Chunk> chunks;
    private Map<String, Map<String, SkillChallenge>> skillChallenges;
    private Map<String, NonSkillChallenge> nonSkillChallenges;
    private Map<String, QuestChallenge> questChallenges;
    private Map<String, DiaryChallenge> diaryChallenges;
    private Map<String, CombatChallenge> combatChallenges;
    private Map<String, ExtraChallenge> extraChallenges;

    private final Client client;
    private final ClientThread clientThread;
    private final SpriteManager spriteManager;
    private final ItemManager itemManager;

    private final IconTextField searchBar = new IconTextField();
    private final List<UITaskSlot> taskSlotList = new ArrayList<>();

    private ChunkmanTabType currentType;

    @Inject
    ChunkmanCompletionist(Client client, ClientThread clientThread, SpriteManager spriteManager, ItemManager itemManager) {
        this.client = client;
        this.clientThread = clientThread;
        this.spriteManager = spriteManager;
        this.itemManager = itemManager;

        loadTasks();

        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);

        setLayout(new DynamicGridLayout(0, 1, 0, 5));
    }

    public void loadTasks() {
        chunks = loadFromFile("/chunks.json", new TypeToken<>() {});
        skillChallenges = loadFromFile("/challenges-skill.json", new TypeToken<>() {});
        nonSkillChallenges = loadFromFile("/challenges-non-skill.json", new TypeToken<>() {});
        questChallenges = loadFromFile("/challenges-quest.json", new TypeToken<>() {});
        diaryChallenges = loadFromFile("/challenges-diary.json", new TypeToken<>() {});
        combatChallenges = loadFromFile("/challenges-combat.json", new TypeToken<>() {});
        extraChallenges = loadFromFile("/challenges-extra.json", new TypeToken<>() {});
    }

    public void openTab(ChunkmanTabType type, boolean forceReload) {
        if(forceReload || currentType != type) {
            currentType = type;

            removeAll();
            searchBar.setText(null);

            add(searchBar);

            if(type == ChunkmanTabType.TASKS || type == ChunkmanTabType.BACKLOG) {
                if(manager.getProfile().startingChunk == 0) {
                    add(createGeneralInfoLabel());
                } else if(manager.getProfile().chunks.size() == 0) {
                    add(createStartButton());
                } else {
                    renderTaskList();
                }
            } else if(type == ChunkmanTabType.SETTINGS) {
                taskSlotList.clear();

                add(settingsPanelProvider.get());
            } else {
                taskSlotList.clear();
            }

            revalidate();
            repaint();
        }
    }

    private void renderTaskList() {
        taskSlotList.clear();

        for(Map.Entry<String, CombatChallenge> entry : combatChallenges.entrySet()) {
            //JLabel icon = new JLabel();

            UITaskSlot slot = new UITaskSlot(entry.getValue(), clientThread, itemManager, null);
            slot.setLabel(entry.getKey());
            slot.setText(entry.getKey());

            taskSlotList.add(slot);

            JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);

            slot.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ChunkMapWindow map = new ChunkMapWindow();
                    map.setVisible(true);
//                    JFrame frame = new JFrame();
//                    frame.setVisible(true);
//                    JWindow window = new JWindow(f);
//                    window.setSize(200, 100);
//                    window.setVisible(true);
//                    window.setLocation(100, 100);
//                    if (!e.isShiftDown()) {
//                        //clearCombinedSlots();
//                    }
//
//                    if (slot.isSelected()) {
//                        //combinedActionSlots.remove(slot);
//                    } else {
//                        //combinedActionSlots.add(slot);
//                    }
//
//                    slot.setSelected(!slot.isSelected());
                    //updateCombinedAction();
                }
            });
        }

        taskSlotList.forEach(this::add);
        revalidate();
        repaint();
    }

    public JLabel createGeneralInfoLabel() {
        JLabel infoLabel = new JLabel();
        infoLabel.setText("<html>Log in to load chunk tasks</html>");

        return infoLabel;
    }

    public JButton createStartButton() {
        JButton button = new JButton();
        button.setText("Haha");

        return button;
    }

    public void unloadProfile() {
        manager.unload();
        settingsPanelProvider.get().updateFields();
    }

    public void loadProfile() {
        manager.init();
        settingsPanelProvider.get().updateFields();
    }

    private <T> T loadFromFile(String resourceName, TypeToken<T> tokenType) {
        InputStream stream = ChunkmanCompletionistPanel.class.getResourceAsStream(resourceName);
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        return GSON.fromJson(reader, tokenType.getType());
    }
}
