package propets.tagging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TaggingConfig {

	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
