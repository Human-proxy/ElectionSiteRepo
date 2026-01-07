package nl.hva.dederdekamer.election_backend.dto;

public class PostAuthorResponse {
    private final String id;
    private final String username;
    private final String profileImageUrl;

    public PostAuthorResponse(String id, String username, String profileImageUrl) {
        this.id = id;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }
    public String getId() {return id;}
    public String getUsername() {return username;}
    public String getProfileImageUrl() {return profileImageUrl;}
}
