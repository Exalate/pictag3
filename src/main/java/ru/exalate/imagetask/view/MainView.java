package ru.exalate.imagetask.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import ru.exalate.imagetask.dto.ImageDto;
import ru.exalate.imagetask.dto.ListOfStrings;
import ru.exalate.imagetask.repo.ImageRepo;
import ru.exalate.imagetask.dto.ListOfImages;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route
public class MainView extends Main {

    private final ImageRepo imageRepo;
    RestTemplate client = new RestTemplate();
    private final Grid<ImageDto> grid = new Grid<>(ImageDto.class);

    private TextField imgURL = new TextField("Image URL");
    private TextField tagFilter = new TextField("Filter tag");
    private TextField tagEdit = new TextField("Edit tags");

    private Button btnNewURL = new Button("Upload");
    private Button btnFilter = new Button("Show by tag");
    private Button btnAll = new Button("Show all");
    private Button btnTagEdit = new Button("Edit");

    private Long curId = 0l;

    @Autowired
    public MainView(ImageRepo imageRepo) {
        this.imageRepo = imageRepo;
        setupClient();
        setupGridDefault();
        btnNewURL.addClickListener(i -> uploadIMG(imgURL.getValue()));
        btnFilter.addClickListener(i -> showByTag(tagFilter.getValue()));
        btnAll.addClickListener(i -> showAll());
        btnTagEdit.addClickListener(i -> editTags(tagEdit.getValue()));
        add(imgURL, btnNewURL, tagFilter, btnFilter, btnAll, grid, tagEdit, btnTagEdit);
    }

    private void uploadIMG(String imgUrl) {
        String url = "http://localhost:8080/pictags/url?url=" + imgUrl;
        client.postForLocation(url, ListOfStrings.class);
        showAll();
    }
    private void showAll() {
        String url = "http://localhost:8080/pictags/images";
        ListOfImages listOfImages = client.getForObject(url, ListOfImages.class);
        List<ImageDto> imageList;
        if (listOfImages != null) {
            imageList = listOfImages.getImageList();
        } else {
            imageList = new ArrayList<>();
        }
        grid.setItems(imageList);
    }
    private void showByTag(String tag) {
        String url = "http://localhost:8080/pictags/tag/" + tag;
        ListOfImages listOfImagesFromClient = client.getForObject(url, ListOfImages.class);
        if (listOfImagesFromClient == null) {
            grid.setItems(new ArrayList<>());
        } else {
            grid.setItems(listOfImagesFromClient.getImageList());
        }
    }
    private void editTags(String tags) {
        String url = "http://localhost:8080/pictags/edit/" + curId + "?tagList=" + tags;
        client.getForObject(url, ImageDto.class);
        tagEdit.setValue("");
        showAll();
    }

    private void setupClient() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        client.getMessageConverters().add(mappingJackson2HttpMessageConverter);
    }
    private void setupGridDefault() {
        grid.getColumnByKey("id")
                .setWidth("0.5px")
                .setResizable(true);
        grid.addComponentColumn(i -> getImageForGrid(i.getImage()));
        grid.getColumnByKey("image").setVisible(false);
        grid.addColumn(new NativeButtonRenderer<>("edit tags", e -> fillInTagEdit(e)))
                .setWidth("1px")
                .onEnabledStateChanged(true);
    }
    private Image getImageForGrid(byte[] b){
        StreamResource resource = new StreamResource("dummyImageName.jpg", () -> new ByteArrayInputStream(b));
        Image image = new Image(resource, "dummy image");
        image.setWidth("150px");
        return image;
    }
    private void fillInTagEdit(ImageDto e) {
        curId = e.getId();
        tagEdit.setValue(tagsToLine(e.getTags()));
    }

    private String tagsToLine(List<String> tags) {
        return String.join(",", tags);
    }
}
