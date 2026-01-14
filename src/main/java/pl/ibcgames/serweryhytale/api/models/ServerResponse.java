package pl.ibcgames.serweryhytale.api.models;

import java.util.List;

public class ServerResponse {

    private int id;
    private List<String> text;
    private String vote_url;

    public int getId() {
        return id;
    }

    public List<String> getText() {
        return text;
    }

    public String getVoteUrl() {
        return vote_url;
    }
}
