package ru.exalate.imagetask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.exalate.imagetask.entity.Image;
import ru.exalate.imagetask.entity.Tag;
import ru.exalate.imagetask.repo.TagRepo;

import java.util.*;


@Service
public class TagService {

    @Autowired
    private TagRepo tagRepo;

    public List<Tag> getTags(List<String> tagList, Image image) {
        List<Tag> tagSet = new ArrayList<>();
        for (String tag : tagList) {
            Optional<Tag> foundTag = tagRepo.findFirstByTag(tag);
            if (foundTag.isEmpty()) {
                tagSet.add(new Tag(tag, image));
            } else {
                Tag found = foundTag.get();
                found.getImages().add(image);
                tagSet.add(found);
            }
        }
        return tagSet;
    }
}
