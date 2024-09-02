package com.blog.controller;

import com.blog.model.Article;
import com.blog.service.ArticleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
public class ArticleController {
    final ArticleService articleService;

    @Value("${uploadDir}")
    private String uploadFolder;

    @GetMapping("/verwaltung")
    public String verwaltung() {
        return "index";
    }

    @GetMapping("/articleVerwaltung")
    public String getArtikelverwaltung(Model model) {
        List<Article> listArticles = articleService.getAllArticles();
        model.addAttribute("listArticles", listArticles);
        return "verwaltung/artikelverwaltung";
    }

    @GetMapping("/image/display/{id}")
    @ResponseBody
    public void showImage(@PathVariable("id") long id, HttpServletResponse response, Optional<Article> article) throws ServletException, IOException {
        article = articleService.getArticleById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(article.get().getImage());
        response.getOutputStream().close();
    }

    @GetMapping("/addAticle")
    public String addArticle(Model model, Article article) {
        model.addAttribute("article", article);
        return "verwaltung/addArticle";
    }

    @PostMapping("/artikelSpeichern")
    public RedirectView enregistrer(Model model, @RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam("editor") String editor,
                                    HttpServletRequest request,
                                    final @RequestParam("image") MultipartFile file) throws IOException {


        String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
        String fileName = file.getOriginalFilename();
        String filePath = Paths.get(uploadDirectory, fileName).toString();

        if (fileName == null || fileName.contains("..")) {
            model.addAttribute("invalid");
        }

        try {

            File dir = new File(uploadDirectory);

            if (!dir.exists()) {
                dir.mkdirs();
            }


            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            stream.write(file.getBytes());
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] imageData = file.getBytes();

        Article articles = new Article();
        articles.setTitle(title);
        articles.setContent(content);
        articles.setEditor(editor);
        articles.setImage(imageData);

        articleService.saveArticle(articles);

        return new RedirectView("artikelVerwaltung", true);


    }

    @GetMapping("/artikelsLöschen/{id}")
    public ModelAndView removeArticle(@PathVariable("id") Long id) {

        Optional<Article> article = articleService.getArticleById(id);

        if(article.isPresent()) {

            articleService.deleteArticle(id);

            return new ModelAndView("redirect:/articleVerwaltung");

        }else {
            return new ModelAndView("redirect:/articleVerwaltung");
        }

    }


    @GetMapping("/editArticle/{id}")
    public String editArticle(@PathVariable(value = "id") Long id, Model model) {

        Optional<Article> article = articleService.getArticleById(id);

        model.addAttribute("article", article);


        return "verwaltung/editArticle";

    }





    @PostMapping("/editArticle/{id}")
    public RedirectView modification(Model model, @RequestParam("title") String title,
                                     @PathVariable("id") Long id,
                                     @RequestParam("content") String content,
                                     @RequestParam("editor") String editor,
                                     HttpServletRequest request,
                                     final @RequestParam("image") MultipartFile file) throws IOException{


        String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
        String fileName = file.getOriginalFilename();
        String filePath = Paths.get(uploadDirectory, fileName).toString();

        if(fileName == null || fileName.contains("..")) {
            model.addAttribute("invalid");
        }

        try {

            File dir = new File(uploadDirectory);

            if(!dir.exists()) {
                dir.mkdirs();
            }

            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            stream.write(file.getBytes());
            stream.close();

        }catch (Exception e) {
            e.printStackTrace();
        }


        byte[] imageData = file.getBytes();

        Article articles = articleService.getArticleById(id).orElse(null);

        articles.setTitle(title);
        articles.setContent(content);
        articles.setEditor(editor);
        articles.setImage(imageData);

        articleService.updateArticle(articles);

        return new RedirectView("/artikelVerwaltung", true);

    }
}