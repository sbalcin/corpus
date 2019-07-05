package com.domain.corpus;

import com.domain.corpus.model.role.Role;
import com.domain.corpus.model.role.RoleName;
import com.domain.corpus.repository.RoleRepository;
import com.domain.corpus.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.convert.Jsr310Converters;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@AllArgsConstructor
@SpringBootApplication
@EntityScan(basePackageClasses = {
        CorpusApplication.class,
        Jsr310Converters.class
})
public class CorpusApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorpusApplication.class, args);
    }

    @PostConstruct
    void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    private final RoleRepository roleRepository;

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
            roleRepository.save(new Role(RoleName.ROLE_USER));
        };
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
