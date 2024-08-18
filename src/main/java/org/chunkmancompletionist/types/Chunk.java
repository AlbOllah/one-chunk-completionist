package org.chunkmancompletionist.types;

import java.util.HashMap;
import java.util.Map;

public class Chunk {
    public Map<String, Chunk> Sections = new HashMap<>();
    public Map<String, String> Quest = new HashMap<>();
    public Map<String, String> Diary = new HashMap<>();
    public Map<String, Integer> Spawn = new HashMap<>();
    public Map<String, Integer> Clue = new HashMap<>();
    public Map<String, Integer> NPC = new HashMap<>();
    public Map<String, Integer> Monster = new HashMap<>();
    public Map<String, Integer> Objects = new HashMap<>();
    public Map<String, Boolean> Connect = new HashMap<>();
    public Map<String, Boolean> Shop = new HashMap<>();
    public String Nickname = "";
    public String Name = "";
    public String Id = "";
}
