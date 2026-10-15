package senai499.com.br.cafeferreira.repository;

import senai499.com.br.cafeferreira.model.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca um cliente pelo e-mail
    Optional<Cliente> findByEmail(String email);

    // Verifica se já existe um cliente com esse e-mail
    boolean existsByEmail(String email);

    // Lista os clientes ativos
    List<Cliente> findByAtivoTrue();

    // Busca clientes pelo nome
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    // Busca clientes ativos pelo nome
    List<Cliente> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
}
