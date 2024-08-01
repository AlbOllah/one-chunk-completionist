package org.chunkmancompletionist;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ChunkmanCompletionistTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ChunkmanCompletionistPlugin.class);
		RuneLite.main(args);
	}
}