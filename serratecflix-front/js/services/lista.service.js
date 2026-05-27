import { apiClient } from '../api/client.js';
import { authService } from '../auth/auth.services.js';

export const listaService = {
    /**
     * Retorna as listas públicas de todos os usuários
     */
    async listarPublicas() {
        return await apiClient.get('/lista-favoritos/publicas');
    },

    /**
     * Retorna as listas privadas do usuário logado atual
     */
    async listarPrivadas() {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.get(`/lista-favoritos/privadas/${user.username}`);
    },

    /**
     * Cria uma nova lista de favoritos associada ao usuário logado
     */
    async criarLista(nomeLista, privada) {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');

        const payload = { nomeLista, privada };
        return await apiClient.post(`/lista-favoritos/${user.username}`, payload);
    },

    /**
     * Deleta uma lista de favoritos pelo ID
     */
    async deletarLista(id) {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.delete(`/lista-favoritos/${user.username}/${id}`);
    }
};