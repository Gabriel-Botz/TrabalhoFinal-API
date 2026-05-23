package org.serratec.trabalho_final_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.ListaFavoritos;
import org.serratec.trabalho_final_api.dto.request.ListaFavoritosRequestDTO;
import org.serratec.trabalho_final_api.dto.response.ListaFavoritosResponseDTO;
import org.serratec.trabalho_final_api.exception.AcessoNegadoException;
import org.serratec.trabalho_final_api.exception.ErroResposta;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.ListaFavoritosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ListaFavoritosService {

    @Autowired
    private ListaFavoritosRepository listaFavoritosRepository;

    public List<ListaFavoritosResponseDTO> listar() {

        List<ListaFavoritos> listaFavoritos = listaFavoritosRepository.findAll();
        List<ListaFavoritosResponseDTO> listaFavoritosDTO = new ArrayList<>();

        for (ListaFavoritos lista : listaFavoritos) {
            listaFavoritosDTO.add(new ListaFavoritosResponseDTO(
                    lista.getId(),
                    lista.getNomeLista(),
                    lista.getPrivada(),
                    lista.getDataCriacao()));
        }

        return listaFavoritosDTO;

    }

    public ListaFavoritosResponseDTO buscarPorId(UUID id) {

        ListaFavoritos lista = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lista de favoritos não encontrada com ID: " + id));

        if (lista != null) {
            return new ListaFavoritosResponseDTO(
                    lista.getId(),
                    lista.getNomeLista(),
                    lista.getPrivada(),
                    lista.getDataCriacao());
        }

        return null;

    }

    @Transactional
    public ListaFavoritosResponseDTO criar(ListaFavoritosRequestDTO listaFavoritosRequestDTO) {

        ListaFavoritos lista = new ListaFavoritos();

        lista.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
        lista.setPrivada(listaFavoritosRequestDTO.getPrivada());
        lista.setDataCriacao(listaFavoritosRequestDTO.getDataCriacao());

        ListaFavoritos novaLista = listaFavoritosRepository.save(lista);

        return new ListaFavoritosResponseDTO(
                novaLista.getId(),
                novaLista.getNomeLista(),
                novaLista.getPrivada(),
                novaLista.getDataCriacao());

    }

    @Transactional
    public ListaFavoritosResponseDTO atualizar(UUID id, ListaFavoritosRequestDTO listaFavoritosRequestDTO,
            String usuario) { // <- Aqui

        ListaFavoritos listaExistente = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new ErroResposta.RecursoNaoEncontradoException(
                        "Lista de favoritos não encontrada com ID: " + id));

        if (listaExistente != null) {

            if (!listaExistente.getUsuario().getUsername().equals(usuario)) // <- Aqui
                throw new AcessoNegadoException("Você não tem permissão para alterar esta lista.");

            listaExistente.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
            listaExistente.setPrivada(listaFavoritosRequestDTO.getPrivada());
            listaExistente.setDataCriacao(listaFavoritosRequestDTO.getDataCriacao());

            ListaFavoritos listaAtualizada = listaFavoritosRepository.save(listaExistente);

            return new ListaFavoritosResponseDTO(
                    listaAtualizada.getId(),
                    listaAtualizada.getNomeLista(),
                    listaAtualizada.getPrivada(),
                    listaAtualizada.getDataCriacao());
        }

        return null;

    }

    @Transactional
    public boolean deletarListaFavoritos(UUID id) {

        if (listaFavoritosRepository.existsById(id)) {
            listaFavoritosRepository.deleteById(id);
            return true;
        }

        return false;

    }
}
