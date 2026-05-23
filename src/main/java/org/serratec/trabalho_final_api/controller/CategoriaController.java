package org.serratec.trabalho_final_api.controller;

import java.util.List;

import org.serratec.trabalho_final_api.dto.request.CategoriaRequestDTO;
import org.serratec.trabalho_final_api.dto.response.CategoriaResponseDTO;
import org.serratec.trabalho_final_api.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService classService;

	// ---↓ Crud GET de listar tudo
	@GetMapping
	public List<CategoriaResponseDTO> listarTudo() {
		return classService.listarTudoGET();
	}

	// ---↓ Crud GET por ID
	@GetMapping("/{id}")
	public CategoriaResponseDTO buscarPorID(@PathVariable Long id) {
		return classService.buscaID_GET(id);
	}

	// ---↓ Crud POST que adiciona categoria
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoriaResponseDTO adicionar(@Valid @RequestBody CategoriaRequestDTO parametro) {
		return classService.adicionarPOST(parametro);
	}

	// ---↓ Crud PUT que atualiza categoria por ID
	@PutMapping("/{id}")
	public CategoriaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO parametro) {

		return classService.atualizarPUT(id, parametro);
	}

	// ---↓ Crud DELETE que apaga categoria por ID
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id) {
		classService.apagarDELETE(id);
	}
}