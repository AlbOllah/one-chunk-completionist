package org.chunkmancompletionist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("chunkcompletionist")
public interface ChunkmanCompletionistConfig extends Config {
	String CONFIG_GROUP = "chunk-completionist";
	String CONFIG_KEY = "tasks";

	@ConfigItem(
		keyName = "default-starting-chunk",
		name = "Default starting chunk",
		description = "Default starting chunk."
	)
	default int startingChunk()
	{
		return 12850;
	}
}
