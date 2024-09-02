package com.blog.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false,unique = true)//veut dire id ne peut pas etre null et elle est unique
    private long id;

    private String title;
    private String content;
    private String editor;

    @Lob
    @Column(name = "image",length = Integer.MAX_VALUE,nullable = true)
    private byte[] image;
}
