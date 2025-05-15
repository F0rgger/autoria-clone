package com.autoria.clone.config;

import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToCarBrandConverter());
        registry.addConverter(new StringToCarModelConverter());
    }

    private static class StringToCarBrandConverter implements Converter<String, CarBrand> {
        @Override
        public CarBrand convert(String source) {
            if (source == null || source.isEmpty()) return null;
            try {
                return CarBrand.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid CarBrand value: " + source + ". Valid values are: " + Arrays.toString(CarBrand.values()));
            }
        }
    }

    private static class StringToCarModelConverter implements Converter<String, CarModel> {
        @Override
        public CarModel convert(String source) {
            if (source == null || source.isEmpty()) return null;
            try {
                return CarModel.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid CarModel value: " + source + ". Valid values are: " + Arrays.toString(CarModel.values()));
            }
        }
    }
}