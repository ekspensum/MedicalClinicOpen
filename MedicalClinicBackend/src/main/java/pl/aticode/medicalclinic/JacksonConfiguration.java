package pl.aticode.medicalclinic;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Configuration 
public class JacksonConfiguration {

	/**
	 * Provide jackson configuration for unknown filter id - for avoid error.    
	 * @param objectMapper
	 */
    public JacksonConfiguration(ObjectMapper objectMapper) { 
        objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false)); 
    } 
    
}
