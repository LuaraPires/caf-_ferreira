
package senai499.com.br.cafeferreira.repository;

import senai499.com.br.cafeferreira.model.Produto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Lista todos os produtos ativos
    List<Produto> findByAtivoTrue();

    // Busca produtos por categoria
    List<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(String categoria);

    // Busca produtos pelo nome
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca produtos ativos pelo nome
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    // Busca produtos por categoria, incluindo inativos
    List<Produto> findByCategoriaIgnoreCase(String categoria);
}

