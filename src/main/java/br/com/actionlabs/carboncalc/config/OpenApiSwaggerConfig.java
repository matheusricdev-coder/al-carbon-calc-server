package br.com.actionlabs.carboncalc.config;

import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Comparator;
import java.util.TreeMap;


@Configuration
public class OpenApiSwaggerConfig {
  @Value("${server.version}")
  private String version;
  @Autowired
  private Environment env;

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("spring")
        .packagesToScan("br.com.actionlabs.carboncalc.rest")
        .addOpenApiCustomizer(sortPathsAlphabetically())
        .build();
  }

  private OpenApiCustomizer sortPathsAlphabetically() {
    return openApi -> {
      TreeMap<String, io.swagger.v3.oas.models.PathItem> sortedPaths = new TreeMap<>(Comparator.naturalOrder());
      sortedPaths.putAll(openApi.getPaths());

      Paths paths = new Paths();
      paths.putAll(sortedPaths);

      openApi.setPaths(paths);
    };
  }

}