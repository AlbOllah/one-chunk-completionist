package org.chunkmancompletionist.panel;

import com.google.inject.Singleton;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.PluginPanel;
import org.chunkmancompletionist.*;
import com.google.inject.Inject;
import org.chunkmancompletionist.managers.ChunkTasksManager;

import javax.swing.*;

@Singleton
public class ChunkmanCompletionistPanel extends PluginPanel {
    @Inject private ChunkmanCompletionistConfig config;
    @Inject private ClientThread clientThread;
    @Inject private ChunkTasksManager chunkTasksManager;

    private JPanel chunkmanPanel;

    public void init(boolean isLoggedIn) {
        //chunkTasksManager.getChunkTaskList();
    }
}
