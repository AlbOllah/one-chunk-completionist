package org.chunkmancompletionist.tasks;

import org.chunkmancompletionist.types.TaskGroup;
import org.chunkmancompletionist.types.TaskType;
import net.runelite.api.Skill;
import net.runelite.api.Prayer;

import java.util.HashMap;
import java.util.List;

public class ChunkTask {
    public String name;
    public boolean isComplete;
    public TaskGroup taskGroup;
    public HashMap<Skill, Integer> skills;
    public List<String> items;
    public String output;

    public TaskType taskType = TaskType.UNKNOWN;
    public boolean isCustom;

    public List<MapMovement> movementRequirement;
    public MapBoundary locationRequirement;
    public String targetRequirement;
    public List<Integer> itemIds;
    public ChatMessageConfig chatMessageConfig;
    public XpTaskConfig xpTaskConfig;
    public FarmingPatchConfig farmingPatchConfig;
    public Prayer prayer;
}
