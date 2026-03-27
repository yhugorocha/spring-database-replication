package io.git.yhugorocha.replication.repository;

import io.git.yhugorocha.replication.model.Item;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemRepository {

    private final JdbcTemplate writerJdbcTemplate;
    private final JdbcTemplate readerJdbcTemplate;

    public ItemRepository(
            @Qualifier("writerJdbcTemplate") JdbcTemplate writerJdbcTemplate,
            @Qualifier("readerJdbcTemplate") JdbcTemplate readerJdbcTemplate) {
        this.writerJdbcTemplate = writerJdbcTemplate;
        this.readerJdbcTemplate = readerJdbcTemplate;
    }

    private final RowMapper<Item> rowMapper = (rs, rowNum) ->
            new Item(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    public void save(String name) {
        writerJdbcTemplate.update(
                "INSERT INTO items(name) VALUES (?)",
                name
        );
    }

    public List<Item> findAllFromReader() {
        //throw new RuntimeException("Não foi possível ler banco de leitura");
        return readerJdbcTemplate.query(
                "SELECT id, name FROM items ORDER BY id",
                rowMapper
        );
    }

    public List<Item> findAllFromWriter() {
        return writerJdbcTemplate.query(
                "SELECT id, name FROM items ORDER BY id",
                rowMapper
        );
    }
}