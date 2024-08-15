package org.chunkmancompletionist.types;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
public class ChunkInfo {
    public Challenges challenges = new Challenges();
    public Map<String, Chunk> chunks = new HashMap<>();
    public CodeItems codeItems = new CodeItems();
    public Map<String, Map<String, Map<String, String>>> drops = new HashMap<>();
    public Map<String, Map<String, Boolean>> objectItems = new HashMap<>();

    public Map<String, String> quests = new HashMap<>();
    public Map<String, String> diaries = new HashMap<>();
    public Map<String, Map<String, Boolean>> clues = new HashMap<>();
    public Map<String, List<String>> starRegions = new HashMap<>();
    public Map<String, List<Map<String, String>>> mapOverlays = new HashMap<>();
    public Map<String, Map<String, Boolean>> shopItems = new HashMap<>();
    public Map<String, Map<String, Map<String, Map<String, String>>>> skillItems = new HashMap<>();
    public TaskUnlocks taskUnlocks = new TaskUnlocks();
    public Map<String, Map<String, Integer>> toolLevels = new HashMap<>();
    public Map<String, Integer> slayerEquipment = new HashMap<>();
    public Map<String, Integer> slayerMonsters = new HashMap<>();
    public Map<String, Map<String, SlayerTask>> slayerMasterTasks = new HashMap<>();
    public Map<String, Map<String, Boolean>> searchTerms = new HashMap<>();
    public Map<String, Map<String, List<String>>> sections = new HashMap<>();
    public List<String> walkableChunks = new ArrayList<>();
    public List<String> walkableChunksF2P = new ArrayList<>();
    public List<String> unnotingChunks = new ArrayList<>();
    public Map<String, Equipment> equipment = new HashMap<>();

    public void save() {
        for(Map.Entry<String, Chunk> chunk: chunks.entrySet()) {
            chunk.getValue().chunkId = chunk.getKey();
        }

        for(Map.Entry<String, Chunk> chunk: chunks.entrySet()) {
            log.info(chunk.getValue().chunkId);
        }

        saveToFileDev(new File("D:\\SWorkspace\\one-chunk-completionist\\src\\main\\resources\\chunkinfo-mod.json"), this);
    }

    private <T> void saveToFileDev(File file, T data) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(GSON.toJson(data));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
