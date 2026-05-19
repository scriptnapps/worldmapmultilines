package com.deadmanmultizones;

import lombok.Value;
import net.runelite.api.coords.WorldPoint;

import java.util.List;

@Value
public class DeadmanMultiZonesData
{
    List<List<WorldPoint>> wikiPolygons;

    List<List<WorldPoint>> multiLinesPolygons;
}