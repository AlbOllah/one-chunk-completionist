package org.chunkmancompletionist.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskUnlocks {
    public Map<String, List<Map<String, String>>> Items = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> Monsters = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> NPCs = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> Objects = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> Shops = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> Spawns = new HashMap<>();

}
