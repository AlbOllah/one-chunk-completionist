package org.chunkmancompletionist.tasks;

import org.chunkmancompletionist.types.TaskGroup;
import org.chunkmancompletionist.types.TaskType;

public class ChunkTask {
    public String name;
    public boolean isComplete;
    public TaskGroup taskGroup;
    public TaskType taskType = TaskType.UNKNOWN;
}
