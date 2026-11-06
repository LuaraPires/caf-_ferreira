package senai499.com.br.cafeferreira.service;

import senai499.com.br.cafeferreira.model.Cliente;
import senai499.com.br.cafeferreira.model.PerfilUsuario;
import senai499.com.br.cafeferreira.model.Usuario;
import senai499.com.br.cafeferreira.repository.ClienteRepository;
import senai499.com.br.cafeferreira.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AutenticacaoService(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void cadastrarCliente(
            String nome,
            String email,
            String telefone,
            String senha,
            String rua,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep) {

        // Normalização dos dados
        nome = nome == null ? "" : nome.trim();
        email = email == null ? "" : email.trim().toLowerCase();
        telefone = telefone == null ? "" : telefone.trim();
        senha = senha == null ? "" : senha;
        rua = rua == null ? "" : rua.trim();
        numero = numero == null ? "" : numero.trim();
        complemento = complemento == null ? "" : complemento.trim();
        bairro = bairro == null ? "" : bairro.trim();
        cidade = cidade == null ? "" : cidade.trim();
        estado = estado == null ? "" : estado.trim().toUpperCase();
        cep = cep == null ? "" : cep.trim();

        // Validação dos campos obrigatórios
        if (nome.isBlank()
                || email.isBlank()
                || telefone.isBlank()
                || senha.isBlank()
                || rua.isBlank()
                || numero.isBlank()
                || bairro.isBlank()
                || cidade.isBlank()
                || estado.isBlank()
                || cep.isBlank()) {

            throw new IllegalArgumentException(
                    "Preencha todos os campos obrigatórios.");
        }

        if (senha.length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos 8 caracteres.");
        }

        if (estado.length() != 2) {
            throw new IllegalArgumentException(
                    "Informe a sigla do estado com 2 letras.");
        }

        if (email.length() > 150) {
            throw new IllegalArgumentException(
                    "O e-mail deve ter no máximo 150 caracteres.");
        }

        // Verifica se o e-mail já existe
        if (clienteRepository.existsByEmail(email)
                || usuarioRepository.existsByEmailIgnoreCase(email)) {

            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }

        // Codifica a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(senha);

        // Cadastra os dados do cliente
        Cliente cliente = new Cliente();

        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        cliente.setSenha(senhaCriptografada);

        cliente.setRua(rua);
        cliente.setNumero(numero);
        cliente.setComplemento(
                complemento.isBlank() ? null : complemento);
        cliente.setBairro(bairro);
        cliente.setCidade(cidade);
        cliente.setEstado(estado);
        cliente.setCep(cep);

        cliente.setAtivo(true);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        // Cadastra o usuário responsável pelo login
        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senhaCriptografada);
        usuario.setPerfil(PerfilUsuario.CLIENTE);
        usuario.setAtivo(true);
        usuario.setCliente(clienteSalvo);

        usuarioRepository.save(usuario);
    }
}
