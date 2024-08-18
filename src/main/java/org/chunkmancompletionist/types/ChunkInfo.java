package org.chunkmancompletionist.types;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ChunkInfo {
    public Challenges challenges;
    public Map<String, Chunk> chunks;
    public CodeItems codeItems;
    public Map<String, Map<String, Map<String, String>>> drops;
    public Map<String, Map<String, Boolean>> objectItems;
    public Map<String, String> quests;
    public Map<String, String> diaries;
    public Map<String, Map<String, Boolean>> clues;
    public Map<String, ArrayList<String>> starRegions;
    public Map<String, ArrayList<Map<String, String>>> mapOverlays;
    public Map<String, Map<String, Boolean>> shopItems;
    public Map<String, Map<String, Map<String, Map<String, String>>>> skillItems;
    public TaskUnlocks taskUnlocks;
    public Map<String, Map<String, Integer>> toolLevels;
    public Map<String, Integer> slayerEquipment;
    public Map<String, Integer> slayerMonsters;
    public Map<String, SlayerTask> slayerMasterTasks;
    public Map<String, Map<String, Boolean>> searchTerms;
    public Map<String, Map<String, ArrayList<String>>> sections;
    public ArrayList<String> walkableChunks;
    public ArrayList<String> walkableChunksF2P;
    public ArrayList<String> unnotingChunks;
    public Map<String, Equipment> equipment;

    public Float getMonsterDropChance(String monster, String drop, String number) {
        String chance = drops.getOrDefault(monster, new HashMap<>()).getOrDefault(drop, new HashMap<>()).getOrDefault(number, "");
        if(chance.equals(""))
            return 0f;

        Float first = null;
        Float second = null;
        try {
            if(chance.split("/")[0].replaceAll("~", "").equals("Always")) {
                first = 1.0f;
                second = 1.0f;
            } else {
                first = Float.parseFloat(chance.split("/")[0].replaceAll("~", ""));
                second = Float.parseFloat(chance.split("/")[1]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0f;
        }

        return first / second;
    }

    public Float getDropTableChance(String drop, String item) {
        String chance = codeItems.dropTables.getOrDefault(drop, new HashMap<>()).getOrDefault(item, "");
        if(chance.equals(""))
            return 0f;

        Float first = null;
        Float second = null;
        try {
            first = Float.parseFloat(chance.split("@")[0].split("/")[0].replaceAll("~", ""));
            second = Float.parseFloat(chance.split("@")[0].split("/")[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0f;
        }

        return first / second;
    }
}
