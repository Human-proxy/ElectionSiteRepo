package nl.hva.dederdekamer.election_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnoreProperties("tags")
    private Set<Post> posts = new HashSet<>();


    @NotBlank
    @Size(max = 10_000)
    @Column(name = "tag_name", nullable = false, length = 10_000)
    private String tagName;

    public Integer getId() { return id; }
    public Set<Post> getPosts() { return posts;}
    public void setPosts(Set<Post> posts) {this.posts = posts;}
    public String getTagName() { return tagName; }
    public void setId(Integer id) {this.id = id;}
    public void setTagName(String tagName) {this.tagName = tagName;}

}
