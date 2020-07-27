package ru.exalate.imagetask.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.exalate.imagetask.entity.Tag;

import java.util.Optional;


@Repository
public interface TagRepo extends JpaRepository<Tag, Long> {

    Optional<Tag> findFirstByTag(String tag);
}
