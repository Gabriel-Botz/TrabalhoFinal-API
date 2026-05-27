import { apiClient } from '../api/client.js';

export const midiaService = {
    // --- FILMES ---
    async buscarFilmes(query) {
        return await apiClient.get(`/filmes/buscar?query=${encodeURIComponent(query)}`);
    },

    async buscarFilmePorId(id) {
        return await apiClient.get(`/filmes/${id}`);
    },

    async buscarFilmesPorCategoria(categoriaId) {
        return await apiClient.get(`/filmes/categoria/${categoriaId}`);
    },

    // --- SÉRIES ---
    async buscarSeries(query) {
        // Rota real do seu SeriesController: GET /series/buscar
        return await apiClient.get(`/series/buscar?query=${encodeURIComponent(query)}`);
    },

    async buscarSeriePorId(id) {
        return await apiClient.get(`/series/${id}`);
    },

    async buscarSeriesPorCategoria(categoriaId) {
        return await apiClient.get(`/series/categoria/${categoriaId}`);
    },

    // --- CATEGORIAS ---
    async listarCategorias() {
        return await apiClient.get('/categorias');
    },

    // --- TRAILER (BUSCA DIRETA TMDB) ---
    async buscarTrailer(tmdbId, tipo) {
        // Você precisará da sua chave de API aqui.
        // DICA: Se possível, não deixe a chave hardcoded, pegue de uma variável de ambiente.
        const API_KEY = "ae41581a9c0c6cabd2cc9bcf5961ba1b";
        const url = `https://api.themoviedb.org/3/${tipo}/${tmdbId}/videos?api_key=${API_KEY}&language=pt-BR`;

        try {
            const response = await fetch(url);
            const data = await response.json();
            // Filtra para pegar apenas o primeiro trailer disponível no YouTube
            const trailer = data.results.find(v => v.site === 'YouTube' && v.type === 'Trailer');
            return trailer ? trailer.key : null;
        } catch (err) {
            console.error("Erro ao buscar trailer no TMDB:", err);
            return null;
        }
    }
};

