package pl.ibcgames.serweryhytale.api.models;

public class VoteResponse {

    private int id;
    private boolean can_claim_reward;
    private String message;

    public boolean isSuccess() {
        return message == null || message.isEmpty();
    }

    public boolean canClaimReward() {
        return can_claim_reward;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public static VoteResponse error(String message) {
        var response = new VoteResponse();
        response.message = message;
        return response;
    }
}
