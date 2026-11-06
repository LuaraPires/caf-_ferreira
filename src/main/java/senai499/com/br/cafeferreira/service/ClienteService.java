
package senai499.com.br.cafeferreira.service;

import senai499.com.br.cafeferreira.model.Cliente;
import senai499.com.br.cafeferreira.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Buscar cliente pelo e-mail
    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("Cliente não encontrado.")
                );
    }

    // Listar todos os clientes
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // Listar somente clientes ativos
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    // Buscar cliente pelo ID
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Cliente não encontrado.")
                );
    }

    // Atualizar dados do cliente
    @Transactional
    public void atualizarDados(String email, Cliente dados) {

        Cliente cliente = buscarPorEmail(email);

        if (dados.getNome() == null || dados.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe seu nome.");
        }

        if (dados.getTelefone() == null || dados.getTelefone().isBlank()) {
            throw new IllegalArgumentException("Informe seu telefone.");
        }

        cliente.setNome(dados.getNome().trim());
        cliente.setTelefone(dados.getTelefone().trim());

        clienteRepository.save(cliente);
    }

    // Inativar cliente
    @Transactional
    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    // Reativar cliente
    @Transactional
    public void reativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }
}
