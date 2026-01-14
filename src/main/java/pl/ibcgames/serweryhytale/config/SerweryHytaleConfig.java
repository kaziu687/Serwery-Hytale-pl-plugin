package pl.ibcgames.serweryhytale.config;

import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SerweryHytaleConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String apiToken;
    private List<String> rewardCommands;
    private boolean sendPlayersList;
    private boolean sendPluginsList;

    public boolean isValid() {
        return apiToken != null && !apiToken.isBlank() && !apiToken.equals("TUTAJ_WPISZ_IDENTYFIKATOR");
    }

    public String getApiToken() {
        return apiToken;
    }

    public List<String> getRewardCommands() {
        return rewardCommands;
    }

    public boolean sendPlayersList() {
        return sendPlayersList;
    }

    public boolean sendPluginsList() {
        return sendPluginsList;
    }

    public void load(Path configPath) throws IOException {
        if (!Files.exists(configPath)) {
            createDefault(configPath);
        }

        JsonObject config = loadJsonFromFile(configPath);
        parseFromJson(config);
    }

    public void saveToken(Path configPath, String newToken) throws IOException {
        // Wczytaj template z resources (zawsze ma komentarze)
        var config = loadTemplateFromResources();

        config.addProperty("identyfikator", newToken);
        config.add("komendy", toJsonArray(this.rewardCommands));
        config.addProperty("wysylajListeGraczy", sendPlayersList);
        config.addProperty("wysylajListePluginow", sendPluginsList);

        // Zapisz
        saveJsonToFile(configPath, config);
        this.apiToken = newToken;
    }

    private JsonObject loadJsonFromFile(Path path) throws IOException {
        try (var reader = Files.newBufferedReader(path)) {
            return GSON.fromJson(reader, JsonObject.class);
        }
    }

    private JsonObject loadTemplateFromResources() throws IOException {
        try (var in = getClass().getResourceAsStream("/config.json");
             var reader = new InputStreamReader(in)) {
            return GSON.fromJson(reader, JsonObject.class);
        }
    }

    private void saveJsonToFile(Path path, JsonObject json) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(json, writer);
        }
    }

    private void parseFromJson(JsonObject config) {
        this.apiToken = config.get("identyfikator").getAsString();

        var komendyArray = config.getAsJsonArray("komendy");
        this.rewardCommands = new ArrayList<>(komendyArray.size());
        for (JsonElement jsonElement : komendyArray) {
            this.rewardCommands.add(jsonElement.getAsString());
        }

        this.sendPlayersList = config.get("wysylajListeGraczy").getAsBoolean();
        this.sendPluginsList = config.get("wysylajListePluginow").getAsBoolean();
    }

    private JsonArray toJsonArray(List<String> list) {
        var array = new JsonArray(list.size());
        for (String item : list) {
            array.add(item);
        }
        return array;
    }

    private void createDefault(Path configPath) throws IOException {
        Files.createDirectories(configPath.getParent());
        try (var in = getClass().getResourceAsStream("/config.json")) {
            Files.copy(in, configPath);
        }
    }
}
