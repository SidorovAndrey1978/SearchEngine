package com.skillbox.searchengine.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Управляющий класс, ответственный за определение главной страницы приложения.
 */
@Controller
public class DefaultController {

    /**
     * Перенаправляет запрос к главному адресу ('/') на представление главной страницы.
     *
     * @return имя представления главной страницы ("index"), которое будет передано дальше в механизм рендеринга.
     */
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
