package org.chunkmancompletionist.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AvailableChunkDataUnlocks {
    public Map<String, AvailableItem> Items = new HashMap<>();
    public Map<String, ArrayList<String>> Monsters = new HashMap<>();
    public Map<String, ArrayList<String>> NPCs = new HashMap<>();
    public Map<String, ArrayList<String>> Objects = new HashMap<>();
    public Map<String, ArrayList<String>> Shops = new HashMap<>();
}
