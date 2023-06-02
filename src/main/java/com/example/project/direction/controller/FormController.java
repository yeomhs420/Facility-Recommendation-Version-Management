package com.example.project.direction.controller;

import com.example.project.direction.dto.InputDto;
import com.example.project.pharmacy.service.PharmacyRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class FormController {

    private final PharmacyRecommendationService pharmacyRecommendationService;

    @GetMapping("/")
    public String main(){
        return "main";
    }

    @GetMapping("/index")
    public String index() {return "index";}

    @GetMapping("/search")
    public ModelAndView postDirection(@ModelAttribute InputDto inputDto){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("output");
        modelAndView.addObject("outputFormList", pharmacyRecommendationService.recommendPharmacyList(inputDto.getAddress(), inputDto.getCategory()));

        return modelAndView;
    }
}
