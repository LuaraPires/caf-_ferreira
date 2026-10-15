package senai499.com.br.cafeferreira.repository;

import senai499.com.br.cafeferreira.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface CardapioRepository extends JpaRepository<Produto, Long> {

    // Buscar produtos ativos
    List<Produto> findByAtivoTrue();

    // Buscar produtos por categoria
    List<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(String categoria);

    // Buscar produtos pelo nome
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
}
