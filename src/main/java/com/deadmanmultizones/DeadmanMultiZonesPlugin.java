package com.deadmanmultizones;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
    name = "WorldMap MultiLines",
    description = "Overlays multicombat zones on the world map",
    tags = {"deadman", "dmm", "map", "multi", "pvp","multicombat","worldmap","kat"}
)
public class DeadmanMultiZonesPlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DeadmanMultiZonesWorldMapOverlay worldMapOverlay;

    @Inject
    private DeadmanMultiZonesLoader loader;

    @Getter
    private volatile DeadmanMultiZonesData zonesData;

    @Inject
    private ScheduledExecutorService executor;

    @Override
    protected void startUp()
    {
        overlayManager.add(worldMapOverlay);

        executor.submit(() ->
        {
            try
            {
                log.info("Loading Deadman multi zones...");

                zonesData = loader.load();

                if (zonesData != null)
                {
                    log.info(
                        "Loaded {} wiki polygons and {} multi-lines polygons",
                        zonesData.getWikiPolygons().size(),
                        zonesData.getMultiLinesPolygons().size()
                    );
                }
            }
            catch (Exception ex)
            {
                log.error("Failed to load Deadman zones", ex);
            }
        });
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(worldMapOverlay);
        zonesData = null;
    }

    @Provides
    DeadmanMultiZonesConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DeadmanMultiZonesConfig.class);
    }
}