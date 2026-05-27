package org.serratec.trabalho_final_api.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "nome", "descricao" })
public class CategoriaResponseDTO {

	private Long id;
	private String nome;
	private String descricao;

	// Constutor Vazio ↓
	public CategoriaResponseDTO() {
		super();
	}

	// Constutor Lotado ↓
	public CategoriaResponseDTO(Long id, String nome, String descricao) {
		super();
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
	}

	// Getters e Setters ↓
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}