package org.chunkmancompletionist.tasks;

import lombok.Getter;

@Getter
public class ValueRange {
    private int min, max;

    public boolean contains(int value) {
        return value >= this.min && value <= this.max;
    }
}
