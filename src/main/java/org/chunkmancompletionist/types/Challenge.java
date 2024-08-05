package org.chunkmancompletionist.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Challenge {
    public List<String> Category = new ArrayList<>();
    public List<String> Chunks = new ArrayList<>();
    public List<String> Items = new ArrayList<>();
    public Map<String, String> Tasks = new HashMap<>();
    public List<String> NPCs = new ArrayList<>();
    public List<String> Objects = new ArrayList<>();
    public List<String> Monsters = new ArrayList<>();
    public Map<String, Integer> Skills = new HashMap<>();

    public int Priority = 0;
    public int Level = 0;

    public boolean Primary = false;
    public boolean NotF2P = false;
    public boolean NoBoost = false;
    public boolean ManualShow = false;

    public String Label = "";
    public String Description = "";
    public String Output = "";
    public String OutputObject = "";
    public String Source = "";
}
