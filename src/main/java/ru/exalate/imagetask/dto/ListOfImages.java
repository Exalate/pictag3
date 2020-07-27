package ru.exalate.imagetask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOfImages {
    private List<ImageDto> imageList;

    @Override
    public String toString() {
        return "ListOfImages";
    }
}
