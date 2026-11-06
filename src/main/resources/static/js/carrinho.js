
function adicionarProduto(botao) {
    const id = Number(botao.dataset.id);
    const nome = botao.dataset.nome;
    const preco = Number(botao.dataset.preco);

    if (!id || !nome || !Number.isFinite(preco) || preco < 0) {
        alert("Não foi possível adicionar esse produto.");
        return;
    }

    const chave = "carrinhoCafe";

    let carrinho = JSON.parse(
        localStorage.getItem(chave) || "[]"
    );

    const existente = carrinho.find(
        item => Number(item.id) === id
    );

    if (existente) {
        existente.quantidade++;
    } else {
        carrinho.push({
            id,
            nome,
            preco,
            quantidade: 1
        });
    }

    localStorage.setItem(chave, JSON.stringify(carrinho));

    const urlPedido = document.querySelector(
        'meta[name="url-pedido"]'
    )?.content || "/pedido";

    window.location.href = urlPedido;
}
