package ru.exalate.imagetask.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private byte[] image;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name="image_tag",
            joinColumns = @JoinColumn (name = "image_id"),
            inverseJoinColumns = @JoinColumn (name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    @Override
    public String toString() {
        return "Image{id:" + id +"}";
    }
}
