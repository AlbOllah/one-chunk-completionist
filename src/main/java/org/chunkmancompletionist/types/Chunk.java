package org.chunkmancompletionist.types;

import java.util.HashMap;
import java.util.Map;

public class Chunk {
    public Map<String, Map<String, Map<String, String>>> Sections = new HashMap<String, Map<String, Map<String, String>>>();
    public Map<String, String> Quest = new HashMap<String, String>();
    public Map<String, String> Diary = new HashMap<String, String>();
    public Map<String, Integer> Spawn = new HashMap<String, Integer>();
    public Map<String, Integer> Clue = new HashMap<String, Integer>();
    public Map<String, Integer> NPC = new HashMap<String, Integer>();
    public Map<String, Integer> Monster = new HashMap<String, Integer>();
    public Map<String, Integer> Objects = new HashMap<String, Integer>();
    public Map<String, Boolean> Connect = new HashMap<String, Boolean>();
    public Map<String, Boolean> Shop = new HashMap<String, Boolean>();
    public String Nickname = "";
    public String Name = "";

}
