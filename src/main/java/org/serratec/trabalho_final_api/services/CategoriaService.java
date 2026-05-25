package org.serratec.trabalho_final_api.services;

import java.util.List;
import java.util.stream.Collectors;

import org.serratec.trabalho_final_api.domain.Categoria;
import org.serratec.trabalho_final_api.dto.request.CategoriaRequestDTO;
import org.serratec.trabalho_final_api.dto.response.CategoriaResponseDTO;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository classRepositoryCategoria;

	// ---↓ Método GET listarTudo ↓↓
	public List<CategoriaResponseDTO> listarTudoGET() {
		List<Categoria> classCategoria = classRepositoryCategoria.findAll();

		return classCategoria.stream().map(categoria -> {

			CategoriaResponseDTO classResponse = new CategoriaResponseDTO();
			classResponse.setId(categoria.getId());
			classResponse.setNome(categoria.getNome());
			classResponse.setDescricao(categoria.getDescricao());
			return classResponse;

		}).collect(Collectors.toList());
	}
	// ======================================================================

	// ---↓ Método GET por ID ↓↓
	public CategoriaResponseDTO buscaID_GET(Long id) {

		// String msg = "Categoria com id '" + id + "' não foi encontrado";

		Categoria classCategoria = classRepositoryCategoria.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Categoria com id '" + id + "' não encontrada"));
		// Alterei o Exception e lancei a mensagem já nele

		CategoriaResponseDTO classResponse = new CategoriaResponseDTO();
		classResponse.setId(classCategoria.getId());
		classResponse.setNome(classCategoria.getNome());
		classResponse.setDescricao(classCategoria.getDescricao());
		return classResponse;
	}
	// ======================================================================

	// ---↓ Método POST adicionar ↓↓
	public CategoriaResponseDTO adicionarPOST(CategoriaRequestDTO parametro) {

		// ---↓ Transformar o DTO Request em uma Entidade Categoria ↓---
		Categoria classCategoria = new Categoria();
		classCategoria.setNome(parametro.getNome());
		classCategoria.setDescricao(parametro.getDescricao());

		// ---↓ A Class Repository salva a Entidade no Banco ↓---
		classCategoria = classRepositoryCategoria.save(classCategoria);

		// ---↓ Transformar a Entidade salva em um DTO Response ↓---
		CategoriaResponseDTO classResponse = new CategoriaResponseDTO();
		classResponse.setId(classCategoria.getId());
		classResponse.setNome(classCategoria.getNome());
		classResponse.setDescricao(classCategoria.getDescricao());

		// ---↓ E finalmente retorna o ResponseDTO ---↓
		return classResponse;
	}
	// ======================================================================

	// ---↓ Método PUT atualizar ↓↓
	public CategoriaResponseDTO atualizarPUT(Long id, CategoriaRequestDTO parametro) {

		String msg = "Categoria com id '" + id + "' não foi encontrado";

		Categoria classCategoria = classRepositoryCategoria.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, msg));

		classCategoria.setNome(parametro.getNome());
		classCategoria.setDescricao(parametro.getDescricao());

		classCategoria = classRepositoryCategoria.save(classCategoria);

		CategoriaResponseDTO classResponse = new CategoriaResponseDTO();
		classResponse.setId(classCategoria.getId());
		classResponse.setNome(classCategoria.getNome());
		classResponse.setDescricao(classCategoria.getDescricao());

		return classResponse;
	}
	// ======================================================================

	// ---↓ Método DELETE apagar ↓↓
	public void apagarDELETE(Long id) {

		String msg = "Categoria com id '" + id + "' não foi encontrado";

		Categoria classCategoria = classRepositoryCategoria.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, msg));

		classRepositoryCategoria.delete(classCategoria);
	}
}