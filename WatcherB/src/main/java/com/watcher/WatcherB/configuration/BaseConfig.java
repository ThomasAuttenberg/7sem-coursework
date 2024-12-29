package com.watcher.WatcherB.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories (basePackages = "com.watcher.WatcherB.repositories")
@ComponentScan (basePackages = "com.watcher.WatcherB")
public class BaseConfig {
}
