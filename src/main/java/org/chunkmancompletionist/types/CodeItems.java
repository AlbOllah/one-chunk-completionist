package org.chunkmancompletionist.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeItems {
    public List<String> boneItems = new ArrayList<>();
    public Map<String, List<String>> chunksPlus = new HashMap<>();
    public Map<String, String> chunkPlusNames = new HashMap<>();
    public Map<String, Map<String, String>> dropTables = new HashMap<>();
    public List<String> elementalRunes = new ArrayList<>();
    public Map<String, List<String>> elementalStaves = new HashMap();
    public Map<String, List<String>> itemsPlus = new HashMap<>();
    public Map<String, String> itemsPlusNames = new HashMap<>();
    public Map<String, List<String>> mixPlus = new HashMap<>();
    public Map<String, String> mixPlusNames = new HashMap<>();
    public Map<String, List<String>> monstersPlus = new HashMap<>();
    public Map<String, String> monstersPlusNames = new HashMap<>();
    public Map<String, List<String>> npcsPlus = new HashMap<>();
    public Map<String, String> npcsPlusNames = new HashMap<>();
    public Map<String, List<String>> objectsPlus = new HashMap<>();
    public Map<String, String> objectsPlusNames = new HashMap<>();
    public Map<String, Integer> rangedItems = new HashMap<>();
    public Map<String, List<String>> tasksPlus = new HashMap<>();
    public Map<String, Map<String, Map<String, Boolean>>> forceTalisman = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> telegrabSpawns = new HashMap<>();
    public Map<String, Boolean> tools = new HashMap<>();
    public Map<String, Boolean> magicTools = new HashMap<>();
    public Map<String, Boolean> bossLogs = new HashMap<>();
    public Map<String, Boolean> bossMonsters = new HashMap<>();
    public Map<String, Boolean> minigameShops = new HashMap<>();
    public Map<String, Map<String, Boolean>> ammoTools = new HashMap<>();
    public Map<String, Map<String, Boolean>> slayerTasks = new HashMap<>();
    public Map<String, Map<String, String>> boostItems = new HashMap<>();

}
