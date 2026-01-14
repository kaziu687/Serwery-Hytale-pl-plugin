package pl.ibcgames.serweryhytale.api;

import com.google.gson.Gson;
import pl.ibcgames.serweryhytale.api.models.ServerResponse;
import pl.ibcgames.serweryhytale.api.models.VoteResponse;
import pl.ibcgames.serweryhytale.config.SerweryHytaleConfig;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class VotifierApiClient {

    private final HttpClient httpClient;
    private final Gson gson;
    private final String baseUrl;
    private final String token;
    private final Duration timeout;

    public VotifierApiClient(SerweryHytaleConfig config) {
        this.httpClient = HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.gson = new Gson();
        this.baseUrl = "https://serwery-hytale.pl";
        this.token = config.getApiToken();
        this.timeout = Duration.ofSeconds(10);
    }

    public CompletableFuture<ServerResponse> getServerResponse() {
        var endpoint = "/api/server-by-key/" + token + "/get-vote";
        return sendRequest(endpoint).thenApply(json -> gson.fromJson(json, ServerResponse.class));
    }

    public CompletableFuture<VoteResponse> checkVote(String playerName) {
        var endpoint = String.format("/api/server-by-key/%s/get-vote/%s", token, playerName);
        return sendRequest(endpoint)
                .thenApply(json -> gson.fromJson(json, VoteResponse.class))
                .exceptionally(ex -> VoteResponse.error(ex.getMessage()));
    }

    private CompletableFuture<String> sendRequest(String endpoint) {
        var request = HttpRequest
                .newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .timeout(timeout)
                .GET()
                .build();

        return httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
