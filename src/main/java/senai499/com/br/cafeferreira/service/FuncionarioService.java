
package senai499.com.br.cafeferreira.service;

import senai499.com.br.cafeferreira.model.PerfilUsuario;
import senai499.com.br.cafeferreira.model.Usuario;
import senai499.com.br.cafeferreira.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarFuncionarios() {
        return usuarioRepository.findByPerfil(
                PerfilUsuario.FUNCIONARIO);
    }

    @Transactional
    public void cadastrarFuncionario(
            String nome, String email, String senha) {

        nome = nome == null ? "" : nome.trim();
        email = email == null ? "" :
                email.trim().toLowerCase();
        senha = senha == null ? "" : senha;

        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            throw new IllegalArgumentException(
                    "Preencha todos os campos.");
        }

        if (senha.length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos 8 caracteres.");
        }

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }

        Usuario funcionario = new Usuario();
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario.setSenha(passwordEncoder.encode(senha));

        // O perfil é definido exclusivamente pelo backend.
        funcionario.setPerfil(PerfilUsuario.FUNCIONARIO);
        funcionario.setAtivo(true);
        funcionario.setCliente(null);

        usuarioRepository.save(funcionario);
    }

    @Transactional
    public void inativarFuncionario(Long id) {

        Usuario funcionario = usuarioRepository
                .findByIdAndPerfil(id, PerfilUsuario.FUNCIONARIO)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário não encontrado."));

        funcionario.setAtivo(false);

        usuarioRepository.save(funcionario);
    }

    @Transactional
    public void reativarFuncionario(Long id) {

        Usuario funcionario = usuarioRepository
                .findByIdAndPerfil(id, PerfilUsuario.FUNCIONARIO)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário não encontrado."));

        funcionario.setAtivo(true);

        usuarioRepository.save(funcionario);
    }
}
