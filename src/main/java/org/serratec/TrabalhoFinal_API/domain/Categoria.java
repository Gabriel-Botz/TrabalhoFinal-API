package org.serratec.TrabalhoFinal_API.domain;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false, unique=true, length = 40)
	private String nome;
	
	@Column(nullable = true, length = 300)
	private String descricao;

	//Construtor Vazio ↓
	public Categoria() {
		super();
	}
	
	//Getters e Setters ↓
	public UUID getId() {return id;}
	public void setId(UUID id) {this.id = id;}

	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}

	public String getDescricao() {return descricao;}
	public void setDescricao(String descricao) {this.descricao = descricao;}
}
