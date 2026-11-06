
package senai499.com.br.cafeferreira.controller;

import senai499.com.br.cafeferreira.model.Cliente;
import senai499.com.br.cafeferreira.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Listar todos os clientes
    @GetMapping
    public String listarClientes(Model model) {

        model.addAttribute("clientes",
                clienteService.listarTodos());

        return "admin/clientes";
    }

    // Pesquisar clientes pelo nome
    @GetMapping("/buscar")
    public String buscarClientes(
            @RequestParam("nome") String nome,
            Model model) {

        model.addAttribute("clientes",
                clienteService.pesquisarPorNome(nome));

        model.addAttribute("busca", nome);

        return "admin/clientes";
    }

    // Abrir formulário de edição
    @GetMapping("/editar/{id}")
    public String editarCliente(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("cliente",
                clienteService.buscarPorId(id));

        return "admin/editar-cliente";
    }

    // Salvar alterações
    @PostMapping("/atualizar/{id}")
    public String atualizarCliente(
            @PathVariable Long id,
            @ModelAttribute Cliente cliente) {

        clienteService.atualizar(id, cliente);

        return "redirect:/admin/clientes";
    }

    // Desativar cliente
    @PostMapping("/desativar/{id}")
    public String desativarCliente(
            @PathVariable Long id) {

        clienteService.desativar(id);

        return "redirect:/admin/clientes";
    }

    // Reativar cliente
    @PostMapping("/reativar/{id}")
    public String reativarCliente(
            @PathVariable Long id) {

        clienteService.reativar(id);

        return "redirect:/admin/clientes";
    }
}
