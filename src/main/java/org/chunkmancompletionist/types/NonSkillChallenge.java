package org.chunkmancompletionist.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NonSkillChallenge extends Challenge {
    public List<String> Sections = new ArrayList<>();
    public List<String> Reward = new ArrayList<>();
    public Map<String, Float> XpReward = new HashMap<>();
    public Map<String, Boolean> SkillsBoost = new HashMap<>();

    public String ClueTier = "";
    public String ClueType = "";
    public String InfoLink = "";
    public String StarRegion = "";

    public boolean UnlocksArea = false;
    public boolean ConnectsSections = false;
    public boolean NotSkiller = false;

    public int QuestPointsNeeded = 0;
    public int TotalLevelNeeded = 0;
    public int CombatLevelNeeded = 0;
    public int KudosNeeded = 0;
    public int CombatPointsNeeded = 0;
    public int Kudos = 0;
}
