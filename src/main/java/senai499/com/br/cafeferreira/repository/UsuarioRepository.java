
package senai499.com.br.cafeferreira.repository;

import senai499.com.br.cafeferreira.model.Usuario;
import senai499.com.br.cafeferreira.model.PerfilUsuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByPerfil(PerfilUsuario perfil);

    List<Usuario> findByPerfilAndAtivoTrue(
            PerfilUsuario perfil);

    Optional<Usuario> findByIdAndPerfil(
            Long id, PerfilUsuario perfil);
}
