package com.udea.sistemas.innosistemas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-test")
    public String testDbConnection() {
        try {
            return "Conectado a: " + jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        } catch (Exception e) {
            return "❌ Error de conexión: " + e.getMessage();
        }
    }
}

