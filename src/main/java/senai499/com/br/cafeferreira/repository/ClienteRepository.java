
package senai499.com.br.cafeferreira.repository;

import senai499.com.br.cafeferreira.model.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository
        extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Cliente> findByAtivoTrue();

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    List<Cliente> findByNomeContainingIgnoreCaseAndAtivoTrue(
            String nome);
}
