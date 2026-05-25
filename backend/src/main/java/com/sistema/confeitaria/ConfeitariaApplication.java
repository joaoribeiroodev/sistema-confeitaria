package com.sistema.confeitaria;

import com.sistema.confeitaria.model.Categoria;
import com.sistema.confeitaria.model.Produto;
import com.sistema.confeitaria.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConfeitariaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfeitariaApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(ProdutoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Produto p1 = new Produto(); p1.setNome("Coxinha de Frango"); p1.setDescricao("Cento frito na hora"); p1.setPrecoUnitario(80.00); p1.setCategoria(Categoria.SALGADOS_FRITOS);
                Produto p2 = new Produto(); p2.setNome("Empada de Frango"); p2.setDescricao("Salgado assado super recheado"); p2.setPrecoUnitario(95.00); p2.setCategoria(Categoria.SALGADOS_ASSADOS);
                Produto p3 = new Produto(); p3.setNome("Camafeu de Nozes"); p3.setDescricao("Doce fino decorado"); p3.setPrecoUnitario(150.00); p3.setCategoria(Categoria.DOCES_FINOS);
                Produto p4 = new Produto(); p4.setNome("Brigadeiro Tradicional"); p4.setDescricao("Chocolate nobre granulado"); p4.setPrecoUnitario(70.00); p4.setCategoria(Categoria.DOCES_SIMPLES);
                
                repository.save(p1); repository.save(p2); repository.save(p3); repository.save(p4);
            }
        };
    }
}