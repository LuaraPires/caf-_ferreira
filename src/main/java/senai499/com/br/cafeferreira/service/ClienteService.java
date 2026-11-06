
package senai499.com.br.cafeferreira.service;

import senai499.com.br.cafeferreira.model.Cliente;
import senai499.com.br.cafeferreira.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    public List<Cliente> listarInativos() {
        return clienteRepository.findByAtivoFalse();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado."));
    }

    public List<Cliente> pesquisarPorNome(String nome) {
        return clienteRepository
                .findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {

        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException(
                    "Este e-mail já está cadastrado.");
        }

        if (cliente.getSenha() == null ||
                cliente.getSenha().isBlank()) {
            throw new RuntimeException(
                    "A senha é obrigatória.");
        }

        cliente.setSenha(
                passwordEncoder.encode(cliente.getSenha()));

        cliente.setAtivo(true);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dados) {

        Cliente cliente = buscarPorId(id);

        if (!cliente.getEmail().equalsIgnoreCase(dados.getEmail())
                && clienteRepository.existsByEmail(dados.getEmail())) {
            throw new RuntimeException(
                    "Este e-mail já está sendo utilizado.");
        }

        cliente.setNome(dados.getNome());
        cliente.setEmail(dados.getEmail());
        cliente.setTelefone(dados.getTelefone());

        if (dados.getSenha() != null &&
                !dados.getSenha().isBlank()) {
            cliente.setSenha(
                    passwordEncoder.encode(dados.getSenha()));
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void desativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void reativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }
}
