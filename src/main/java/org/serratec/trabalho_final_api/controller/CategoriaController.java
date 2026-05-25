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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Categoria", description = "Endpoints para gerenciamento de Categorias")
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService classService;

	// ---↓ Crud GET de listar tudo com Swagger
	@Operation(summary = "Lista todas as Categorias", description = "Retorna uma lista contendo todas as Categorias cadastradas no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
	@GetMapping
	public List<CategoriaResponseDTO> listarTudo() {
		return classService.listarTudoGET();
	}


	// ---↓ Crud GET por ID com Swagger
	@Operation(summary = "Buscar Categoria por ID", description = "Retorna os detalhes completos de uma Categoria específica com base no ID informado.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma Categoria encontrada com o ID informado")
        })
	@GetMapping("/{id}")
	public CategoriaResponseDTO buscarPorID(@PathVariable Long id) {
		return classService.buscaID_GET(id);
	}


	// ---↓ Crud POST que adiciona categoria com Swagger
	@Operation(summary = "Cadastrar nova Categoria", description = "Salva uma nova Categoria no banco de dados e retorna o recurso criado acompanhado da sua URI de localização.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou mal formatados")
        })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoriaResponseDTO adicionar(@Valid @RequestBody CategoriaRequestDTO parametro) {
		return classService.adicionarPOST(parametro);
	}


	// ---↓ Crud PUT que atualiza categoria por ID com Swagger
	@Operation(summary = "Altera todas as propriedades da Categoria", description = "Substitui completamente os dados de uma Categoria existente com base no ID informado.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Corpo da requisição possui dados inconsistentes"),
            @ApiResponse(responseCode = "404", description = "Categoria não localizada para atualização")
        })
	@PutMapping("/{id}")
	public CategoriaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO parametro) {

		return classService.atualizarPUT(id, parametro);
	}


	@Operation(summary = "Deleta todas as propriedades da Categoria", description = "Deleta completamente os dados de uma Categoria existente com base no ID informado.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria Deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não localizada para Deletar")
        })
	// ---↓ Crud DELETE que apaga categoria por ID com Swagger
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id) {
		classService.apagarDELETE(id);
	}
}