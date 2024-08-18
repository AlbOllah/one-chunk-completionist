package org.chunkmancompletionist;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
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
import java.awt.event.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
public class ChunkmanCompletionist extends JPanel {
    @Inject private ChunkTasksManager manager;
    @Inject private Provider<SettingsPanel> settingsPanelProvider;

    @Inject private Provider<ChunkmanCompletionistPanel> completionistPanelProvider;

    private ChunkInfo chunkInfo;

//    private Map<String, Chunk> chunks;
//    private Map<String, Map<String, SkillChallenge>> skillChallenges;
//    private Map<String, NonSkillChallenge> nonSkillChallenges;
//    private Map<String, QuestChallenge> questChallenges;
//    private Map<String, DiaryChallenge> diaryChallenges;
//    private Map<String, CombatChallenge> combatChallenges;
//    private Map<String, ExtraChallenge> extraChallenges;
//
//    private Map<String, Map<String, String>> codeItems;
//    private TaskUnlocks taskUnlocks;

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
        loadExtraData();

        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);

        setLayout(new DynamicGridLayout(0, 1, 0, 5));
    }

    public void loadTasks() {
        chunkInfo = loadFromFile("/chunkinfo.json", new TypeToken<>() {});
//        chunks = loadFromFile("/chunks.json", new TypeToken<>() {});
//        skillChallenges = loadFromFile("/challenges-skill.json", new TypeToken<>() {});
//        nonSkillChallenges = loadFromFile("/challenges-non-skill.json", new TypeToken<>() {});
//        questChallenges = loadFromFile("/challenges-quest.json", new TypeToken<>() {});
//        diaryChallenges = loadFromFile("/challenges-diary.json", new TypeToken<>() {});
//        combatChallenges = loadFromFile("/challenges-combat.json", new TypeToken<>() {});
//        extraChallenges = loadFromFile("/challenges-extra.json", new TypeToken<>() {});
    }

    public void loadExtraData() {
        //taskUnlocks = loadFromFile("/task-unlocks.json", new TypeToken<>() {});
        //codeItems = loadFromFile("/code-items.json", new TypeToken<>() {});
    }

    public void openTab(ChunkmanTabType type, boolean forceReload) {
        log.info(String.format("force: %s", forceReload));
        if(forceReload || currentType != type) {
            currentType = type;

            removeAll();
            searchBar.setText(null);

            add(searchBar);

            if(type == ChunkmanTabType.TASKS || type == ChunkmanTabType.BACKLOG) {
//                if(manager.getProfile().startingChunk == 0) {
//                    currentType = ChunkmanTabType.INFO;
//                } else
                    if(manager.getProfile().chunks.size() == 0) {
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

            if(type == ChunkmanTabType.INFO) {
                removeAll();
                searchBar.setText(null);

                add(searchBar);
                add(createGeneralInfoLabel());
            }

            revalidate();
            repaint();
        }
    }

    private void renderTaskList() {
        taskSlotList.clear();

        for(Map.Entry<String, CombatChallenge> entry : chunkInfo.challenges.Combat.entrySet()) {
            //JLabel icon = new JLabel();

            UITaskSlot slot = new UITaskSlot(entry.getValue(), clientThread, itemManager, null);
            slot.setLabel(entry.getKey());
            slot.setText(entry.getKey());

            taskSlotList.add(slot);

            JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);

            slot.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    //map.setVisible(true);
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
        button.setText("Start your journey");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateChunkTasks();
                openTab(ChunkmanTabType.TASKS, true);
            }
        });

        return button;
    }

    private void generateChunkTasks() {
        log.info(GSON.toJson(manager.getProfile()));

        manager.getProfile().startingChunk = 12082;
        if(manager.getProfile().chunks.size() == 0) {
            manager.getProfile().chunks.add("12082-1");
        }

        ArrayList<String> availableChunks = manager.getProfile().chunks;
        AvailableChunkData chunkData = new AvailableChunkData();

        log.info(String.format("generating tasks for chunk: %s", manager.getProfile().startingChunk));
        ArrayList<Chunk> validChunks = new ArrayList<>();
        Map<String, String> possibleTasks = new HashMap<>();

        for(Map.Entry<String, Chunk> entry : chunkInfo.chunks.entrySet()) {
            for(String chunkStr : availableChunks) {
                String[] split = chunkStr.split("-");
                if(Objects.equals(entry.getKey(), split[0])) {
                    Chunk chunk;
                    if(split.length > 1 && entry.getValue().Sections.size() > 0) {
                        chunk = entry.getValue().Sections.get(split[1]);
                    } else {
                        chunk = entry.getValue();
                    }
                    chunk.Id = chunkStr;
                    validChunks.add(chunk);
                }
            }
        }

        SkillChallenge telegrab = chunkInfo.challenges.Magic.get("Cast ~|telekinetic grab|~");

        for(Map.Entry<String, Map<String, List<Map<String, String>>>> item : chunkInfo.taskUnlocks.Spawns.entrySet()) {
            String itemName = item.getKey();
            for(Map.Entry<String, List<Map<String, String>>> chunk : item.getValue().entrySet()) {
                String chunkName = chunk.getKey();
                if(availableChunks.contains(chunkName)) {
                    AvailableItem availableItem;
                    if(chunkData.Unlocks.Items.containsKey(itemName)) {
                        availableItem = chunkData.Unlocks.Items.get(itemName);
                    } else {
                        availableItem = new AvailableItem();
                    }
                    availableItem.Chunk.add(chunkName);
                    chunkData.Unlocks.Items.put(itemName, availableItem);
                }
            }
        }

        for(Map.Entry<String, Map<String, List<Map<String, String>>>> item : chunkInfo.codeItems.telegrabSpawns.entrySet()) {
            String itemName = item.getKey();
            for(Map.Entry<String, List<Map<String, String>>> chunk : item.getValue().entrySet()) {
                String chunkName = chunk.getKey();
                if(availableChunks.contains(chunkName)) {
                    AvailableItem availableItem;
                    if(chunkData.Unlocks.Items.containsKey(itemName)) {
                        availableItem = chunkData.Unlocks.Items.get(itemName);
                    } else {
                        availableItem = new AvailableItem();
                    }
                    availableItem.Chunk.add(chunkName);
                    chunkData.Unlocks.Items.put(itemName, availableItem);
                }
            }
        }

        for(Map.Entry<String, Map<String, List<Map<String, String>>>> monster : chunkInfo.taskUnlocks.Monsters.entrySet()) {
            String monsterName = monster.getKey();
            for(Map.Entry<String, List<Map<String, String>>> chunk : monster.getValue().entrySet()) {
                String chunkName = chunk.getKey();
                if(availableChunks.contains(chunkName)) {
                    ArrayList<String> availableMonster;
                    if(chunkData.Unlocks.Monsters.containsKey(monsterName)) {
                        availableMonster = chunkData.Unlocks.Monsters.get(monsterName);
                    } else {
                        availableMonster = new ArrayList<>();
                    }
                    availableMonster.add(chunkName);
                    chunkData.Unlocks.Monsters.put(monsterName, availableMonster);
                }
            }
        }

        for(Map.Entry<String, Map<String, List<Map<String, String>>>> npc : chunkInfo.taskUnlocks.NPCs.entrySet()) {
            String npcName = npc.getKey();
            for(Map.Entry<String, List<Map<String, String>>> chunk : npc.getValue().entrySet()) {
                String chunkName = chunk.getKey();
                if(availableChunks.contains(chunkName)) {
                    ArrayList<String> availableNPC;
                    if(chunkData.Unlocks.NPCs.containsKey(npcName)) {
                        availableNPC = chunkData.Unlocks.NPCs.get(npcName);
                    } else {
                        availableNPC = new ArrayList<>();
                    }
                    availableNPC.add(chunkName);
                    chunkData.Unlocks.NPCs.put(npcName, availableNPC);
                }
            }
        }

        for(Map.Entry<String, Map<String, List<Map<String, String>>>> object : chunkInfo.taskUnlocks.Objects.entrySet()) {
            String objectName = object.getKey();
            for(Map.Entry<String, List<Map<String, String>>> chunk : object.getValue().entrySet()) {
                String chunkName = chunk.getKey();
                if(availableChunks.contains(chunkName)) {
                    ArrayList<String> availableObject;
                    if(chunkData.Unlocks.Objects.containsKey(objectName)) {
                        availableObject = chunkData.Unlocks.Objects.get(objectName);
                    } else {
                        availableObject = new ArrayList<>();
                    }
                    availableObject.add(chunkName);
                    chunkData.Unlocks.Objects.put(objectName, availableObject);
                }
            }
        }

        for(Map.Entry<String, Map<String, List<Map<String, String>>>> shop : chunkInfo.taskUnlocks.Shops.entrySet()) {
            String shopName = shop.getKey();
            for(Map.Entry<String, List<Map<String, String>>> chunk : shop.getValue().entrySet()) {
                String chunkName = chunk.getKey();
                if(availableChunks.contains(chunkName)) {
                    ArrayList<String> availableShop;
                    if(chunkData.Unlocks.Shops.containsKey(shopName)) {
                        availableShop = chunkData.Unlocks.Shops.get(shopName);
                    } else {
                        availableShop = new ArrayList<>();
                    }
                    availableShop.add(chunkName);
                    chunkData.Unlocks.Shops.put(shopName, availableShop);
                }
            }
        }

//        for(Map.Entry<String, List<Map<String, String>>> item : chunkInfo.taskUnlocks.Items.entrySet()) {
//            String itemName = item.getKey();
//            String monster = "";
//            String asterisk = "*";
//            if(itemName.contains("^")) {
//                asterisk += "^";
//                String[] split = itemName.split("\\^");
//                itemName = split[0];
//                if(split.length > 1) {
//                    monster = split[1];
//                }
//            }
//
//
//            if(!asterisk.contains("^")) {
//
//            }
////            for(Map<String, String> chunk : item.getValue()) {
////
////            }
//        }

        for(Chunk chunk : validChunks) {
            for(Map.Entry<String, Integer> monster : chunk.Monster.entrySet()) {
                String monsterName = monster.getKey();
                ArrayList<String> availableMonster;
                if(chunkData.Unlocks.Monsters.containsKey(monsterName)) {
                    availableMonster = chunkData.Unlocks.Monsters.get(monsterName);
                } else {
                    availableMonster = new ArrayList<>();
                }
                availableMonster.add(chunk.Id);
                chunkData.Unlocks.Monsters.put(monsterName, availableMonster);
            }
        }

        calculateChallenges(chunkData);
    }

    private void calculateChallenges(AvailableChunkData chunkData) {
        log.info(GSON.toJson(chunkData));
        Challenges valids = new Challenges();

        ArrayList<String> skillNames = new ArrayList<>();
        for(Skill skill : Skill.values()) {
            skillNames.add(skill.getName());
        }
        skillNames.add("Nonskill");
        skillNames.add("Quest");
        skillNames.add("Diary");
        skillNames.add("Extra");

        for(String skillName : skillNames) {
            if(!skillName.equals("Nonskill") && !skillName.equals("Quest") && !skillName.equals("Diary") && !skillName.equals("Extra")) {
                Map<String, SkillChallenge> skill = chunkInfo.challenges.get(skillName);
                SkillChallenge lowest;
                String lowestName;
                for(Map.Entry<String, SkillChallenge> challenge : skill.entrySet()) {
                    boolean validChallenge = true;

                }
            }
        }
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
