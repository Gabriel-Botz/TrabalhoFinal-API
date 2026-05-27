package org.serratec.trabalho_final_api.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.serratec.trabalho_final_api.domain.AvaliacaoFilme;
import org.serratec.trabalho_final_api.domain.AvaliacaoSerie;
import org.serratec.trabalho_final_api.domain.Categoria;
import org.serratec.trabalho_final_api.domain.Filme;
import org.serratec.trabalho_final_api.domain.ListaFavoritos;
import org.serratec.trabalho_final_api.domain.Series;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.enumerated.ClassificacaoIndicativa;
import org.serratec.trabalho_final_api.enumerated.TipoUsuario;
import org.serratec.trabalho_final_api.repository.AvaliacaoFilmeRepository;
import org.serratec.trabalho_final_api.repository.AvaliacaoSerieRepository;
import org.serratec.trabalho_final_api.repository.CategoriaRepository;
import org.serratec.trabalho_final_api.repository.FilmeRepository;
import org.serratec.trabalho_final_api.repository.ListaFavoritosRepository;
import org.serratec.trabalho_final_api.repository.SeriesRepository;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private FilmeRepository filmeRepository;
    @Autowired
    private SeriesRepository seriesRepository;
    @Autowired
    private AvaliacaoFilmeRepository avaliacaoFilmeRepository;
    @Autowired
    private AvaliacaoSerieRepository avaliacaoSerieRepository;
    @Autowired
    private ListaFavoritosRepository listaFavoritosRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (usuarioRepository.count() > 0) {
            System.out.println("Banco de dados já possui dados. Ignorando seeding...");
            return;
        }

        System.out.println("Iniciando a inserção automática de dados para teste...");

        List<Categoria> categorias = new ArrayList<>();
        String[] nomesCategorias = { "Ação", "Comédia", "Drama", "Ficção Científica", "Terror", "Romance", "Animação",
                "Documentário", "Suspense", "Aventura" };
        for (int i = 0; i < 10; i++) {
            Categoria cat = new Categoria();
            cat.setNome(nomesCategorias[i]);
            cat.setDescricao("Filmes e séries de tirar o fôlego do gênero de " + nomesCategorias[i]);
            categorias.add(categoriaRepository.save(cat));
        }

        List<Usuario> usuarios = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Usuario user = new Usuario();
            user.setNome("Usuário " + i);
            user.setEmail("usuario" + i + "@email.com");
            user.setUsername("user" + i);
            // Exemplo de hash BCrypt para 'senha123'
            user.setSenha("$2a$10$7R7MvEOnBebhWw9Sg7mUvO8L7v79wKkF8NqH3K1t8N3g5L4k3r2sO");
            user.setTipoUsuario(i == 1 ? TipoUsuario.ADMIN : TipoUsuario.USER); // O primeiro é ADMIN, o resto USER
            user.setDataCriacao(LocalDateTime.now());
            usuarios.add(usuarioRepository.save(user));
        }

        List<Filme> filmes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Filme filme = new Filme();
            filme.setTmdbId(1000L + i);
            filme.setTitulo("Filme Blockbuster " + i);
            filme.setDescricao(
                    "Uma sinopse incrivelmente detalhada e emocionante sobre o incrível Filme de teste número " + i);
            filme.setDuracao(90 + (i * 10)); // Varia entre 100 e 190 min
            filme.setDataLancamento(LocalDate.now().minusYears(i));
            filme.setNotaMedia(7.0 + (i * 0.3) > 10.0 ? 9.5 : 7.0 + (i * 0.3));
            filme.setClassificacaoIndicativa(ClassificacaoIndicativa.LIVRE);

            filme.setCategorias(List.of(categorias.get(i % 10), categorias.get((i + i) % 10)));
            filmes.add(filmeRepository.save(filme));
        }

        List<Series> listaSeries = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Series serie = new Series();
            serie.setTitulo("Série Suprema " + i);
            serie.setDescricao("A história envolvente que se desenvolve ao longo de temporadas da série número " + i);
            serie.setTemporadas(i % 4 + i);
            serie.setEpisodios(serie.getTemporadas() * 10);
            serie.setDataLancamento(LocalDate.now().minusYears(i + 1));
            serie.setNotaMedia(6.5 + (i * 0.3));

            serie.setCategorias(List.of(categorias.get((i + 2) % 10), categorias.get((i + 3) % 10)));
            listaSeries.add(seriesRepository.save(serie));
        }

        for (int i = 0; i < 10; i++) {
            AvaliacaoFilme avFilme = new AvaliacaoFilme();
            avFilme.setNota(8.5);
            avFilme.setComentario("Simplesmente fantástico! O filme número " + (i + 1) + " entregou tudo.");
            avFilme.setDataAvaliacao(LocalDate.now());
            avFilme.setUsuario(usuarios.get(i));
            avFilme.setFilme(filmes.get(i));
            avaliacaoFilmeRepository.save(avFilme);
        }

        for (int i = 0; i < 10; i++) {
            AvaliacaoSerie avSerie = new AvaliacaoSerie();
            avSerie.setNota(9.0);
            avSerie.setComentario("Essa série marcou minha vida. Vale muito a pena assistir a " + (i + 1));
            avSerie.setDataAvaliacao(LocalDate.now());
            avSerie.setUsuario(usuarios.get(i));
            avSerie.setSeries(listaSeries.get(i));
            avaliacaoSerieRepository.save(avSerie);
        }

        for (int i = 0; i < 10; i++) {
            ListaFavoritos lista = new ListaFavoritos();
            lista.setNomeLista("Minhas produções favoritas vol " + (i + 1));
            lista.setPrivada(i % 2 == 0);
            lista.setDataCriacao(LocalDate.now().minusDays(i));
            lista.setUsuario(usuarios.get(i));
            lista.setFilmes(List.of(filmes.get(i), filmes.get((i + 1) % 10)));
            lista.setSeries(List.of(listaSeries.get(i), listaSeries.get((i + 1) % 10)));

            listaFavoritosRepository.save(lista);
        }

        System.out.println("Banco de dados populado com sucesso!");
    }

}
