package org.chunkmancompletionist.types;

public class Rules {
    public boolean Skillcape = false;
    public boolean RareDrop = false;
    public boolean Pouch = false;
    public boolean InsidePOH = false;
    public boolean InsidePOHPrimary = false;
    public boolean ConstructionMilestone = false;
    public boolean ConstructionMinigame = false;
    public boolean Boss = false;
    public boolean BossLevel = false;
    public boolean SlayerEquipment = false;
    public boolean NormalFarming = false;
    public boolean Raking = false;
    public boolean SulphurousFertiliser = false;
    public boolean CoX = false;
    public boolean TitheFarm = false;
    public boolean KillX = false;
    public boolean SorceressGarden = false;
    public boolean Spells = false;
    public boolean ShowSkillTasks = false;
    public boolean ShowQuestTasks = false;
    public boolean ShowDiaryTasks = false;
    public boolean ShowBestinSlotTasks = false;
    public boolean ShowBestinSlotPrayerTasks = false;
    public boolean ShowBestinSlotDefensiveTasks = false;
    public boolean ShowBestinSlotFlinchingTasks = false;
    public boolean ShowBestinSlotWeightTasks = false;
    public boolean ShowBestinSlotMeleeStyleTasks = false;
    public boolean ShowBestinSlot1Hand2H = false;
    public boolean ShowQuestTasksComplete = false;
    public boolean ShowDiaryTasksComplete = false;
    public boolean ShowDiaryTasksAny = false;
    public boolean HighestLevel = false;
    public boolean BISSkilling = false;
    public boolean CollectionLog = false;
    public boolean Minigame = false;
    public boolean PvPMinigame = false;
    public boolean ShortcutTask = false;
    public boolean Shortcut = false;
    public boolean WieldCraftedItems = false;
    public boolean MultiStepProcessing = false;
    public boolean ShootingStar = false;
    public boolean Forestry = false;
    public boolean ForestryXp = false;
    public boolean PuroPuro = false;
    public boolean Extraimplings = false;
    public boolean CollectionLogBosses = false;
    public boolean CollectionLogRaids = false;
    public boolean CollectionLogClues = false;
    public boolean CollectionLogMinigames = false;
    public boolean CollectionLogOther = false;
    public boolean HerbloreUnlocked = false;
    public boolean FarmingPrimary = false;
    public boolean TertiaryKeys = false;
    public boolean Wanderingimplings = false;
    public boolean SecondaryPrimary = false;
    public int SecondaryPrimaryAmount = 1;
    public boolean RDT = false;
    public boolean UntrackedUniques = false;
    public boolean CombatandTeleportSpells = false;
    public boolean PrimarySpawns = false;
    public boolean SmithingbySmelting = false;
    public boolean Pets = false;
    public boolean Jars = false;
    public boolean Stuffables = false;
    public int KillXAmount = 1;
    public int RareDropAmount = 1000;
    public boolean ManuallyCompleteTasks = false;
    public boolean EveryDrop = false;
    public boolean HerbloreUnlockedSnakeWeed = false;
    public boolean HigherLander = false;
    public boolean StartingItems = false;
    public boolean TutorAmmo = false;
    public boolean SecondaryMTA = false;
    public boolean FossilIslandTasks = false;
    public boolean CombatDiaryTasks = false;
    public boolean PVPOnlySpells = false;
    public boolean SkillingPets = false;
    public boolean MoneyUnlockables = false;
    public boolean Prayers = false;
    public boolean AllDroptables = false;
    public boolean AllDroptablesNest = false;
    public boolean F2P = false;
    public boolean Skiller = false;
    public boolean FillStash = false;
    public boolean FillPOH = false;
    public boolean HerbloreUnlockedException = false;
    public boolean AllShops = false;
    public boolean QuestSkillReqs = false;
    public boolean CleaningHerbs = false;
    public boolean Boosting = false;
    public boolean SuperheatFurnace = false;
    public boolean PartialProducts = false;
    public boolean POHRooms = false;
    
    public boolean get(String key) {
        switch (key) {
            case "Skillcape":
                return Skillcape;
            case "Rare Drop":
                return RareDrop;
            case "Pouch":
                return Pouch;
            case "InsidePOH":
                return InsidePOH;
            case "InsidePOH Primary":
                return InsidePOHPrimary;
            case "Construction Milestone":
                return ConstructionMilestone;
            case "Construction Minigame":
                return ConstructionMinigame;
            case "Boss":
                return Boss;
            case "Boss Level":
                return BossLevel;
            case "Slayer Equipment":
                return SlayerEquipment;
            case "Normal Farming":
                return NormalFarming;
            case "Raking":
                return Raking;
            case "Sulphurous Fertiliser":
                return SulphurousFertiliser;
            case "CoX":
                return CoX;
            case "Tithe Farm":
                return TitheFarm;
            case "Kill X":
                return KillX;
            case "Sorceress's Garden":
                return SorceressGarden;
            case "Spells":
                return Spells;
            case "Show Skill Tasks":
                return ShowSkillTasks;
            case "Show Quest Tasks":
                return ShowQuestTasks;
            case "Show Diary Tasks":
                return ShowDiaryTasks;
            case "Show Best in Slot Tasks":
                return ShowBestinSlotTasks;
            case "Show Best in Slot Prayer Tasks":
                return ShowBestinSlotPrayerTasks;
            case "Show Best in Slot Defensive Tasks":
                return ShowBestinSlotDefensiveTasks;
            case "Show Best in Slot Flinching Tasks":
                return ShowBestinSlotFlinchingTasks;
            case "Show Best in Slot Weight Tasks":
                return ShowBestinSlotWeightTasks;
            case "Show Best in Slot Melee Style Tasks":
                return ShowBestinSlotMeleeStyleTasks;
            case "Show Best in Slot 1H and 2H":
                return ShowBestinSlot1Hand2H;
            case "Show Quest Tasks Complete":
                return ShowQuestTasksComplete;
            case "Show Diary Tasks Complete":
                return ShowDiaryTasksComplete;
            case "Show Diary Tasks Any":
                return ShowDiaryTasksAny;
            case "Highest Level":
                return HighestLevel;
            case "BIS Skilling":
                return BISSkilling;
            case "Collection Log":
                return CollectionLog;
            case "Minigame":
                return Minigame;
            case "PvP Minigame":
                return PvPMinigame;
            case "Shortcut Task":
                return ShortcutTask;
            case "Shortcut":
                return Shortcut;
            case "Wield Crafted Items":
                return WieldCraftedItems;
            case "Multi Step Processing":
                return MultiStepProcessing;
            case "Shooting Star":
                return ShootingStar;
            case "Forestry":
                return Forestry;
            case "ForestryXp":
                return ForestryXp;
            case "Puro-Puro":
                return PuroPuro;
            case "Extra implings":
                return Extraimplings;
            case "Collection Log Bosses":
                return CollectionLogBosses;
            case "Collection Log Raids":
                return CollectionLogRaids;
            case "Collection Log Clues":
                return CollectionLogClues;
            case "Collection Log Minigames":
                return CollectionLogMinigames;
            case "Collection Log Other":
                return CollectionLogOther;
            case "Herblore Unlocked":
                return HerbloreUnlocked;
            case "Farming Primary":
                return FarmingPrimary;
            case "Tertiary Keys":
                return TertiaryKeys;
            case "Wandering implings":
                return Wanderingimplings;
            case "Secondary Primary":
                return SecondaryPrimary;
            case "RDT":
                return RDT;
            case "Untracked Uniques":
                return UntrackedUniques;
            case "Combat and Teleport Spells":
                return CombatandTeleportSpells;
            case "Primary Spawns":
                return PrimarySpawns;
            case "Smithing by Smelting":
                return SmithingbySmelting;
            case "Pets":
                return Pets;
            case "Jars":
                return Jars;
            case "Stuffables":
                return Stuffables;
            case "Manually Complete Tasks":
                return ManuallyCompleteTasks;
            case "Every Drop":
                return EveryDrop;
            case "Herblore Unlocked Snake Weed":
                return HerbloreUnlockedSnakeWeed;
            case "HigherLander":
                return HigherLander;
            case "Starting Items":
                return StartingItems;
            case "Tutor Ammo":
                return TutorAmmo;
            case "Secondary MTA":
                return SecondaryMTA;
            case "Fossil Island Tasks":
                return FossilIslandTasks;
            case "Combat Diary Tasks":
                return CombatDiaryTasks;
            case "PVP-Only Spells":
                return PVPOnlySpells;
            case "Skilling Pets":
                return SkillingPets;
            case "Money Unlockables":
                return MoneyUnlockables;
            case "Prayers":
                return Prayers;
            case "All Droptables":
                return AllDroptables;
            case "All Droptables Nest":
                return AllDroptablesNest;
            case "F2P":
                return F2P;
            case "Skiller":
                return Skiller;
            case "Fill Stash":
                return FillStash;
            case "Fill POH":
                return FillPOH;
            case "Herblore Unlocked Exception":
                return HerbloreUnlockedException;
            case "All Shops":
                return AllShops;
            case "Quest Skill Reqs":
                return QuestSkillReqs;
            case "Cleaning Herbs":
                return CleaningHerbs;
            case "Boosting":
                return Boosting;
            case "Superheat Furnace":
                return SuperheatFurnace;
            case "Partial Products":
                return PartialProducts;
            case "POH Rooms":
                return POHRooms;

            default:
                return false;
        }
    }
}
