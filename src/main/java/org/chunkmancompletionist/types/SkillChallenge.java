package org.chunkmancompletionist.types;

import java.util.ArrayList;
import java.util.List;

public class SkillChallenge extends Challenge {
    public List<String> Mix = new ArrayList<>();

    public String BackupParent = "";

    public boolean ManualInvalid = false;
    public boolean ManualNonProcessing = false;
    public boolean NoPet = false;
    public boolean NoXp = false;
    public boolean NotEquip = false;
    public boolean AlwaysValid = false;
}
