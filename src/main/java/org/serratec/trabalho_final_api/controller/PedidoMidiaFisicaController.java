package org.serratec.trabalho_final_api.controller;

import java.util.List;

import org.serratec.trabalho_final_api.dto.request.PedidoMidiaFisicaRequestDTO;
import org.serratec.trabalho_final_api.dto.response.PedidoMidiaFisicaResponseDTO;
import org.serratec.trabalho_final_api.services.PedidoMidiaFisicaService;
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

@Tag(name = "Pedidos Midia Fisica", description = "Endpoints para gerenciamento de Pedidos de Midia Fisica")
@RestController
@RequestMapping("/pedidosMidiaFisica")
public class PedidoMidiaFisicaController {
    
    @Autowired
    private PedidoMidiaFisicaService classService;


    // ---↓ Crud GET de listar tudo com Swagger
	@Operation(summary = "Lista todos os Pedidos de Midia Fisica", description = "Retorna a lista contendo todos os Pedidos de Midia Fisica cadastrados no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de Pedidos de Midia Fisica retornada com sucesso")
	@GetMapping
	public List<PedidoMidiaFisicaResponseDTO> listarTudo() {
		return classService.listarTudoGET();
	}


	// ---↓ Crud GET por ID com Swagger
	@Operation(summary = "Busca o Pedido de Midia Fisica por ID", description = "Retorna os detalhes completos de Pedido de Midia Fisica específico com base no ID informado.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido de Midia Fisica encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum Pedido de Midia Fisica encontrado com o ID informado")
        })
	@GetMapping("/{id}")
	public PedidoMidiaFisicaResponseDTO buscarPorID(@PathVariable Long id) {
		return classService.buscaID_GET(id);
	}


    // ---↓ Crud POST que adiciona categoria com Swagger
	@Operation(summary = "Cadastrar novo Pedido de Midia Fisica", description = "Salva um novo Pedido de Midia Fisica no banco de dados e retorna o recurso criado acompanhado da sua URI de localização.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido de Midia Fisica cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou mal formatados")
        })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoMidiaFisicaResponseDTO adicionar(@Valid @RequestBody PedidoMidiaFisicaRequestDTO parametro) {
		return classService.adicionarPOST(parametro);
	}
    

	// ---↓ Crud PUT que atualiza categoria por ID com Swagger
	@Operation(summary = "Altera todas as propriedades do Pedido de Midia Fisica", description = "Substitui completamente os dados de um Pedido de Midia Fisica existente com base no ID informado.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido de Midia Fisica atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Corpo da requisição possui dados inconsistentes"),
            @ApiResponse(responseCode = "404", description = "Pedido de Midia Fisica não localizada para atualização")
        })
	@PutMapping("/{id}")
	public PedidoMidiaFisicaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody PedidoMidiaFisicaRequestDTO parametro) {

		return classService.atualizarPUT(id, parametro);
	}
    
    
    // ---↓ Crud DELETE que apaga categoria por ID com Swagger
    @Operation(summary = "Deleta todas as propriedades do Pedido de Midia Fisica", description = "Deleta completamente os dados de um Pedido de Midia Fisica existente com base no ID informado.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido de Midia Fisica Deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido de Midia Fisica não localizada para Deletar")
        })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id) {
		classService.apagarDELETE(id);
	}
}