package senai499.com.br.cafeferreira.controller;

import senai499.com.br.cafeferreira.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PedidoController {

    private final ProdutoRepository produtoRepository;

    public PedidoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/pedido")
    public String paginaPedido() {
        return "pedido";
    }

    @GetMapping("/cardapio")
    public String paginaCardapio(Model model) {

        model.addAttribute(
                "produtos",
                produtoRepository.findByAtivoTrue()
        );

        return "cardapio";
    }
}