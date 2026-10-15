package senai499.com.br.cafeferreira.repository;

import senai499.com.br.cafeferreira.model.Pedido;
import senai499.com.br.cafeferreira.model.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Busca todos os pedidos de um cliente
    List<Pedido> findByCliente(Cliente cliente);

    // Busca pedidos pelo ID do cliente
    List<Pedido> findByClienteId(Long clienteId);

    // Busca pedidos pelo status
    List<Pedido> findByStatusIgnoreCase(String status);

    // Busca pedidos de um cliente por status
    List<Pedido> findByClienteIdAndStatusIgnoreCase(
            Long clienteId,
            String status
    );
}
