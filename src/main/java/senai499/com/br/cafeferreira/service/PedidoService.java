
package senai499.com.br.cafeferreira.service;

import senai499.com.br.cafeferreira.model.Cliente;
import senai499.com.br.cafeferreira.model.ItemPedido;
import senai499.com.br.cafeferreira.model.Pedido;
import senai499.com.br.cafeferreira.model.Produto;

import senai499.com.br.cafeferreira.repository.ClienteRepository;
import senai499.com.br.cafeferreira.repository.PedidoRepository;
import senai499.com.br.cafeferreira.repository.ProdutoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    public final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    // Lista todos os pedidos
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // Busca um pedido pelo ID
    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado."));
    }

    // Lista os pedidos de um cliente
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado."));

        return pedidoRepository.findByCliente(cliente);
    }

    // Cadastra um novo pedido
    @Transactional
    public Pedido cadastrar(
            Long clienteId,
            String formaPagamento,
            String observacao) {

        if (formaPagamento == null || formaPagamento.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe a forma de pagamento.");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado."));

        Pedido pedido = new Pedido(
                cliente,
                formaPagamento,
                observacao
        );

        return pedidoRepository.save(pedido);
    }

    // Adiciona um produto ao pedido
    @Transactional
    public Pedido adicionarProduto(
            Long pedidoId,
            Long produtoId,
            Integer quantidade) {

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado."));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new RuntimeException("Produto não encontrado."));

        if (!produto.isAtivo()) {
            throw new IllegalArgumentException(
                    "Este produto não está disponível.");
        }

        ItemPedido item = new ItemPedido(produto, quantidade);

        pedido.adicionarItem(item);
        pedido.calcularTotal();

        return pedidoRepository.save(pedido);
    }

    // Atualiza o status do pedido
    @Transactional
    public Pedido atualizarStatus(Long pedidoId, String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o status do pedido.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado."));

        pedido.setStatus(status.trim().toUpperCase());

        return pedidoRepository.save(pedido);
    }

    // Exclui um pedido
    @Transactional
    public void excluir(Long id) {

        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido não encontrado.");
        }

        pedidoRepository.deleteById(id);
    }
}
