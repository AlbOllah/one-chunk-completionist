package org.chunkmancompletionist.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskUnlocks {
    public Map<String, List<Map<String, String>>> items = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> monsters = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> npcs = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> objects = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> shops = new HashMap<>();
    public Map<String, Map<String, List<Map<String, String>>>> spawns = new HashMap<>();
}
