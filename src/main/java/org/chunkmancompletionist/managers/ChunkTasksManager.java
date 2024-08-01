package org.chunkmancompletionist.managers;

import org.chunkmancompletionist.tasks.ChunkTask;
import org.chunkmancompletionist.types.TaskType;

import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.chunkmancompletionist.ChunkmanCompletionistConfig.CONFIG_GROUP;
import static net.runelite.http.api.RuneLiteAPI.GSON;

@Singleton
@Slf4j
public class ChunkTasksManager {
    @Inject Client client;
    @Inject ConfigManager configManager;

    @Getter private List<ChunkTask> chunkTaskList;

    public List<ChunkTask> getActiveChunkTasksByType(TaskType taskType) {
        return chunkTaskList.stream().filter(t -> !t.isComplete && t.taskType == taskType).collect(Collectors.toList());
    }

    public void markTask(ChunkTask task, boolean completed) {
        task.isComplete = completed;
        this.saveChunkTasks();
    }

    public void loadChunkTasks() {
        chunkTaskList = loadChunkTasksFromProfile();
        if(chunkTaskList == null)
            chunkTaskList = new ArrayList<>();
    }

    public void saveChunkTasks() {
        configManager.setRSProfileConfiguration(CONFIG_GROUP, configManager.getProfile().getName(), GSON.toJson(chunkTaskList));
    }

    private ArrayList<ChunkTask> loadChunkTasksFromProfile() {
        String profileName = configManager.getProfile().getName();
        String chunkTasksDataJson = configManager.getRSProfileConfiguration(CONFIG_GROUP, profileName, String.class);

        log.info(profileName);
        log.info(chunkTasksDataJson);
        if(chunkTasksDataJson == null)
            return null;

        try {
            return GSON.fromJson(chunkTasksDataJson, new TypeToken<ArrayList<ChunkTask>>(){}.getType());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
