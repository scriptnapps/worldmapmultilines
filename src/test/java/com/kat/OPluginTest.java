package com.kat;

import com.deadmanmultizones.DeadmanMultiZonesPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OPluginTest
{
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void main(String[] args)
	{
		try
		{
			ExternalPluginManager.loadBuiltin(DeadmanMultiZonesPlugin.class);
			RuneLite.main(args);
		}
		catch (Throwable t)
		{
			System.err.println("Failed to start RuneLite or load plugin.");
			System.err.println("Error: " + t.getClass().getName());
			System.err.println("Message: " + t.getMessage());

			t.printStackTrace();

			Throwable cause = t.getCause();

			while (cause != null)
			{
				System.err.println("\nCaused by:");
				System.err.println("Error: " + cause.getClass().getName());
				System.err.println("Message: " + cause.getMessage());

				cause.printStackTrace();

				cause = cause.getCause();
			}
		}
	}
}