package org.serratec.trabalho_final_api.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 40)
	private String nome;

	@Column(nullable = true, length = 300)
	private String descricao;

	@ManyToMany(mappedBy = "categorias")
	@JsonIgnore
	private List<Filme> filmes;

	@ManyToMany(mappedBy = "categorias")
	@JsonIgnore
	private List<Series> series;

	// Construtor Vazio ↓
	public Categoria() {
		super();
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

	public List<Filme> getFilmes() {
		return filmes;
	}

	public void setFilmes(List<Filme> filmes) {
		this.filmes = filmes;
	}

	public List<Series> getSeries() {
		return series;
	}

	public void setSeries(List<Series> series) {
		this.series = series;
	}

}
