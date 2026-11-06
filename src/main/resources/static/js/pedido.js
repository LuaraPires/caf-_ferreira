
const CHAVE_CARRINHO = "carrinhoCafe";

let carrinho = JSON.parse(localStorage.getItem(CHAVE_CARRINHO) || "[]");
let modalPedido = null;

document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("modalPedido");

    if (modal && window.bootstrap) {
        modalPedido = new bootstrap.Modal(modal);
    }

    // Normaliza os dados antigos do carrinho.
    carrinho = carrinho.map(item => ({
        ...item,
        id: item.id ?? null,
        preco: Number(item.preco),
        quantidade: Math.max(1, Number(item.quantidade) || 1)
    }));

    salvarCarrinho();
    atualizarCarrinho();
});

function salvarCarrinho() {
    localStorage.setItem(CHAVE_CARRINHO, JSON.stringify(carrinho));
}

function moeda(valor) {
    return Number(valor).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

function identificador(item) {
    return item.id !== null && item.id !== undefined
        ? String(item.id)
        : item.nome;
}

// Compatibilidade com chamadas antigas que usam nome e preço.
function adicionar(nome, preco) {
    const item = carrinho.find(p => p.nome === nome);

    if (item) {
        item.quantidade++;
    } else {
        carrinho.push({
            id: null,
            nome,
            preco: Number(preco),
            quantidade: 1
        });
    }

    salvarCarrinho();
    atualizarCarrinho();
}

function aumentar(idOuNome) {
    const item = carrinho.find(
        p => identificador(p) === String(idOuNome)
    );

    if (item) {
        item.quantidade++;
        salvarCarrinho();
        atualizarCarrinho();
    }
}

function diminuir(idOuNome) {
    const item = carrinho.find(
        p => identificador(p) === String(idOuNome)
    );

    if (!item) return;

    item.quantidade--;

    if (item.quantidade <= 0) {
        carrinho = carrinho.filter(
            p => identificador(p) !== String(idOuNome)
        );
    }

    salvarCarrinho();
    atualizarCarrinho();
}

function remover(idOuNome) {
    carrinho = carrinho.filter(
        p => identificador(p) !== String(idOuNome)
    );

    salvarCarrinho();
    atualizarCarrinho();
}

function calcularTotal() {
    return carrinho.reduce(
        (soma, item) => soma + item.preco * item.quantidade,
        0
    );
}

function escapar(texto) {
    return String(texto).replace(/[&<>"']/g, caractere => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    }[caractere]));
}

function atualizarCarrinho() {
    const lista = document.getElementById("listaPedido");
    const total = document.getElementById("total");

    if (!lista || !total) return;

    if (carrinho.length === 0) {
        lista.innerHTML = `
            <p class="text-muted text-center">
                Seu pedido está vazio.
            </p>
        `;
    } else {
        lista.innerHTML = carrinho.map(item => {
            const id = escapar(identificador(item));

            return `
                <div class="item-carrinho border-bottom py-3">

                    <div class="d-flex justify-content-between gap-2">
                        <div>
                            <strong>${escapar(item.nome)}</strong>

                            <div class="text-muted small">
                                ${moeda(item.preco)} cada
                            </div>
                        </div>

                        <strong>
                            ${moeda(item.preco * item.quantidade)}
                        </strong>
                    </div>

                    <div class="d-flex align-items-center gap-2 mt-2">

                        <button
                            class="btn btn-sm btn-outline-secondary"
                            type="button"
                            data-acao="diminuir"
                            data-id="${id}">
                            −
                        </button>

                        <span>${item.quantidade}</span>

                        <button
                            class="btn btn-sm btn-outline-secondary"
                            type="button"
                            data-acao="aumentar"
                            data-id="${id}">
                            +
                        </button>

                        <button
                            class="btn btn-sm btn-outline-danger ms-auto"
                            type="button"
                            data-acao="remover"
                            data-id="${id}">
                            Remover
                        </button>

                    </div>
                </div>
            `;
        }).join("");
    }

    total.textContent = moeda(calcularTotal());

    const totalModal = document.getElementById("totalModal");

    if (totalModal) {
        totalModal.textContent = moeda(calcularTotal());
    }
}

// Eventos dos botões do carrinho.
document.addEventListener("click", event => {
    const botao = event.target.closest("[data-acao]");

    if (!botao) return;

    const id = botao.dataset.id;
    const acao = botao.dataset.acao;

    if (acao === "aumentar") aumentar(id);
    if (acao === "diminuir") diminuir(id);
    if (acao === "remover") remover(id);
});

function abrirFinalizacao() {
    if (carrinho.length === 0) {
        alert("Adicione pelo menos um produto antes de finalizar.");
        return;
    }

    // Não permite finalizar itens antigos sem ID.
    if (carrinho.some(item => item.id === null)) {
        alert("Existem produtos antigos no carrinho. Remova-os e adicione novamente pelo cardápio.");
        return;
    }

    document.getElementById("formularioPedido").classList.remove("d-none");
    document.getElementById("confirmacao").classList.add("d-none");
    document.getElementById("botoesModal").classList.remove("d-none");

    document.getElementById("pagamento").value = "";
    document.getElementById("observacao").value = "";

    atualizarCarrinho();
    modalPedido?.show();
}

async function finalizarPedido() {
    const pagamento = document.getElementById("pagamento").value;
    const observacao = document.getElementById("observacao").value.trim();

    if (!pagamento) {
        alert("Selecione a forma de pagamento.");
        document.getElementById("pagamento").focus();
        return;
    }

    if (carrinho.length === 0) {
        alert("Seu carrinho está vazio.");
        return;
    }

    if (carrinho.some(item => item.id === null)) {
        alert("Remova os produtos antigos e adicione-os novamente pelo cardápio.");
        return;
    }

    const botao = document.querySelector(
        '#botoesModal button[onclick="finalizarPedido()"]'
    );

    if (botao) {
        botao.disabled = true;
        botao.textContent = "Enviando...";
    }

    try {
        const urlApi = document.querySelector(
            'meta[name="url-pedidos"]'
        )?.content || "/api/pedidos";

        const csrfToken = document.querySelector(
            'meta[name="_csrf"]'
        )?.content;

        const csrfHeader = document.querySelector(
            'meta[name="_csrf_header"]'
        )?.content;

        const headers = {
            "Content-Type": "application/json"
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        const resposta = await fetch(urlApi, {
            method: "POST",
            headers,
            body: JSON.stringify({
                pagamento,
                observacao,
                itens: carrinho.map(item => ({
                    produtoId: item.id,
                    quantidade: item.quantidade
                }))
            })
        });

        const dados = await resposta.json();

        if (!resposta.ok) {
            throw new Error(
                dados.mensagem || "Não foi possível enviar o pedido."
            );
        }

        document.getElementById("formularioPedido").classList.add("d-none");
        document.getElementById("confirmacao").classList.remove("d-none");
        document.getElementById("botoesModal").classList.add("d-none");

        document.getElementById("numeroPedido").textContent =
            `Número do pedido: ${dados.numeroPedido} • Total: ${moeda(dados.total)}`;

        carrinho = [];
        salvarCarrinho();
        atualizarCarrinho();

    } catch (erro) {
        alert(erro.message || "Erro ao enviar o pedido.");
    } finally {
        if (botao) {
            botao.disabled = false;
            botao.textContent = "Confirmar pedido";
        }
    }
}
