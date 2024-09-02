package com.blog.service;

import com.blog.model.Article;
import com.blog.repository.ArticleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class ArticleService {

    final ArticleRepository articleRepository;

    //get all  from database
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    //get article by id
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    //save article on database
    public void saveArticle(Article article) {
        articleRepository.save(article);
    }

    //delete article
    public void deleteArticle(long id) {
        articleRepository.deleteById(id);
    }

    //update article
    public void updateArticle(Article updatedArticle) {
        Optional<Article> ExistingArticleOptional = articleRepository.findById(updatedArticle.getId());
        if (ExistingArticleOptional.isPresent()) {
            Article articleToUpdate = ExistingArticleOptional.get();
            articleToUpdate.setTitle(updatedArticle.getTitle());
            articleToUpdate.setContent(updatedArticle.getContent());
            articleToUpdate.setEditor(updatedArticle.getEditor());
            articleToUpdate.setImage(updatedArticle.getImage());
            articleRepository.saveAndFlush(articleToUpdate);
        }
    }

}
