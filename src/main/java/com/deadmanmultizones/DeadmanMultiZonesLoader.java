package com.deadmanmultizones;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class DeadmanMultiZonesLoader
{


    private static final String WIKI_URL =
        "https://oldschool.runescape.wiki/w/Map:DeadmanAnnihilationMulticombatZones";

    private static final String MULTI_LINES_URL =
        "https://raw.githubusercontent.com/tsbreuer/Multi-Lines/geoJSON/src/main/java/com/tsbreuer/multilines/MultiLinesData.json";

    private static final Path LOCAL_WIKI_HTML = Paths.get(
        "wiki.html"
    );

    private static final Path LOCAL_MULTI_LINES_JSON = Paths.get(
        "multilines.json"
    );

    InputStream wiki = getClass().getResourceAsStream("wiki.html");
    InputStream multilines = getClass().getResourceAsStream("multilines.json");

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private Gson gson;

    public DeadmanMultiZonesData load()
    {
        List<List<WorldPoint>> wikiPolygons = new ArrayList<>();

        List<List<WorldPoint>> multiLinesPolygons = new ArrayList<>();

        // =========================================
        // WIKI DATA
        // =========================================

        try
        {
            JsonObject wikiData = loadWikiData();

            wikiPolygons.addAll(parsePolygons(wikiData));

            log.info("Loaded wiki polygons: {}", wikiPolygons.size());
        }
        catch (Exception ex)
        {
            log.error("Wiki data failed completely", ex);
        }

        // =========================================
        // MULTI-LINES DATA
        // =========================================

        try
        {
            JsonObject multiLinesData = loadMultiLinesData();

            multiLinesPolygons.addAll(parsePolygons(multiLinesData));

            log.info("Loaded Multi-Lines polygons: {}", multiLinesPolygons.size());
        }
        catch (Exception ex)
        {
            log.error("Multi-Lines data failed completely", ex);
        }

        return new DeadmanMultiZonesData(
            wikiPolygons,
            multiLinesPolygons
        );
    }

    private JsonObject loadWikiData() throws IOException
    {
        try
        {
            //if (Files.exists(LOCAL_WIKI_HTML))
            //{
                //String html = Files.readString(LOCAL_WIKI_HTML);

                String html = new String(
                    wiki.readAllBytes(),
                    StandardCharsets.UTF_8
                );

                return extractKartographerLiveData(html);
            //}
        }
        catch (Exception ex)
        {
            log.warn("Local wiki load failed", ex);
        }

        log.info("Loading wiki from live URL");

        String html = fetchText(WIKI_URL);

        return extractKartographerLiveData(html);
    }

    private JsonObject loadMultiLinesData() throws IOException
    {
        try
        {
           // if (Files.exists(LOCAL_MULTI_LINES_JSON))
           // {
              //  String json = Files.readString(LOCAL_MULTI_LINES_JSON);

                String json = new String(
                    multilines.readAllBytes(),
                    StandardCharsets.UTF_8
                );
                return gson.fromJson(json, JsonObject.class);
            //}
        }
        catch (Exception ex)
        {
            log.warn("Local Multi-Lines load failed", ex);
        }

        log.info("Loading Multi-Lines from GitHub");

        String json = fetchText(MULTI_LINES_URL);

        return gson.fromJson(json, JsonObject.class);
    }

    private String fetchText(String url) throws IOException
    {
        Request request = new Request.Builder()
            .url(url)
            .header("User-Agent", "RuneLite DeadmanMultiZones plugin")
            .build();

        try (Response response = okHttpClient.newCall(request).execute())
        {
            if (!response.isSuccessful() || response.body() == null)
            {
                throw new IOException(
                    "Bad response from " + url + ": " + response.code()
                );
            }

            return response.body().string();
        }
    }

    private JsonObject extractKartographerLiveData(String html)
    {
        String marker = "\"wgKartographerLiveData\":";

        int start = html.indexOf(marker);

        if (start == -1)
        {
            throw new IllegalStateException(
                "wgKartographerLiveData not found"
            );
        }

        int objectStart = html.indexOf('{', start);

        int objectEnd = findMatchingBrace(html, objectStart);

        String json = html.substring(objectStart, objectEnd + 1);

        return gson.fromJson(json, JsonObject.class);
    }

    private int findMatchingBrace(String text, int start)
    {
        int depth = 0;

        boolean inString = false;

        boolean escaped = false;

        for (int i = start; i < text.length(); i++)
        {
            char c = text.charAt(i);

            if (escaped)
            {
                escaped = false;
                continue;
            }

            if (c == '\\')
            {
                escaped = true;
                continue;
            }

            if (c == '"')
            {
                inString = !inString;
                continue;
            }

            if (inString)
            {
                continue;
            }

            if (c == '{')
            {
                depth++;
            }
            else if (c == '}')
            {
                depth--;

                if (depth == 0)
                {
                    return i;
                }
            }
        }

        throw new IllegalStateException(
            "Could not find end of JSON object"
        );
    }

    private List<List<WorldPoint>> parsePolygons(JsonObject data)
    {
        List<List<WorldPoint>> polygons = new ArrayList<>();

        if (data == null)
        {
            return polygons;
        }

        if (data.has("MultiLines"))
        {
            parseMultiLinesJson(data, polygons);

            return polygons;
        }

        if (data.has("type")
            && "FeatureCollection".equals(
            data.get("type").getAsString()))
        {
            parseFeatureCollection(data, polygons);

            return polygons;
        }

        if (data.entrySet().isEmpty())
        {
            return polygons;
        }

        JsonElement first =
            data.entrySet()
                .iterator()
                .next()
                .getValue();

        if (!first.isJsonArray())
        {
            return polygons;
        }

        for (JsonElement rootValue : first.getAsJsonArray())
        {
            if (rootValue.isJsonObject())
            {
                parseFeatureCollection(
                    rootValue.getAsJsonObject(),
                    polygons
                );
            }
        }

        return polygons;
    }

    private void parseMultiLinesJson(
        JsonObject data,
        List<List<WorldPoint>> polygons
    )
    {
        JsonObject multiLines = data.getAsJsonObject("MultiLines");

        if (multiLines == null || !multiLines.has("Areas"))
        {
            return;
        }

        JsonArray areas = multiLines.getAsJsonArray("Areas");

        for (JsonElement areaElement : areas)
        {
            JsonObject area = areaElement.getAsJsonObject();

            if (area.has("Removed")
                && area.get("Removed").getAsBoolean())
            {
                continue;
            }

            if (area.has("Enabled")
                && !area.get("Enabled").getAsBoolean())
            {
                continue;
            }

            if (!area.has("Tiles"))
            {
                continue;
            }

            JsonArray tiles = area.getAsJsonArray("Tiles");

            for (JsonElement tileElement : tiles)
            {
                JsonObject tile = tileElement.getAsJsonObject();

                int x = tile.get("x").getAsInt();
                int y = tile.get("y").getAsInt();
                int width = tile.get("width").getAsInt();
                int height = tile.get("height").getAsInt();

                List<WorldPoint> rect = new ArrayList<>();

                rect.add(new WorldPoint(x, y, 0));
                rect.add(new WorldPoint(x + width, y, 0));
                rect.add(new WorldPoint(x + width, y + height, 0));
                rect.add(new WorldPoint(x, y + height, 0));
                rect.add(new WorldPoint(x, y, 0));

                polygons.add(rect);
            }
        }
    }

    private void parseFeatureCollection(
        JsonObject featureCollection,
        List<List<WorldPoint>> polygons
    )
    {
        if (featureCollection == null
            || !featureCollection.has("features"))
        {
            return;
        }

        JsonArray features =
            featureCollection.getAsJsonArray("features");

        for (JsonElement featureElement : features)
        {
            JsonObject feature = featureElement.getAsJsonObject();

            if (!feature.has("geometry")
                || feature.get("geometry").isJsonNull())
            {
                continue;
            }

            JsonObject geometry =
                feature.getAsJsonObject("geometry");

            JsonObject properties =
                feature.has("properties")
                    && feature.get("properties").isJsonObject()
                    ? feature.getAsJsonObject("properties")
                    : new JsonObject();

            int plane =
                properties.has("plane")
                    ? properties.get("plane").getAsInt()
                    : 0;

            if (!geometry.has("type")
                || !geometry.has("coordinates"))
            {
                continue;
            }

            String type = geometry.get("type").getAsString();

            if ("Polygon".equals(type))
            {
                parsePolygonCoordinates(
                    geometry.getAsJsonArray("coordinates"),
                    plane,
                    polygons
                );
            }
            else if ("MultiPolygon".equals(type))
            {
                JsonArray multiPolygon =
                    geometry.getAsJsonArray("coordinates");

                for (JsonElement polygonElement : multiPolygon)
                {
                    parsePolygonCoordinates(
                        polygonElement.getAsJsonArray(),
                        plane,
                        polygons
                    );
                }
            }
        }
    }

    private void parsePolygonCoordinates(
        JsonArray rings,
        int plane,
        List<List<WorldPoint>> polygons
    )
    {
        for (JsonElement ringElement : rings)
        {
            JsonArray ring = ringElement.getAsJsonArray();

            List<WorldPoint> points = new ArrayList<>();

            for (JsonElement coordElement : ring)
            {
                JsonArray coord = coordElement.getAsJsonArray();

                int x = coord.get(0).getAsInt();
                int y = coord.get(1).getAsInt();

                points.add(new WorldPoint(x, y, plane));
            }

            if (points.size() >= 3)
            {
                polygons.add(points);
            }
        }
    }
}