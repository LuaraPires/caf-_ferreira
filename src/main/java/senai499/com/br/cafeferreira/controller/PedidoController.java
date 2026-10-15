package senai499.com.br.cafeferreira.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class PedidoController {
    @GetMapping("/pedido")
    public String paginaPedido() {
        return "pedido";
    }

    @GetMapping("/cardapio")
    public String paginaCardapio() {
        return "cardapio";
    }

}
