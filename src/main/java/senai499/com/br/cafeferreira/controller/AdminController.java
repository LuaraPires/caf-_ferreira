
package senai499.com.br.cafeferreira.controller;

import senai499.com.br.cafeferreira.model.Usuario;
import senai499.com.br.cafeferreira.model.Cliente;
import senai499.com.br.cafeferreira.model.Produto;
import senai499.com.br.cafeferreira.model.Pedido;

import senai499.com.br.cafeferreira.model.PerfilUsuario;

import senai499.com.br.cafeferreira.repository.UsuarioRepository;
import senai499.com.br.cafeferreira.repository.ClienteRepository;
import senai499.com.br.cafeferreira.repository.ProdutoRepository;
import senai499.com.br.cafeferreira.repository.PedidoRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Controller
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            PedidoRepository pedidoRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // PAINEL ADMINISTRATIVO
    // ==========================================

    @GetMapping("/admin/painel")
    public String painelAdmin(Model model) {

        List<Usuario> funcionarios = usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getPerfil() == PerfilUsuario.FUNCIONARIO)
                .toList();

        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalProdutos", produtoRepository.count());
        model.addAttribute("totalPedidos", pedidoRepository.count());
        model.addAttribute("totalFuncionarios", funcionarios.size());

        return "admin/painel-admin";
    }

    // ==========================================
    // LISTAR FUNCIONÁRIOS
    // ==========================================

    @GetMapping("/admin/funcionarios")
    public String listarFuncionarios(Model model) {

        List<Usuario> funcionarios = usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getPerfil() == PerfilUsuario.FUNCIONARIO)
                .toList();

        model.addAttribute("funcionarios", funcionarios);

        return "admin/funcionarios";
    }

    // ==========================================
    // FORMULÁRIO DE CADASTRO
    // ==========================================

    @GetMapping("/admin/funcionarios/novo")
    public String formularioFuncionario(Model model) {

        model.addAttribute("usuario", new Usuario());

        return "admin/cadastrar-funcionario";
    }

    // ==========================================
    // CADASTRAR FUNCIONÁRIO
    // ==========================================

    @PostMapping("/admin/funcionarios/salvar")
    public String cadastrarFuncionario(
            @ModelAttribute("usuario") Usuario usuario,
            RedirectAttributes redirectAttributes) {

        try {

            if (usuario.getNome() == null || usuario.getNome().isBlank()
                    || usuario.getEmail() == null || usuario.getEmail().isBlank()
                    || usuario.getSenha() == null || usuario.getSenha().isBlank()) {

                throw new IllegalArgumentException(
                        "Preencha todos os campos obrigatórios."
                );
            }

            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new IllegalArgumentException(
                        "Este e-mail já está cadastrado."
                );
            }

            usuario.setNome(usuario.getNome().trim());
            usuario.setEmail(usuario.getEmail().trim());

            // O perfil é definido exclusivamente pelo backend
            usuario.setPerfil(PerfilUsuario.FUNCIONARIO);

            // Criptografa a senha
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

            // Conta ativa por padrão
            usuario.setAtivo(true);

            // Funcionário não recebe vínculo com cliente
            usuario.setCliente(null);

            usuarioRepository.save(usuario);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Funcionário cadastrado com sucesso!"
            );

            return "redirect:/admin/funcionarios";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("erro", e.getMessage());

            return "redirect:/admin/funcionarios/novo";
        }
    }

    // ==========================================
    // INATIVAR FUNCIONÁRIO
    // ==========================================

    @PostMapping("/admin/funcionarios/inativar/{id}")
    public String inativarFuncionario(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElse(null);

        if (usuario == null
                || usuario.getPerfil() != PerfilUsuario.FUNCIONARIO) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Funcionário não encontrado."
            );

            return "redirect:/admin/funcionarios";
        }

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Funcionário inativado com sucesso!"
        );

        return "redirect:/admin/funcionarios";
    }

    // ==========================================
    // ATIVAR FUNCIONÁRIO
    // ==========================================

    @PostMapping("/admin/funcionarios/ativar/{id}")
    public String ativarFuncionario(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElse(null);

        if (usuario == null
                || usuario.getPerfil() != PerfilUsuario.FUNCIONARIO) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Funcionário não encontrado."
            );

            return "redirect:/admin/funcionarios";
        }

        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Funcionário ativado com sucesso!"
        );

        return "redirect:/admin/funcionarios";
    }

    // ==========================================
    // LISTAR CLIENTES
    // ==========================================

    @GetMapping("/admin/clientes")
    public String listarClientes(Model model) {

        model.addAttribute("clientes", clienteRepository.findAll());

        return "admin/clientes";
    }

    // ==========================================
    // LISTAR PRODUTOS
    // ==========================================

    @GetMapping("/admin/produtos")
    public String listarProdutos(Model model) {

        model.addAttribute("produtos", produtoRepository.findAll());

        return "admin/produtos";
    }

    // ==========================================
    // LISTAR PEDIDOS
    // ==========================================

    @GetMapping("/admin/pedidos")
    public String listarPedidos(Model model) {

        model.addAttribute("pedidos", pedidoRepository.findAll());

        return "admin/pedidos";
    }
}
