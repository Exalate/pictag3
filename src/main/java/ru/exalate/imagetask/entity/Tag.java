package ru.exalate.imagetask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tag;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "tags")
    private List<Image> images = new ArrayList<>();

    public Tag(String name, Image image) {
        this.tag = name;
        this.images.add(image);
    }

    @Override
    public String toString() {
        return tag;
    }
}
