package org.serratec.TrabalhoFinal_API.dto.response;

import java.util.UUID;

public class CategoriaResponseDTO {

	private UUID id;
	private String nome;
	private String descricao;

	// Constutor Vazio ↓
	public CategoriaResponseDTO() {
		super();
	}

	// Constutor Lotado ↓
	public CategoriaResponseDTO(UUID id, String nome, String descricao) {
		super();
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
	}

	// Getters e Setters ↓
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
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