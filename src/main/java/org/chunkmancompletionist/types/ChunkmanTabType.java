package org.chunkmancompletionist.types;

import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Skill;
//import net.runelite.client.plugins.skillcalculator.skills.*;

@AllArgsConstructor
@Getter
public enum ChunkmanTabType {
    SETTINGS(true, null),
    CHUNK(true, null),
    RULES(true, null),
    TASKS(true, null),
    BACKLOG(true, null),
    INFO(true, null);

    private final boolean mainTab;
    @Nullable
    private final Skill skill;

//    MINING(Skill.MINING, MiningBonus.values(), MiningAction.values()),
//    AGILITY(Skill.AGILITY, AgilityBonus.values(), AgilityAction.values()),
//    SMITHING(Skill.SMITHING, SmithingBonus.values(), SmithingAction.values()),
//    HERBLORE(Skill.HERBLORE, null, HerbloreAction.values()),
//    FISHING(Skill.FISHING, FishingBonus.values(), FishingAction.values()),
//    THIEVING(Skill.THIEVING, null, ThievingAction.values()),
//    COOKING(Skill.COOKING, null, CookingAction.values()),
//    PRAYER(Skill.PRAYER, PrayerBonus.values(), PrayerAction.values()),
//    CRAFTING(Skill.CRAFTING, null, CraftingAction.values()),
//    FIREMAKING(Skill.FIREMAKING, FiremakingBonus.values(), FiremakingAction.values()),
//    MAGIC(Skill.MAGIC, null, MagicAction.values()),
//    FLETCHING(Skill.FLETCHING, null, FletchingAction.values()),
//    WOODCUTTING(Skill.WOODCUTTING, WoodcuttingBonus.values(), WoodcuttingAction.values()),
//    RUNECRAFT(Skill.RUNECRAFT, RunecraftBonus.values(), RunecraftAction.values()),
//    FARMING(Skill.FARMING, FarmingBonus.values(), FarmingAction.values()),
//    CONSTRUCTION(Skill.CONSTRUCTION, ConstructionBonus.values(), ConstructionAction.values()),
//    HUNTER(Skill.HUNTER, null, HunterAction.values());

//    private final Skill skill;
//    @Nullable
//    private final SkillBonus[] skillBonuses;
//    private final SkillAction[] skillActions;
}
