package org.chunkmancompletionist;

import com.google.inject.Provider;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ClientTick;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.api.worldmap.WorldMapIcon;
import net.runelite.api.worldmap.WorldMapRegion;
import net.runelite.api.worldmap.WorldMapRenderer;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.AgilityShortcut;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.hiscore.HiscorePlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.chunkmancompletionist.managers.ChunkTasksManager;
import org.chunkmancompletionist.panel.ChunkMapWindow;
import org.chunkmancompletionist.panel.ChunkmanCompletionistPanel;
import org.chunkmancompletionist.types.ChunkTaskCalculateMessage;
import org.chunkmancompletionist.types.ChunkTaskCalculatorMessage;
import org.chunkmancompletionist.types.ChunkmanTabType;

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
	@Inject private Provider<ChunkMapWindow> mapWindow;
	@Inject private Provider<ChunkTaskCalculator> calculator;
	@Inject private ChunkTasksManager chunkTasksManager;
	@Inject private ChunkmanCompletionist completionist;

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
	}

	@Override
	protected void shutDown() throws Exception {
		clientToolbar.removeNavigation(uiNavigationButton);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		log.info(gameStateChanged.toString());
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			completionist.loadProfile();
			uiPanel.get().reload(true);
		} else if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN) {
			completionist.unloadProfile();
			uiPanel.get().reload(false);
		}
	}

	@Subscribe
	public void onChunkTaskCalculatorMessage(ChunkTaskCalculatorMessage event) {
		mapWindow.get().react(event);
	}

	@Provides
	ChunkmanCompletionistConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ChunkmanCompletionistConfig.class);
	}

	@Subscribe
	void onChunkTaskCalculateMessage(ChunkTaskCalculateMessage event) {
		calculator.get().calculate(event);
	}
}
