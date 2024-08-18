package org.chunkmancompletionist.types;

import java.util.Map;

public class Challenges {
    public Map<String, SkillChallenge> Agility;
    public Map<String, SkillChallenge> Attack;
    public Map<String, SkillChallenge> Construction;
    public Map<String, SkillChallenge> Cooking;
    public Map<String, SkillChallenge> Crafting;
    public Map<String, SkillChallenge> Defence;
    public Map<String, SkillChallenge> Farming;
    public Map<String, SkillChallenge> Firemaking;
    public Map<String, SkillChallenge> Fishing;
    public Map<String, SkillChallenge> Fletching;
    public Map<String, SkillChallenge> Herblore;
    public Map<String, SkillChallenge> Hitpoints;
    public Map<String, SkillChallenge> Hunter;
    public Map<String, SkillChallenge> Magic;
    public Map<String, SkillChallenge> Mining;
    public Map<String, SkillChallenge> Prayer;
    public Map<String, SkillChallenge> Ranged;
    public Map<String, SkillChallenge> Runecraft;
    public Map<String, SkillChallenge> Slayer;
    public Map<String, SkillChallenge> Smithing;
    public Map<String, SkillChallenge> Strength;
    public Map<String, SkillChallenge> Thieving;
    public Map<String, SkillChallenge> Woodcutting;
    public Map<String, CombatChallenge> Combat;
    public Map<String, NonSkillChallenge> Nonskill;
    public Map<String, ExtraChallenge> Extra;
    public Map<String, DiaryChallenge> Diary;
    public Map<String, QuestChallenge> Quest;

    public <T> T get(String name) {
        switch (name) {
            case "Agility":
                return (T) Agility;

            case "Attack":
                return (T) Attack;

            case "Construction":
                return (T) Construction;

            case "Cooking":
                return (T) Cooking;

            case "Crafting":
                return (T) Crafting;

            case "Defence":
                return (T) Defence;

            case "Farming":
                return (T) Farming;

            case "Firemaking":
                return (T) Firemaking;

            case "Fishing":
                return (T) Fishing;

            case "Fletching":
                return (T) Fletching;

            case "Herblore":
                return (T) Herblore;

            case "Hitpoints":
                return (T) Hitpoints;

            case "Hunter":
                return (T) Hunter;

            case "Magic":
                return (T) Magic;

            case "Mining":
                return (T) Mining;

            case "Prayer":
                return (T) Prayer;

            case "Ranged":
                return (T) Ranged;

            case "Runecraft":
                return (T) Runecraft;

            case "Slayer":
                return (T) Slayer;

            case "Smithing":
                return (T) Smithing;

            case "Strength":
                return (T) Strength;

            case "Thieving":
                return (T) Thieving;

            case "Woodcutting":
                return (T) Woodcutting;

            case "Combat":
                return (T) Combat;

            case "Nonskill":
                return (T) Nonskill;

            case "Extra":
                return (T) Extra;

            case "Diary":
                return (T) Diary;

            case "Quest":
                return (T) Quest;

            default:
                return null;
        }
    }
}
