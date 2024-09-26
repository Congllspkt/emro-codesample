package smartsuite.config.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.config.workplace.lib.WorkplaceCacheInitializer;

@Configuration
public class WorkplaceConfiguration {

    @Bean("workplaceCacheInitializer")
    public WorkplaceCacheInitializer workplaceCacheInitializer() {
        return new WorkplaceCacheInitializer();
    }
}
