package nl.hva.dederdekamer.election_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer  id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private Timestamp created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = true)
    private UserEntity author;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Comment> comments = new java.util.ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnoreProperties("posts")
    private List<TagEntity> tags = new ArrayList<>();



    public Post(String title, String content,  UserEntity author) {
        this.title = title;
        this.content = content;
        this.created = Timestamp.from(ZonedDateTime.now(ZoneId.of("Europe/Amsterdam")).toInstant());
        this.author =  author;
    }

    public Post() {
        this.created = Timestamp.from(ZonedDateTime.now(ZoneId.of("Europe/Amsterdam")).toInstant());
    }

    public Integer getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TagEntity> getTags() {return tags;}
    public void setTags(List<TagEntity> tags) {this.tags = tags;}

    public UserEntity getAuthor() {
        return author;
    }
    public void setAuthor(UserEntity author) {
        this.author = author;
    }
    // Geeft de timestamp als string in Europe/Amsterdam tijdzone
    public String getCreatedLocal() {
        if (created == null) return null;
        return created.toInstant().atZone(java.time.ZoneId.of("Europe/Amsterdam")).toString();
    }

    public Object orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}
