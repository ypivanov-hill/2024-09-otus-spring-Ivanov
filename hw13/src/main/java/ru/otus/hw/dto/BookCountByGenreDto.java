package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookCountByGenreDto {

   private Long id;

   private Long count;
}
