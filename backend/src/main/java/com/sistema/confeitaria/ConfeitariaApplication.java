package com.sistema.confeitaria;

import com.sistema.confeitaria.model.Categoria;
import com.sistema.confeitaria.model.Produto;
import com.sistema.confeitaria.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ConfeitariaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfeitariaApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(ProdutoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                
                List<Produto> produtos = List.of(
                    // salgados fritos
                    criarProduto("Coxinha de Frango", 1.80, Categoria.SALGADOS_FRITOS),
                    criarProduto("Quibe", 1.80, Categoria.SALGADOS_FRITOS),
                    criarProduto("Boliviano", 1.80, Categoria.SALGADOS_FRITOS),
                    criarProduto("Risole", 1.80, Categoria.SALGADOS_FRITOS),
                    criarProduto("Bolinho misto(queijo e presunto)", 1.80, Categoria.SALGADOS_FRITOS),
                    criarProduto("Pastel frito", 1.80, Categoria.SALGADOS_FRITOS),
                    criarProduto("Salgados Congelados(todos)", 1.80, Categoria.SALGADOS_FRITOS),
                    
                    // salgados assados
                    criarProduto("Empada de Frango", 1.80, Categoria.SALGADOS_ASSADOS),
                    criarProduto("Barquete", 1.80, Categoria.SALGADOS_ASSADOS),
                    criarProduto("Saltenha", 1.80, Categoria.SALGADOS_ASSADOS),
                    criarProduto("Empada", 1.80, Categoria.SALGADOS_ASSADOS),
                    criarProduto("Pastel de Forno", 1.80, Categoria.SALGADOS_ASSADOS),
                    criarProduto("Pãozinho recheado", 1.80, Categoria.SALGADOS_ASSADOS),
                    criarProduto("Pãozinho sem recheio", 1.80, Categoria.SALGADOS_ASSADOS),
                    
                    // doces finos
                    criarProduto("Ameixa", 2.00, Categoria.DOCES_FINOS),
                    criarProduto("Limão", 2.00, Categoria.DOCES_FINOS),
                    criarProduto("Maracujá", 2.00, Categoria.DOCES_FINOS),
                    criarProduto("Amendoim", 2.00, Categoria.DOCES_FINOS),
                    criarProduto("Nozes", 2.00, Categoria.DOCES_FINOS),
                    criarProduto("Damasco", 2.00, Categoria.DOCES_FINOS),
                    criarProduto("Prestígio", 2.00, Categoria.DOCES_FINOS),
                    
                    // doces simples 
                    criarProduto("Brigadeiro", 1.80, Categoria.DOCES_SIMPLES),
                    criarProduto("Casadinho", 1.80, Categoria.DOCES_SIMPLES),
                    criarProduto("Beijinho", 1.80, Categoria.DOCES_SIMPLES),
                    criarProduto("Pastel doce", 1.80, Categoria.DOCES_SIMPLES),
                    criarProduto("Empadinha doce", 1.80, Categoria.DOCES_SIMPLES),
                    criarProduto("Brigadeiro de leite ninho", 1.80, Categoria.DOCES_SIMPLES)
                );

                repository.saveAll(produtos);
            }
        };
    }

    // Método auxiliar para não repetir os "setters" 27 vezes
    private Produto criarProduto(String nome, double preco, Categoria categoria) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setPrecoUnitario(preco);
        p.setCategoria(categoria);
        return p;
    }
}