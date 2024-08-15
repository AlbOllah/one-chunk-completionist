package org.chunkmancompletionist.types;

import java.util.HashMap;
import java.util.Map;

public class Equipment {
    public int attack_speed = 0;
    public int attack_crush = 0;
    public int attack_magic = 0;
    public int attack_ranged = 0;
    public int attack_slash = 0;
    public int attack_stab = 0;
    public int defence_crush = 0;
    public int defence_magic = 0;
    public int defence_ranged = 0;
    public int defence_slash = 0;
    public int defence_stab = 0;
    public float magic_damage = 0;
    public int melee_strength = 0;
    public int prayer = 0;
    public int ranged_strength = 0;
    public String slot = "";
    public Map<String, Integer> requirements = new HashMap<>();
}
