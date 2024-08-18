package org.chunkmancompletionist.types;

import java.util.HashMap;
import java.util.Map;

public class BaseChunkData {
    public Map<String, Map<String, String>> items = new HashMap<>();
    public Map<String, String> objects = new HashMap<>();
    public Map<String, String> monsters = new HashMap<>();
    public Map<String, String> npcs = new HashMap<>();
    public Map<String, String> shops = new HashMap<>();
    public Map<String, Map<String, Boolean>> unlockedSections = new HashMap<>();
    public Map<String, Map<String, String>> dropRates = new HashMap<>();
    public Map<String, Map<String, Map<String, String>>> dropTables = new HashMap<>();
}
