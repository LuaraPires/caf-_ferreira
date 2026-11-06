
package senai499.com.br.cafeferreira.controller;

import senai499.com.br.cafeferreira.model.Cliente;
import senai499.com.br.cafeferreira.service.ClienteService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Verifica se existe um usuário autenticado
    private boolean usuarioAutenticado(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());
    }

    // Busca o cliente que está logado
    private Cliente buscarClienteLogado(Authentication authentication) {
        return clienteService.buscarPorEmail(authentication.getName());
    }

    // ==========================================
    // PAINEL DO CLIENTE
    // ==========================================

    @GetMapping("/cliente/painel")
    public String painelCliente(
            Authentication authentication,
            Model model) {

        if (!usuarioAutenticado(authentication)) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", buscarClienteLogado(authentication));

        return "cliente/painel-cliente";
    }

    // ==========================================
    // VISUALIZAR PERFIL
    // ==========================================

    @GetMapping("/cliente/perfil")
    public String visualizarPerfil(
            Authentication authentication,
            Model model) {

        if (!usuarioAutenticado(authentication)) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", buscarClienteLogado(authentication));

        return "cliente/perfil";
    }

    // ==========================================
    // EDITAR PERFIL
    // ==========================================

    @GetMapping("/cliente/perfil/editar")
    public String editarPerfil(
            Authentication authentication,
            Model model) {

        if (!usuarioAutenticado(authentication)) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", buscarClienteLogado(authentication));

        return "cliente/editar-perfil";
    }

    // ==========================================
    // ATUALIZAR PERFIL
    // ==========================================

    @PostMapping("/cliente/perfil/editar")
    public String atualizarPerfil(
            Authentication authentication,
            @ModelAttribute("cliente") Cliente dados,
            RedirectAttributes redirectAttributes) {

        if (!usuarioAutenticado(authentication)) {
            return "redirect:/login";
        }

        try {
            clienteService.atualizarDados(
                    authentication.getName(),
                    dados
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Seus dados foram atualizados com sucesso!"
            );

            return "redirect:/cliente/perfil";

        } catch (IllegalArgumentException | jakarta.persistence.EntityNotFoundException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );

            return "redirect:/cliente/perfil/editar";
        }
    }
}
