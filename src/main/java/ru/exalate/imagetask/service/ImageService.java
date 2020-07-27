package ru.exalate.imagetask.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.exalate.imagetask.dto.ImageDto;
import ru.exalate.imagetask.dto.ListOfImages;
import ru.exalate.imagetask.dto.ListOfStrings;
import ru.exalate.imagetask.entity.Tag;
import ru.exalate.imagetask.repo.ImageRepo;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageService {

    @Autowired
    private TagService tagService;
    @Autowired
    private ImageRepo imageRepo;

    private final List<String> list = new ArrayList<>();

    public ListOfStrings uploadImage(byte[] uploadedImage) {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString imgBytes = ByteString.copyFrom(uploadedImage);
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();

            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    log.error("Ошибка: " + res.getError().getMessage());
                    return new ListOfStrings(new ArrayList<>()) {
                    };
                }
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    annotation.getDescription().lines().forEach(list::add);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка во время анализа файла.", e);
        }
        ru.exalate.imagetask.entity.Image image = new ru.exalate.imagetask.entity.Image();
        image.setImage(uploadedImage);
        image.setTags(tagService.getTags(list, image));
        imageRepo.save(image);
        return new ListOfStrings(list);
    }


    @Transactional
    public ListOfStrings getTagsByUrl(String url) {
        List<String> tagList = new ArrayList<>();
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            URL imgUrl = new URL(url);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            URLConnection conn = imgUrl.openConnection();
            conn.setRequestProperty("User-Agent", "Firefox");

            try (InputStream inputStream = conn.getInputStream()) {
                int n = 0;
                byte[] buffer = new byte[1024];
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            }
            byte[] imgByte = output.toByteArray();

            ByteString imgBytes = ByteString.copyFrom(imgByte);

            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    log.error("Ошибка:" + res.getError().getMessage());
                    return null;
                }
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    annotation
                            .getAllFields()
                            .forEach((k, v) -> {
                                if (k.toString().contains("description")) {
                                    tagList.add(v.toString());
                                }
                            });
                }
            }
            ru.exalate.imagetask.entity.Image image = new ru.exalate.imagetask.entity.Image();
            image.setImage(imgByte);
            image.setUrl(url);
            image.setTags(tagService.getTags(tagList, image));
            imageRepo.save(image);

        } catch (Exception e) {
            log.error("Ошибка при анализе ссылки на изображение", e);
        }

        return new ListOfStrings(tagList);
    }

    public ListOfImages getImagesByTag(String tag) {
        return new ListOfImages(getListImageDto(imageRepo.findImagesByTagQuery(tag)));
    }

    public ImageDto getImageById(Long id) {
        ImageDto foundImage = new ImageDto();
        Optional<ru.exalate.imagetask.entity.Image> image = imageRepo.findById(id);
        if (image != null) {
            foundImage.setId(image.get().getId());
            foundImage.setImage(image.get().getImage());
            foundImage.setTags(getTags(image.get().getTags()));
        }
        return foundImage;
    }

    public ListOfStrings editTags(Long id, List<String> tagList) {
        Optional<ru.exalate.imagetask.entity.Image> foundImage = imageRepo.findById(id);
        if (foundImage.isPresent()) {
            ru.exalate.imagetask.entity.Image image = foundImage.get();
            image.setTags(tagService.getTags(tagList, image));
            ru.exalate.imagetask.entity.Image savedImage = imageRepo.save(image);
            return new ListOfStrings(savedImage.getTags().stream()
                    .map(tag -> tag.getTag())
                    .collect(Collectors.toList()));
        } else {
            return new ListOfStrings(new ArrayList<>());
        }

    }

    public ListOfImages getAllImages() {
        return new ListOfImages(getListImageDto(imageRepo.findAll()));
    }

    private List<ImageDto> getListImageDto(List<ru.exalate.imagetask.entity.Image> list) {
        List<ImageDto> dtoList = new ArrayList<>();
        for (ru.exalate.imagetask.entity.Image image : list) {
            ImageDto imageDto = new ImageDto();
            imageDto.setId(image.getId());
            imageDto.setImage(image.getImage());
            imageDto.setTags(getTags(image.getTags()));
            dtoList.add(imageDto);
        }
        return dtoList;
    }

    private List<String> getTags(List<Tag> tags) {
        List<String> list = new ArrayList<>();
        for (Tag tag : tags) {
            list.add(tag.getTag());
        }
        return list;
    }
}
