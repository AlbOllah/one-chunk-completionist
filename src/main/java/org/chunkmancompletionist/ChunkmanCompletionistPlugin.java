package org.chunkmancompletionist;

import com.google.inject.Provider;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.hiscore.HiscorePlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.chunkmancompletionist.managers.ChunkTasksManager;
import org.chunkmancompletionist.panel.ChunkmanCompletionistPanel;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Chunkman Completionist"
)
public class ChunkmanCompletionistPlugin extends Plugin {
	@Inject private Client client;
	@Inject private ClientToolbar clientToolbar;
	@Inject private ChunkmanCompletionistConfig config;
	@Inject private Provider<ChunkmanCompletionistPanel> uiPanel;
	@Inject private ChunkTasksManager chunkTasksManager;

	private NavigationButton uiNavigationButton;
	private boolean lastWorldWasMembers;

	@Override
	protected void startUp() throws Exception {
		final BufferedImage icon = ImageUtil.loadImageResource(HiscorePlugin.class, "hardcore_ironman.png");

		uiNavigationButton = NavigationButton.builder()
				.tooltip("Chunkman Completionist")
				.icon(icon)
				.priority(6)
				.panel(uiPanel.get())
				.build();

		clientToolbar.addNavigation(uiNavigationButton);
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception {
		clientToolbar.removeNavigation(uiNavigationButton);
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
			chunkTasksManager.loadChunkTasks();

		}
	}

	@Provides
	ChunkmanCompletionistConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ChunkmanCompletionistConfig.class);
	}
}
