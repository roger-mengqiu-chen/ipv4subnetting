package com.mengqiu.ipv4subnetting.controller;

import com.mengqiu.ipv4subnetting.response.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping()
    public String getHome() {
        return "index";
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public JsonResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new JsonResponse("Fail", "Invalid input");
    }
}
