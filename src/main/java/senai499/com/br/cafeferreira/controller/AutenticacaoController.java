package senai499.com.br.cafeferreira.controller;

import senai499.com.br.cafeferreira.service.AutenticacaoService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(
            AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/criar-conta")
    public String criarConta() {
        return "criar-conta";
    }

    @PostMapping("/criar-conta")
    public String cadastrarCliente(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String senha,
            @RequestParam("confirmarSenha") String confirmarSenha,
            @RequestParam String rua,
            @RequestParam String numero,
            @RequestParam(required = false, defaultValue = "") String complemento,
            @RequestParam String bairro,
            @RequestParam String cidade,
            @RequestParam String estado,
            @RequestParam String cep,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Confirma se as senhas são iguais
        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            preencherFormulario(
                    model, nome, email, telefone, rua, numero,
                    complemento, bairro, cidade, estado, cep);
            return "criar-conta";
        }

        try {
            // Cadastra o cliente com todos os dados
            autenticacaoService.cadastrarCliente(
                    nome,
                    email,
                    telefone,
                    senha,
                    rua,
                    numero,
                    complemento,
                    bairro,
                    cidade,
                    estado,
                    cep
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Conta criada com sucesso! Faça login para continuar."
            );

            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());

            preencherFormulario(
                    model, nome, email, telefone, rua, numero,
                    complemento, bairro, cidade, estado, cep);

            return "criar-conta";
        }
    }

    private void preencherFormulario(
            Model model,
            String nome,
            String email,
            String telefone,
            String rua,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep) {

        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        model.addAttribute("telefone", telefone);
        model.addAttribute("rua", rua);
        model.addAttribute("numero", numero);
        model.addAttribute("complemento", complemento);
        model.addAttribute("bairro", bairro);
        model.addAttribute("cidade", cidade);
        model.addAttribute("estado", estado);
        model.addAttribute("cep", cep);
    }
}
