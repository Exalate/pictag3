package ru.exalate.imagetask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfStrings {
    private List<String> list;
}
