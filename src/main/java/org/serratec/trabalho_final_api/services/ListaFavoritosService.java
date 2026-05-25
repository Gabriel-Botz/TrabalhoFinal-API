package org.serratec.trabalho_final_api.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.ListaFavoritos;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.request.ListaFavoritosRequestDTO;
import org.serratec.trabalho_final_api.dto.response.ListaFavoritosResponseDTO;
import org.serratec.trabalho_final_api.exception.AcessoNegadoException;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.ListaFavoritosRepository;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ListaFavoritosService {

    @Autowired
    private ListaFavoritosRepository listaFavoritosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // @Autowired
    // private PermissaoService permissaoService;

    // Listar somente as listas públicas
    public List<ListaFavoritosResponseDTO> listarPublicas() {

        List<ListaFavoritos> listaFavoritos = listaFavoritosRepository.findAll();
        List<ListaFavoritosResponseDTO> listaFavoritosDTO = new ArrayList<>();

        for (ListaFavoritos lista : listaFavoritos) {

            // Adiciona somente listas públicas
            if (!lista.getPrivada()) {
                listaFavoritosDTO.add(new ListaFavoritosResponseDTO(
                        lista.getId(),
                        lista.getNomeLista(),
                        lista.getPrivada(),
                        lista.getDataCriacao(),
                        lista.getUsuario()));
            }
        }

        return listaFavoritosDTO;

    }

    // Listar somente as listas privadas do usuário logado
    public List<ListaFavoritosResponseDTO> listarPrivadas() {

        List<ListaFavoritos> listaFavoritos = listaFavoritosRepository.findAll();
        List<ListaFavoritosResponseDTO> listaFavoritosDTO = new ArrayList<>();

        for (ListaFavoritos lista : listaFavoritos) {

            if (!lista.getPrivada()) {

                // Verificar se o usuário logado é igual ao proprietario da lista
                // Se sim, mostrar somente listas onde o usuário logado é o proprietário da lista
                if (true) {

                    listaFavoritosDTO.add(new ListaFavoritosResponseDTO(
                            lista.getId(),
                            lista.getNomeLista(),
                            lista.getPrivada(),
                            lista.getDataCriacao(),
                            lista.getUsuario()));

                }
            }
        }

        return listaFavoritosDTO;

    }

    public ListaFavoritosResponseDTO buscarPorId(UUID id) {

        ListaFavoritos lista = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lista de favoritos não encontrada com ID: " + id));

        return new ListaFavoritosResponseDTO(
                lista.getId(),
                lista.getNomeLista(),
                lista.getPrivada(),
                lista.getDataCriacao(),
                lista.getUsuario());

        // Verificar se a lista é pública. Se sim, retorna a lista
        // Se não, verifica se o usuário logado é proprietário do id informado
        // Se sim, mostra a lista. Se não, lança excessão não encontrado.

        // if (true) {
        // return new ListaFavoritosResponseDTO(
        // lista.getId(),
        // lista.getNomeLista(),
        // lista.getPrivada(),
        // lista.getDataCriacao(),
        // lista.getUsuario());
        // } else {
        // throw new RecursoNaoEncontradoException(
        // "Lista não encontrada");
        // }

    }

    // Busca por nome, mas somente as listas públicas
    public List<ListaFavoritosResponseDTO> buscarPorNomePublicas(String nome) {

        if (nome.isBlank() || nome == null) {
            throw new RecursoNaoEncontradoException("Nenhuma lista contendo \"" + nome + "\"foi encontrada");
        }

        List<ListaFavoritos> listas = listaFavoritosRepository.findByNomeListaContainingIgnoreCase(nome);
        List<ListaFavoritosResponseDTO> listasDTO = new ArrayList<>();
        listas.forEach(lista -> {

            listasDTO.add(new ListaFavoritosResponseDTO(
                    lista.getId(),
                    lista.getNomeLista(),
                    lista.getPrivada(),
                    lista.getDataCriacao(),
                    lista.getUsuario()));

        });

        return listasDTO;
    }

    // Busca por nome, mas somente as listas privadas
    public List<ListaFavoritosResponseDTO> buscarPorNomePrivadas(String nome) {

        if (nome.isBlank() || nome == null) {
            throw new RecursoNaoEncontradoException("Nenhuma lista contendo \"" + nome + "\"foi encontrada");
        }

        List<ListaFavoritos> listas = listaFavoritosRepository.findByNomeListaContainingIgnoreCase(nome);
        List<ListaFavoritosResponseDTO> listasDTO = new ArrayList<>();
        listas.forEach(lista -> {

            listasDTO.add(new ListaFavoritosResponseDTO(
                    lista.getId(),
                    lista.getNomeLista(),
                    lista.getPrivada(),
                    lista.getDataCriacao(),
                    lista.getUsuario()));

        });

        return listasDTO;
    }

    @Transactional
    public ListaFavoritosResponseDTO criar(ListaFavoritosRequestDTO listaFavoritosRequestDTO) {

        // captura o username do usuario logado
        String usuario = SecurityContextHolder.getContext().getAuthentication().getName();

        // captura a entidade do usuario
        Usuario donoDaLista = usuarioRepository.findByUsername(usuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado."));

        ListaFavoritos lista = new ListaFavoritos();

        lista.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
        lista.setPrivada(listaFavoritosRequestDTO.getPrivada());
        lista.setDataCriacao(LocalDate.now());

        // vinculando a lista ao usuario
        lista.setUsuario(donoDaLista);

        ListaFavoritos novaLista = listaFavoritosRepository.save(lista);

        return new ListaFavoritosResponseDTO(
                novaLista.getId(),
                novaLista.getNomeLista(),
                novaLista.getPrivada(),
                novaLista.getDataCriacao(),
                novaLista.getUsuario());

    }

    @Transactional
    public ListaFavoritosResponseDTO atualizar(UUID id, ListaFavoritosRequestDTO listaFavoritosRequestDTO,
            String usuario) { // <- Aqui

        ListaFavoritos listaExistente = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lista de favoritos não encontrada com ID: " + id));

        if (!listaExistente.getUsuario().getUsername().equals(usuario)) // <- Aqui
            throw new AcessoNegadoException("Você não tem permissão para alterar esta lista.");

        listaExistente.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
        listaExistente.setPrivada(listaFavoritosRequestDTO.getPrivada());
        listaExistente.setDataCriacao(LocalDate.now());

        ListaFavoritos listaAtualizada = listaFavoritosRepository.save(listaExistente);

        return new ListaFavoritosResponseDTO(
                listaAtualizada.getId(),
                listaAtualizada.getNomeLista(),
                listaAtualizada.getPrivada(),
                listaAtualizada.getDataCriacao(),
                listaAtualizada.getUsuario());

    }

    @Transactional
    public boolean deletar(UUID id) {

        if (!listaFavoritosRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Lista com o id " + id + " não encontrada");
        } else {
            listaFavoritosRepository.deleteById(id);
            return true;
        }
    }
}
