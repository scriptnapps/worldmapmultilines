package com.deadmanmultizones;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

@Slf4j
public class DeadmanMultiZonesWorldMapOverlay extends Overlay
{
    private final WorldMapOverlay worldMapOverlay;

    private final DeadmanMultiZonesPlugin plugin;

    private final DeadmanMultiZonesConfig config;

    @Inject
    private Client client;

    @Inject
    DeadmanMultiZonesWorldMapOverlay(
        WorldMapOverlay worldMapOverlay,
        DeadmanMultiZonesPlugin plugin,
        DeadmanMultiZonesConfig config
    )
    {
        this.worldMapOverlay = worldMapOverlay;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    private WorldPoint remapDungeonPoint(WorldPoint wp)
    {
        if (wp.getY() >= 6000)
        {
            return new WorldPoint(
                wp.getX(),
                wp.getY() - 2560,
                wp.getPlane()
            );
        }

        return wp;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Widget worldMap =
            client.getWidget(WidgetInfo.WORLD_MAP_VIEW);

        if (worldMap == null || worldMap.isHidden())
        {
            return null;
        }

        DeadmanMultiZonesData data = plugin.getZonesData();

        if (data == null)
        {
            return null;
        }

        Rectangle bounds = worldMap.getBounds();

        Shape oldClip = graphics.getClip();

        try
        {
            graphics.setClip(bounds);

            graphics.setStroke(new BasicStroke(1f));

            // =========================================
            // WIKI POLYGONS
            // =========================================

            if (config.showWiki())
            {
                drawPolygons(
                    graphics,
                    data.getWikiPolygons(),
                    config.wikiFillColor(),
                    config.wikiBorderColor()
                );
            }

            // =========================================
            // MULTI-LINES POLYGONS
            // =========================================

            if (config.showMultiLines())
            {
                drawPolygons(
                    graphics,
                    data.getMultiLinesPolygons(),
                    config.multiFillColor(),
                    config.multiBorderColor()
                );
            }
        }
        catch (Exception ex)
        {
            log.error("Overlay render failed", ex);
        }
        finally
        {
            graphics.setClip(oldClip);
        }

        return null;
    }

    private void drawPolygons(
        Graphics2D graphics,
        List<List<WorldPoint>> polygons,
        Color fillColor,
        Color borderColor
    )
    {
        for (List<WorldPoint> ring : polygons)
        {
            Polygon polygon = new Polygon();

            for (WorldPoint wp : ring)
            {
                WorldPoint drawPoint = remapDungeonPoint(wp);

                Point p =
                    worldMapOverlay
                        .mapWorldPointToGraphicsPoint(drawPoint);

                if (p == null)
                {
                    continue;
                }

                polygon.addPoint(p.getX(), p.getY());
            }

            if (polygon.npoints < 3)
            {
                continue;
            }

            graphics.setColor(fillColor);
            graphics.fillPolygon(polygon);

            graphics.setColor(borderColor);
            graphics.drawPolygon(polygon);
        }
    }
}