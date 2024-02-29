package com.scaffold.boot3.common.config;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;


/**
 * SPA Project를 내장하는 경우 오류 페이지를 index로 포워드 해준다.
 */
@Configuration
public class SpaErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(jakarta.servlet.http.HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (status == HttpStatus.NOT_FOUND) {
            return new ModelAndView("forward:/");
        }

        return null;
    }
}
