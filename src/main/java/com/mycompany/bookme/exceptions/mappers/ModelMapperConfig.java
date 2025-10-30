package com.mycompany.bookme.exceptions.mappers;


import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Configuration
public class ModelMapperConfig {

  // todo: sacar estos converters de aqui
  private final Converter<ZonedDateTime, OffsetDateTime> zonedToOffset =
      ctx -> {
        ZonedDateTime source = ctx.getSource();
        return (source != null) ? source.toOffsetDateTime() : null;
      };

  private final Converter<OffsetDateTime, ZonedDateTime> offsetToZoned =
      ctx -> {
        OffsetDateTime source = ctx.getSource();
        return (source != null) ? source.toZonedDateTime() : null;
      };

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    configureUserMapping(mapper);
    return mapper;
  }

  private void configureUserMapping(ModelMapper mapper) {
    mapper
        .getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
        .setSkipNullEnabled(true);

    mapper.addConverter(zonedToOffset);
    mapper.addConverter(offsetToZoned);
  }
}
