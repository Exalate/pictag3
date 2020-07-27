package ru.exalate.imagetask.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.exalate.imagetask.dto.ImageDto;
import ru.exalate.imagetask.dto.ListOfImages;
import ru.exalate.imagetask.entity.Image;
import ru.exalate.imagetask.entity.Tag;
import ru.exalate.imagetask.repo.ImageRepo;
import ru.exalate.imagetask.repo.TagRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {

    @Mock
    TagRepo tagRepo;
    @Mock
    ImageRepo imageRepo;
    @Mock
    TagService tagService;

    @InjectMocks
    ImageService imageService;

    @Test
    public void firstTest() {
        Assert.assertTrue(true);
    }

    @Test
    public void getTagsByUrl() {
        List<Tag> listTags = new ArrayList<>();
        Tag tag1 = new Tag("tag1", new Image());
        listTags.add(tag1);
        Tag tag2 = new Tag("tag2", new Image());
        listTags.add(tag2);
        Tag tag3 = new Tag("tag3", new Image());
        listTags.add(tag3);

        List<String> listTagsString = new ArrayList<>();
        listTagsString.add("tag1");
        listTagsString.add("tag2");
        listTagsString.add("tag3");
        ListOfImages listOfImages = new ListOfImages();
        ImageDto image1 = new ImageDto();
        image1.setTags(listTagsString);
        image1.setId(1L);

        Image img1 = new Image();
        img1.setId(1L);
        img1.setTags(listTags);

        ImageDto image2 = new ImageDto();
        image2.setTags(List.of("tag1", "tag3"));
        image2.setId(2L);

        Image img2 = new Image();
        img2.setId(2L);
        img2.setTags(List.of(tag1, tag3));

        listOfImages.setImageList(List.of(image1, image2));
        Mockito.when(imageRepo.findImagesByTagQuery(Mockito.any())).thenReturn(List.of(img1, img2));
        ListOfImages listOfImagesRes = imageService.getImagesByTag("test");
        Assert.assertEquals(listOfImages.getImageList().size(), listOfImagesRes.getImageList().size());
        Assert.assertEquals(listOfImages.getImageList().get(0).getTags(), listOfImagesRes.getImageList().get(0).getTags());
        Assert.assertEquals(listOfImages.getImageList().get(1).getTags(), listOfImagesRes.getImageList().get(1).getTags());
    }

    @Test
    public void getImageById() {
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag();
        tag1.setTag("tag1");
        tag1.setId(1L);
        tags.add(tag1);
        Image image = new Image();
        image.setId(1L);
        image.setUrl("http://google.ru");
        image.setTags(tags);
        Mockito.when(imageRepo.findById(1L)).thenReturn(Optional.of(image));
        ImageDto imageDto = imageService.getImageById(1L);
        Assert.assertEquals(image.getId(), imageDto.getId());
        Assert.assertEquals(List.of(image.getTags().get(0).getTag()), imageDto.getTags());
    }

}
