package org.chunkmancompletionist.tasks;

import lombok.Getter;
import net.runelite.api.Skill;

@Getter
public class XpTaskConfig {
    private Skill skill;
    private int xpMin;
    private int xpMax;
    private MapBoundary location;
}
