package nl.hva.dederdekamer.election_backend.dto;


public class PostResponse {
    private final Integer  id;
    private final String title;
    private final String content;
    private final String created;
    private final PostAuthorResponse author;

    public  PostResponse(Integer id, String title, String content, String created, PostAuthorResponse author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.created = created;
        this.author = author;
    }

    public Integer getId() {return id;}
    public String getTitle() {return title;}
    public String getContent() {return content;}
    public String getCreated() {return created;}
    public PostAuthorResponse getAuthor() {return author;}
}
