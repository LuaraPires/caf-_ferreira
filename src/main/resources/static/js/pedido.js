const CHAVE_CARRINHO = "carrinhoCafe";
let carrinho = JSON.parse(localStorage.getItem(CHAVE_CARRINHO) || "[]");
let modalPedido;

document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("modalPedido");
    if (modal && window.bootstrap) modalPedido = new bootstrap.Modal(modal);
    atualizarCarrinho();
});

function salvarCarrinho() {
    localStorage.setItem(CHAVE_CARRINHO, JSON.stringify(carrinho));
}

function moeda(valor) {
    return Number(valor).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function adicionar(nome, preco) {
    const item = carrinho.find(p => p.nome === nome);
    if (item) item.quantidade += 1;
    else carrinho.push({ nome, preco: Number(preco), quantidade: 1 });
    salvarCarrinho();
    atualizarCarrinho();
}

function aumentar(nome) {
    const item = carrinho.find(p => p.nome === nome);
    if (item) item.quantidade++;
    salvarCarrinho();
    atualizarCarrinho();
}

function diminuir(nome) {
    const item = carrinho.find(p => p.nome === nome);
    if (!item) return;
    item.quantidade--;
    if (item.quantidade <= 0) carrinho = carrinho.filter(p => p.nome !== nome);
    salvarCarrinho();
    atualizarCarrinho();
}

function remover(nome) {
    carrinho = carrinho.filter(p => p.nome !== nome);
    salvarCarrinho();
    atualizarCarrinho();
}

function calcularTotal() {
    return carrinho.reduce((soma, item) => soma + item.preco * item.quantidade, 0);
}

function atualizarCarrinho() {
    const lista = document.getElementById("listaPedido");
    const total = document.getElementById("total");
    if (!lista || !total) return;

    if (carrinho.length === 0) {
        lista.innerHTML = '<p class="text-muted text-center">Seu pedido está vazio.</p>';
    } else {
        lista.innerHTML = carrinho.map(item => `
            <div class="item-carrinho border-bottom py-3">
                <div class="d-flex justify-content-between gap-2">
                    <div>
                        <strong>${escapar(item.nome)}</strong>
                        <div class="text-muted small">${moeda(item.preco)} cada</div>
                    </div>
                    <strong>${moeda(item.preco * item.quantidade)}</strong>
                </div>
                <div class="d-flex align-items-center gap-2 mt-2">
                    <button class="btn btn-sm btn-outline-secondary" type="button" onclick="diminuir('${escaparJS(item.nome)}')">−</button>
                    <span>${item.quantidade}</span>
                    <button class="btn btn-sm btn-outline-secondary" type="button" onclick="aumentar('${escaparJS(item.nome)}')">+</button>
                    <button class="btn btn-sm btn-outline-danger ms-auto" type="button" onclick="remover('${escaparJS(item.nome)}')">Remover</button>
                </div>
            </div>`).join("");
    }
    total.textContent = moeda(calcularTotal());
    const totalModal = document.getElementById("totalModal");
    if (totalModal) totalModal.textContent = moeda(calcularTotal());
}

function escapar(texto) {
    return String(texto).replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));
}

function escaparJS(texto) {
    return String(texto).replace(/\\/g, "\\\\").replace(/'/g, "\\'").replace(/"/g, '\\"');
}

function abrirFinalizacao() {
    if (carrinho.length === 0) {
        alert("Adicione pelo menos um produto antes de finalizar o pedido.");
        return;
    }
    document.getElementById("formularioPedido").classList.remove("d-none");
    document.getElementById("confirmacao").classList.add("d-none");
    document.getElementById("botoesModal").classList.remove("d-none");
    document.getElementById("nome").value = "";
    document.getElementById("pagamento").value = "";
    document.getElementById("observacao").value = "";
    atualizarCarrinho();
    modalPedido?.show();
}

async function finalizarPedido() {
    const nome = document.getElementById("nome").value.trim();
    const pagamento = document.getElementById("pagamento").value;
    const observacao = document.getElementById("observacao").value.trim();

    if (!nome) {
        alert("Informe seu nome.");
        document.getElementById("nome").focus();
        return;
    }
    if (!pagamento) {
        alert("Selecione a forma de pagamento.");
        document.getElementById("pagamento").focus();
        return;
    }
    if (carrinho.length === 0) {
        alert("Seu carrinho está vazio.");
        return;
    }

    const botao = document.querySelector('#botoesModal button[onclick="finalizarPedido()"]');
    if (botao) { botao.disabled = true; botao.textContent = "Enviando..."; }

    try {
        const resposta = await fetch("/api/pedidos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                nome,
                pagamento,
                observacao,
                itens: carrinho.map(item => ({
                    nome: item.nome,
                    preco: item.preco,
                    quantidade: item.quantidade
                }))
            })
        });

        const dados = await resposta.json();
        if (!resposta.ok) throw new Error(dados.mensagem || "Não foi possível enviar o pedido.");

        document.getElementById("formularioPedido").classList.add("d-none");
        document.getElementById("confirmacao").classList.remove("d-none");
        document.getElementById("botoesModal").classList.add("d-none");
        document.getElementById("numeroPedido").textContent =
            `Número do pedido: ${dados.numeroPedido} • Total: ${moeda(dados.total)}`;

        carrinho = [];
        salvarCarrinho();
        atualizarCarrinho();
    } catch (erro) {
        alert(erro.message || "Erro ao enviar pedido. Verifique se o Spring Boot está em execução.");
    } finally {
        if (botao) { botao.disabled = false; botao.textContent = "Confirmar pedido"; }
    }
}
