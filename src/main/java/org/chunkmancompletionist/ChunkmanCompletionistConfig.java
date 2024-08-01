package org.chunkmancompletionist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("chunkcompletionist")
public interface ChunkmanCompletionistConfig extends Config {
	String CONFIG_GROUP = "chunk-completionist";
	String CONFIG_KEY = "tasks";

	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}
}
