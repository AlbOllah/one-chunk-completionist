package org.chunkmancompletionist.panel;

import com.google.inject.Singleton;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.PluginPanel;
import org.chunkmancompletionist.*;
import com.google.inject.Inject;
import org.chunkmancompletionist.managers.ChunkTasksManager;
import org.chunkmancompletionist.types.Challenge;
import org.chunkmancompletionist.types.Chunk;

import javax.swing.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Singleton
@Slf4j
public class ChunkmanCompletionistPanel extends PluginPanel {
    @Inject private ChunkmanCompletionistConfig config;
    @Inject private ClientThread clientThread;
    @Inject private ChunkTasksManager chunkTasksManager;

    private JPanel chunkmanPanel;

    public void init(boolean isLoggedIn) {
        Map<String, Map<String, Challenge>> challenges = loadFromFile("/challenges.json", new TypeToken<>() {});
        Map<String, Chunk> chunks = loadFromFile("/chunks.json", new TypeToken<>() {});
        //chunkTasksManager.getChunkTaskList();
    }

    private <T> T loadFromFile(String resourceName, TypeToken<T> tokenType) {
        InputStream stream = ChunkmanCompletionistPanel.class.getResourceAsStream(resourceName);
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        return GSON.fromJson(reader, tokenType.getType());
    }
}
