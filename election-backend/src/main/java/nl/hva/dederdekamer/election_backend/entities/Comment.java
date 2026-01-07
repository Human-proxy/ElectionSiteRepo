package nl.hva.dederdekamer.election_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @NotBlank
    @Size(max = 10_000)
    @Column(nullable = false, length = 10_000)
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant created;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id", nullable = false)
    // TODO: Instead of cascade delete, implement "Deleted user" placeholder when author is deleted
    private UserEntity author;

    public Comment() {}

    public Comment(Post post, String content, UserEntity author) {
        this.post = post;
        this.content = content;
        this.author = author;
    }

    @PrePersist
    protected void onCreate() {
        if (this.created == null) {
            this.created = Instant.now();
        }
    }

    public Integer getId() { return id; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreated() { return created; }

    public UserEntity getAuthor() { return author; }
    public void setAuthor(UserEntity author) { this.author = author; }

    public String getCreatedLocal() {
        if (created == null) return null;
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME
                .withZone(ZoneId.of("Europe/Amsterdam"))
                .format(created);
    }
}
