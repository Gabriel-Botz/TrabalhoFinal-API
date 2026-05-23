package org.serratec.trabalho_final_api.dto.request;

import jakarta.validation.constraints.*;

public class CategoriaRequestDTO {
	
	@NotBlank(message = "ERRO! O Campo nome não pode ficar vazio!")
	@Size(max=40, message = "O nome pode ter no máximo 40 caracteres!")
	private String nome;
	
	@Size(max=300, message="A descrição pode ter no máximo 300 caracteres!")
	private String descricao;

	//Construtor vazio
	public CategoriaRequestDTO() {
		super();
	}

	//Construtor Cheio
	public CategoriaRequestDTO(String nome, String descricao) {
		super();
		this.nome = nome;
		this.descricao = descricao;}

	//Getters e Setters
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}

	public String getDescricao() {return descricao;}
	public void setDescricao(String descricao) {this.descricao = descricao;}
}