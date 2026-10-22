
package senai499.com.br.cafeferreira.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    @GetMapping("/cliente/inicio")
    public String painelCliente(Model model) {

        model.addAttribute("nomeCliente", "Cliente");

        return "painel-cliente";
    }
}
