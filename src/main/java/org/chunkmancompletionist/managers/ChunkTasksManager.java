package org.chunkmancompletionist.managers;

import com.google.inject.Provider;
import net.runelite.client.RuneLite;
import org.chunkmancompletionist.panel.ChunkmanCompletionistPanel;
import org.chunkmancompletionist.panel.SettingsPanel;
import org.chunkmancompletionist.tasks.ChunkTask;
import org.chunkmancompletionist.types.Profile;
import org.chunkmancompletionist.types.TaskGroup;
import org.chunkmancompletionist.types.TaskType;

import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    @Getter private Profile profile = new Profile();

    private File profileFile;
    private File tasksFile;

    public List<ChunkTask> getActiveChunkTasksByType(TaskType taskType) {
        return chunkTaskList.stream().filter(t -> !t.isComplete && t.taskType == taskType).collect(Collectors.toList());
    }

    public void init() {
        loadProfile();
    }

    public void unload() {
        chunkTaskList = new ArrayList<>();
        profile = new Profile();
    }

    public void markTask(ChunkTask task, boolean completed) {
        task.isComplete = completed;
        this.saveChunkTasks();
    }

    public void saveProfile() {
        saveToFile(profileFile, profile);
    }

    public void saveChunkTasks() {
        saveToFile(tasksFile, chunkTaskList);
    }

    private void loadProfile() {
        setupProfile();

        profile = loadFromFile(profileFile, new TypeToken<>() {});
        if(profile == null) {
            profile = new Profile();
            saveProfile();
        }

        chunkTaskList = loadFromFile(tasksFile, new TypeToken<>() {});
        if(chunkTaskList == null) {
            chunkTaskList = new ArrayList<>();
            saveChunkTasks();
        }
    }

    private void setupProfile() {
        String profileKey = configManager.getRSProfileKey();
        File profileFolder = new File(RuneLite.RUNELITE_DIR, "profiles/" + profileKey);
        if(!profileFolder.exists()) {
            profileFolder.mkdirs();
        }

        profileFile = new File(profileFolder, "chunkman-completionist-profile.json");
        if(!profileFile.exists()) {
            try {
                profileFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tasksFile = new File(profileFolder, "chunkman-completionist-tasks.json");
        if(!tasksFile.exists()) {
            try {
                tasksFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private <T> T loadFromFile(File file, TypeToken<T> tokenType) {
        try {
            InputStream stream = new FileInputStream(file);
            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

            return GSON.fromJson(reader, tokenType.getType());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private <T> void saveToFile(File file, T data) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.println(GSON.toJson(data));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
