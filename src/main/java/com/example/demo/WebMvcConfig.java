package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *@author admin
 *@date 2021年12月26日
 */

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //windows本地文件目录
        registry.addResourceHandler("/restaurantRes/**").addResourceLocations("file:E:\\UploadFile\\");
        //windows本地文件目录
        //registry.addResourceHandler("/restaurantRes/**").addResourceLocations("file:/pic/upload/");
    }
}
