package org.chunkmancompletionist.tasks;

import org.chunkmancompletionist.types.PatchType;
import lombok.Getter;

import java.util.List;

@Getter
public class FarmingPatchConfig {
    private PatchType patchType;
    private List<ValueRange> varbitRanges;
}