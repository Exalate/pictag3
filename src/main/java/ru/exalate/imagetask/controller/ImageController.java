package ru.exalate.imagetask.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.exalate.imagetask.dto.ImageDto;
import ru.exalate.imagetask.service.ImageService;
import ru.exalate.imagetask.dto.ListOfImages;
import ru.exalate.imagetask.dto.ListOfStrings;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pictags/")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/images/file")
    public ListOfStrings analyzeByUploadedImage(@RequestParam("image") MultipartFile image) throws Exception {
        if (!image.isEmpty()) {
            try {
                byte[] bytes = image.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File( "uploaded")));
                stream.write(bytes);
                stream.close();
                return imageService.uploadImage(bytes);

            } catch (Exception e) {
                log.error("Изображение не загружено" + e.getMessage());
                throw new Exception("Ошибка при загрузке изображения");
            }
        } else {
            return null;
        }
    }

    @PostMapping("/url")
    public Object analyzeByUrl(@RequestParam("url") String url) {
        return imageService.getTagsByUrl(url);
    }
    @GetMapping("/images")
    public ListOfImages getAllImages() {
        return imageService.getAllImages();
    }
    @GetMapping("/tag/{tag}")
    public ListOfImages getImagesByTag(@PathVariable("tag") String tag) {
        return imageService.getImagesByTag(tag);
    }
    @GetMapping("/edit/{id}")
    public ListOfStrings editTags(@PathVariable("id") Long id, @RequestParam("tagList") List<String> tagList) {
        return imageService.editTags(id, tagList);
    }

    @GetMapping("/images/image/{id}")
    public ImageDto getImagesById(@PathVariable("id") Long id) {
        return imageService.getImageById(id);
    }


}
