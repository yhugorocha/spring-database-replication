package io.git.yhugorocha.replication.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcTemplateConfig {

    @Bean(name = "writerJdbcTemplate")
    public JdbcTemplate writerJdbcTemplate(@Qualifier("writerDataSource") DataSource writerDataSource) {
        return new JdbcTemplate(writerDataSource);
    }

    @Bean(name = "readerJdbcTemplate")
    public JdbcTemplate readerJdbcTemplate(@Qualifier("readerDataSource") DataSource readerDataSource) {
        return new JdbcTemplate(readerDataSource);
    }
}
