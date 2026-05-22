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
                lista.getNomeLista(), 
                lista.getPrivada(), 
                lista.getDataCriacao()
            ));
        }

        return listaFavoritosDTO;

    }

    public ListaFavoritosResponseDTO buscarListaFavoritosPorId(UUID id) {

        ListaFavoritos listaFavoritos = listaFavoritosRepository.findById(id).orElse(null);

        if(listaFavoritos != null) {
            return new ListaFavoritosResponseDTO(
                listaFavoritos.getNomeLista(), 
                listaFavoritos.getPrivada(), 
                listaFavoritos.getDataCriacao()
            );
        }

        return null;

    }

    public ListaFavoritosResponseDTO criarListaFavoritos(ListaFavoritosRequestDTO listaFavoritosRequestDTO) {

        ListaFavoritos listafavoritos = new ListaFavoritos();

        listafavoritos.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
        listafavoritos.setPrivada(listaFavoritosRequestDTO.getPrivada());
        listafavoritos.setDataCriacao(listaFavoritosRequestDTO.getDataCriacao());

        ListaFavoritos novaListaFavoritos = listaFavoritosRepository.save(listafavoritos);

        return new ListaFavoritosResponseDTO(
            novaListaFavoritos.getNomeLista(), 
            novaListaFavoritos.getPrivada(), 
            novaListaFavoritos.getDataCriacao()
        );

    }

    public ListaFavoritosResponseDTO atualizarListaFavoritos(UUID id, ListaFavoritosRequestDTO listaFavoritosRequestDTO) {

        ListaFavoritos listaFavoritosExistente = listaFavoritosRepository.findById(id).orElse(null);

        if(listaFavoritosExistente != null) {
            listaFavoritosExistente.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
            listaFavoritosExistente.setPrivada(listaFavoritosRequestDTO.getPrivada());
            listaFavoritosExistente.setDataCriacao(listaFavoritosRequestDTO.getDataCriacao());

            ListaFavoritos listaAtualizada = listaFavoritosRepository.save(listaFavoritosExistente);

            return new ListaFavoritosResponseDTO(
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
