package ru.exalate.imagetask.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImageDto {
    private Long id;
    private byte[] image;
    private List<String> tags;
}
