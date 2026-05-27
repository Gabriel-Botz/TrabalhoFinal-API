import { apiClient } from '../api/client.js';

export const avaliacaoService = {
    /**
     * Retorna todas as avaliações de filmes cadastradas
     */
    async listarAvaliacoesFilmes() {
        return await apiClient.get('/avaliacoesFilmes');
    },

    /**
     * Envia uma nova avaliação de filme (Obedece o AvaliacaoFilmeRequestDTO)
     * Certifique-se de passar os IDs corretos no payload no passo da UI
     */
    async avaliarFilme(filmeId, nota, comentario) {
        const payload = {
            filmeId, // Deve bater com o esperado no seu AvaliacaoFilmeRequestDTO
            nota,
            comentario
        };
        return await apiClient.post('/avaliacoesFilmes', payload);
    },

    /**
     * Busca os top 5 filmes avaliados do sistema
     */
    async buscarTop5Filmes() {
        return await apiClient.get('/avaliacoesFilmes/top5');
    }
};