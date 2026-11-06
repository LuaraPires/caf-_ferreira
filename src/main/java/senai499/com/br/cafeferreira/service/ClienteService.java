import org.springframework.transaction.annotation.Transactional;
public Cliente buscarPorEmail(String email) {

    return clienteRepository.findByEmail(email)
            .orElseThrow(() ->
                    new jakarta.persistence.EntityNotFoundException(
                            "Cliente não encontrado."
                    )
            );
}

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

    cliente.setRua(dados.getRua());
    cliente.setNumero(dados.getNumero());
    cliente.setComplemento(dados.getComplemento());
    cliente.setBairro(dados.getBairro());
    cliente.setCidade(dados.getCidade());
    cliente.setEstado(dados.getEstado());
    cliente.setCep(dados.getCep());

    clienteRepository.save(cliente);
}
