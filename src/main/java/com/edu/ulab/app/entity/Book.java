package com.edu.ulab.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Book {
    private Long id;
    private Long userId;
    private String title;
    private String author;
    private long pageCount;
}
