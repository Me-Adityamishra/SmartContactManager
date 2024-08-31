package com.SmartContactManager.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/desc.html")
    public String getDescPage() {
        return "Desc";  
    }
}
