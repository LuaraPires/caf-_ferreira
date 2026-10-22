
package senai499.com.br.cafeferreira.controller;

import senai499.com.br.cafeferreira.service.FuncionarioService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FuncionarioService funcionarioService;

    public AdminController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping("/funcionarios")
    public String listarFuncionarios(Model model) {
        model.addAttribute(
                "funcionarios",
                funcionarioService.listarFuncionarios());

        return "admin/funcionarios";
    }

    @PostMapping("/funcionarios/cadastrar")
    public String cadastrarFuncionario(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam("confirmarSenha") String confirmarSenha,
            RedirectAttributes redirectAttributes) {

        if (!senha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "As senhas não coincidem.");

            return "redirect:/admin/funcionarios";
        }

        try {
            funcionarioService.cadastrarFuncionario(
                    nome, email, senha);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Funcionário cadastrado com sucesso.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", e.getMessage());
        }

        return "redirect:/admin/funcionarios";
    }

    @PostMapping("/funcionarios/{id}/inativar")
    public String inativarFuncionario(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            funcionarioService.inativarFuncionario(id);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Funcionário inativado com sucesso.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", e.getMessage());
        }

        return "redirect:/admin/funcionarios";
    }

    @PostMapping("/funcionarios/{id}/reativar")
    public String reativarFuncionario(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            funcionarioService.reativarFuncionario(id);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Funcionário reativado com sucesso.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", e.getMessage());
        }

        return "redirect:/admin/funcionarios";
    }
}
