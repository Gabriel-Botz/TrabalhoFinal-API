package org.serratec.trabalho_final_api.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.serratec.trabalho_final_api.domain.Filme;
import org.serratec.trabalho_final_api.domain.PedidoMidiaFisica;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.request.PedidoMidiaFisicaRequestDTO;
import org.serratec.trabalho_final_api.dto.response.PedidoMidiaFisicaResponseDTO;
import org.serratec.trabalho_final_api.enumerated.StatusPedido;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.FilmeRepository;
import org.serratec.trabalho_final_api.repository.PedidoMidiaFisicaRepository;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoMidiaFisicaService {

    @Autowired
    private PedidoMidiaFisicaRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FilmeRepository filmeRepository;
    

    // ---↓ Método GET listarTudo ↓↓
    public List<PedidoMidiaFisicaResponseDTO> listarTudoGET() {
        List<PedidoMidiaFisica> classPedidos = pedidoRepository.findAll();

        return classPedidos.stream().map(pedido -> new PedidoMidiaFisicaResponseDTO(
            pedido.getId(),
            pedido.getUsuario().getNome(),
            pedido.getFilme().getTitulo(),
            pedido.getFormato(),
            pedido.getDataPedido(),
            pedido.getEnderecoEntrega(),
            pedido.getStatus()
        )).collect(Collectors.toList());
    }
    //======================================================================


    // ---↓ Método GET por ID ↓↓
    public PedidoMidiaFisicaResponseDTO buscaID_GET(Long id) {
        String msg = "Pedido com ID '" + id + "' não foi encontrado";

        PedidoMidiaFisica classPedidos = pedidoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException(msg));

        return new PedidoMidiaFisicaResponseDTO(
            classPedidos.getId(),
            classPedidos.getUsuario().getNome(),
            classPedidos.getFilme().getTitulo(),
            classPedidos.getFormato(),
            classPedidos.getDataPedido(),
            classPedidos.getEnderecoEntrega(),
            classPedidos.getStatus()
        );
    }
    //======================================================================


    // ---↓ Método POST adicionar ↓↓
    public PedidoMidiaFisicaResponseDTO adicionarPOST(PedidoMidiaFisicaRequestDTO parametro) {
        String msgUsuarioObj = "Utilizador com ID '" + parametro.getUsuarioId() + "' não encontrado";
        String msgFilmeObj = "Filme com ID '" + parametro.getFilmeId() + "' não encontrado";

        
        Usuario usuario = usuarioRepository.findById(parametro.getUsuarioId()).orElseThrow(() -> new RecursoNaoEncontradoException(msgUsuarioObj));
        Filme filme = filmeRepository.findById(parametro.getFilmeId()).orElseThrow(() -> new RecursoNaoEncontradoException(msgFilmeObj));


        PedidoMidiaFisica classPedidos = new PedidoMidiaFisica();
        classPedidos.setUsuario(usuario);
        classPedidos.setFilme(filme);
        classPedidos.setFormato(parametro.getFormato());
        classPedidos.setEnderecoEntrega(parametro.getEnderecoEntrega());
        
  
        classPedidos.setDataPedido(LocalDate.now()); 
        classPedidos.setStatus(StatusPedido.PROCESSANDO); 


        classPedidos = pedidoRepository.save(classPedidos);

        
        return new PedidoMidiaFisicaResponseDTO(
            classPedidos.getId(),
            classPedidos.getUsuario().getNome(),
            classPedidos.getFilme().getTitulo(),
            classPedidos.getFormato(),
            classPedidos.getDataPedido(),
            classPedidos.getEnderecoEntrega(),
            classPedidos.getStatus()
        );
    }
    //======================================================================


    // ---↓ Método PUT atualizar ↓↓
    public PedidoMidiaFisicaResponseDTO atualizarPUT(Long id, PedidoMidiaFisicaRequestDTO parametro) {
        String msg = "Pedido com ID '" + id + "' não foi encontrado";

        PedidoMidiaFisica classPedido = pedidoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException(msg));

        // Permite atualizar apenas o endereço e o formato do disco
        classPedido.setEnderecoEntrega(parametro.getEnderecoEntrega());
        classPedido.setFormato(parametro.getFormato());

        classPedido = pedidoRepository.save(classPedido);

        return new PedidoMidiaFisicaResponseDTO(
            classPedido.getId(),
            classPedido.getUsuario().getNome(),
            classPedido.getFilme().getTitulo(),
            classPedido.getFormato(),
            classPedido.getDataPedido(),
            classPedido.getEnderecoEntrega(),
            classPedido.getStatus()
        );
    }
    //======================================================================


    // ---↓ Método DELETE apagar ↓↓
    public void apagarDELETE(Long id) {
        String msg = "Pedido com id '" + id + "' não foi encontrado";
        
        PedidoMidiaFisica pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(msg));

        pedidoRepository.delete(pedido);
    }
    //======================================================================
}
