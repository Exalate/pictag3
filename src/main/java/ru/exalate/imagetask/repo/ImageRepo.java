package ru.exalate.imagetask.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.exalate.imagetask.entity.Image;

import java.util.List;

public interface ImageRepo extends JpaRepository<Image, Long> {

    @Query(value = "select * from image left outer join image_tag it on image.id = it.image_id left outer join tag t on it.tag_id = t.id WHERE t.tag = :tagSearch", nativeQuery = true)
    List<Image> findImagesByTagQuery(@Param("tagSearch")String tagSearch);
}
