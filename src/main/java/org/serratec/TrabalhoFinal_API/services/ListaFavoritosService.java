package org.serratec.TrabalhoFinal_API.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.ListaFavoritos;
import org.serratec.TrabalhoFinal_API.dto.request.ListaFavoritosRequestDTO;
import org.serratec.TrabalhoFinal_API.dto.response.ListaFavoritosResponseDTO;
import org.serratec.TrabalhoFinal_API.repository.ListaFavoritosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListaFavoritosService {

    @Autowired
    private ListaFavoritosRepository listaFavoritosRepository;

    public List<ListaFavoritosResponseDTO> listarListaFavoritos() {

        List<ListaFavoritos> listaFavoritos = listaFavoritosRepository.findAll();
        List<ListaFavoritosResponseDTO> listaFavoritosDTO = new ArrayList<>();

        for(ListaFavoritos lista : listaFavoritos) {
            listaFavoritosDTO.add(new ListaFavoritosResponseDTO(
                lista.getId(),
                lista.getNomeLista(), 
                lista.getPrivada(), 
                lista.getDataCriacao()
            ));
        }

        return listaFavoritosDTO;

    }

    public ListaFavoritosResponseDTO buscarListaFavoritosPorId(UUID id) {

        ListaFavoritos lista = listaFavoritosRepository.findById(id).orElse(null);

        if(lista != null) {
            return new ListaFavoritosResponseDTO(
                lista.getId(),
                lista.getNomeLista(), 
                lista.getPrivada(), 
                lista.getDataCriacao()
            );
        }

        return null;

    }

    public ListaFavoritosResponseDTO criarListaFavoritos(ListaFavoritosRequestDTO listaFavoritosRequestDTO) {

        ListaFavoritos lista = new ListaFavoritos();

        lista.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
        lista.setPrivada(listaFavoritosRequestDTO.getPrivada());
        lista.setDataCriacao(listaFavoritosRequestDTO.getDataCriacao());

        ListaFavoritos novaLista = listaFavoritosRepository.save(lista);

        return new ListaFavoritosResponseDTO(
            novaLista.getId(),
            novaLista.getNomeLista(), 
            novaLista.getPrivada(), 
            novaLista.getDataCriacao()
        );

    }

    public ListaFavoritosResponseDTO atualizarListaFavoritos(UUID id, ListaFavoritosRequestDTO listaFavoritosRequestDTO) {

        ListaFavoritos listaExistente = listaFavoritosRepository.findById(id).orElse(null);

        if(listaExistente != null) {
            listaExistente.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
            listaExistente.setPrivada(listaFavoritosRequestDTO.getPrivada());
            listaExistente.setDataCriacao(listaFavoritosRequestDTO.getDataCriacao());

            ListaFavoritos listaAtualizada = listaFavoritosRepository.save(listaExistente);

            return new ListaFavoritosResponseDTO(
                listaAtualizada.getId(),
                listaAtualizada.getNomeLista(), 
                listaAtualizada.getPrivada(), 
                listaAtualizada.getDataCriacao()
            );
        }

        return null;

    }

    public boolean deletarListaFavoritos(UUID id) {

        if(listaFavoritosRepository.existsById(id)) {
            listaFavoritosRepository.deleteById(id);
            return true;
        }

        return false;

    }
}
