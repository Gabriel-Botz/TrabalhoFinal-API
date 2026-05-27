package org.serratec.trabalho_final_api.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

import org.serratec.trabalho_final_api.enumerated.FormatoMidia;
import org.serratec.trabalho_final_api.enumerated.StatusPedido;

@Entity
@Table(name = "pedido_midia_fisica")
public class PedidoMidiaFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    
    @ManyToOne
    @JoinColumn(name = "filme_id", nullable = false)
    private Filme filme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FormatoMidia formato;

    @Column(nullable = false)
    private LocalDate dataPedido;

    @Column(nullable = false, length = 255)
    private String enderecoEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPedido status;


    //Construtor Vazio ↓
    public PedidoMidiaFisica() {
        super();
    }

    //Getters e Setters ↓
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Filme getFilme() {
        return filme;
    }
    public void setFilme(Filme filme) {
        this.filme = filme;
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