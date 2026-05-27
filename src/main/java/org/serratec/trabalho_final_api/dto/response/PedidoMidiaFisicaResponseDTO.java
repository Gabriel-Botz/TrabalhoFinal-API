package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;

import org.serratec.trabalho_final_api.enumerated.FormatoMidia;
import org.serratec.trabalho_final_api.enumerated.StatusPedido;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "nomeUsuario", "tituloFilme", "formato", "dataPedido", "enderecoEntrega", "status" })
public class PedidoMidiaFisicaResponseDTO {

	private Long id;
	private String nomeUsuario;
	private String tituloFilme;
	private FormatoMidia formato;
	private LocalDate dataPedido;
	private String enderecoEntrega;
	private StatusPedido status;

	// Construtor Vazio ↓
	public PedidoMidiaFisicaResponseDTO() {
	}

	// Construtor LOTADO ↓
	public PedidoMidiaFisicaResponseDTO(Long id, String nomeUsuario, String tituloFilme, FormatoMidia formato,
			LocalDate dataPedido, String enderecoEntrega, StatusPedido status) {
		super();
		this.id = id;
		this.nomeUsuario = nomeUsuario;
		this.tituloFilme = tituloFilme;
		this.formato = formato;
		this.dataPedido = dataPedido;
		this.enderecoEntrega = enderecoEntrega;
		this.status = status;
	}

	// Getters e Setters ↓
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getTituloFilme() {
		return tituloFilme;
	}

	public void setTituloFilme(String tituloFilme) {
		this.tituloFilme = tituloFilme;
	}

	public FormatoMidia getFormato() {
		return formato;
	}

	public void setFormato(FormatoMidia formato) {
		this.formato = formato;
	}

	public LocalDate getDataPedido() {
		return dataPedido;
	}

	public void setDataPedido(LocalDate dataPedido) {
		this.dataPedido = dataPedido;
	}

	public String getEnderecoEntrega() {
		return enderecoEntrega;
	}

	public void setEnderecoEntrega(String enderecoEntrega) {
		this.enderecoEntrega = enderecoEntrega;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}
}