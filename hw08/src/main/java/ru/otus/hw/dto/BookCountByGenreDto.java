package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookCountByGenreDto {

   private String id;

   private Long count;
}
