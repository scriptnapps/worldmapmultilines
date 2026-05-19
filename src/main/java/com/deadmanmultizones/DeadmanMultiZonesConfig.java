package com.deadmanmultizones;

import net.runelite.client.config.*;

import java.awt.Color;

@ConfigGroup("WorldMap MultiLines")
public interface DeadmanMultiZonesConfig extends Config
{
    // =========================================
    // WIKI SOURCE
    // =========================================

    @ConfigItem(
        keyName = "showWiki",
        name = "Show Wiki Zones",
        description = "Render polygons from OSRS Wiki source",
        position = 0
    )
    default boolean showWiki()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
        keyName = "wikiFillColor",
        name = "Wiki Fill Color",
        description = "Fill color for Wiki polygons",
        position = 1
    )
    default Color wikiFillColor()
    {
        return new Color(0x3500FF00, true);
    }

    @Alpha
    @ConfigItem(
        keyName = "wikiBorderColor",
        name = "Wiki Border Color",
        description = "Border color for Wiki polygons",
        position = 2
    )
    default Color wikiBorderColor()
    {
        return new Color(0xCC00FF00, true);
    }

    // =========================================
    // MULTI-LINES SOURCE
    // =========================================

    @ConfigItem(
        keyName = "showMultiLines",
        name = "Show Multi-Lines Zones",
        description = "Render polygons from Multi-Lines source",
        position = 3
    )
    default boolean showMultiLines()
    {
        return false;
    }

    @Alpha
    @ConfigItem(
        keyName = "multiFillColor",
        name = "Multi-Lines Fill Color",
        description = "Fill color for Multi-Lines polygons",
        position = 4
    )
    default Color multiFillColor()
    {
        return new Color(0x35FF0000, true);
    }

    @Alpha
    @ConfigItem(
        keyName = "multiBorderColor",
        name = "Multi-Lines Border Color",
        description = "Border color for Multi-Lines polygons",
        position = 5
    )
    default Color multiBorderColor()
    {
        return new Color(0xCCFF0000, true);
    }
}