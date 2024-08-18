package org.chunkmancompletionist.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeItems {
    public ArrayList<String> boneItems;
    public Map<String, ArrayList<String>> chunksPlus;
    public Map<String, String> chunksPlusNames;
    public Map<String, Map<String, String>> dropTables;
    public ArrayList<String> elementalRunes;
    public Map<String, ArrayList<String>> elementalStaves;
    public Map<String, ArrayList<String>> itemsPlus;
    public Map<String, String> itemsPlusNames;
    public Map<String, ArrayList<String>> mixPlus;
    public Map<String, String> mixPlusNames;
    public Map<String, ArrayList<String>> monstersPlus;
    public Map<String, String> monstersPlusNames;
    public Map<String, ArrayList<String>> npcsPlus;
    public Map<String, String> objectsPlusNames;
    public Map<String, ArrayList<String>> objectsPlus;
    public Map<String, Integer> rangedItems;
    public Map<String, ArrayList<String>> tasksPlus;
    public Map<String, Map<String, Map<String, Boolean>>> forceTalisman = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> telegrabSpawns = new HashMap<>();
    public Map<String, Boolean> tools;
    public Map<String, Boolean> magicTools;
    public Map<String, Boolean> bossLogs;
    public Map<String, Boolean> bossMonsters;
    public Map<String, Boolean> minigameShops;
    public Map<String, Map<String, Boolean>> ammoTools;
    public Map<String, Map<String, Boolean>> slayerTasks;
    public Map<String, Map<String, String>> boostItems;
}
