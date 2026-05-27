import { apiClient } from '../api/client.js';
import { authService } from '../auth/auth.services.js';

export const listaService = {

    async listarPublicas() {
        return await apiClient.get('/lista-favoritos/publicas');
    },

    async listarPrivadas() {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.get(`/lista-favoritos/privadas/${user.username}`);
    },

    async criarLista(nomeLista, privada) {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.post(`/lista-favoritos/${user.username}`, { nomeLista, privada });
    },

    async deletarLista(idLista) {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.delete(`/lista-favoritos/${user.username}/${idLista}`);
    },

    async adicionarFilme(idLista, idFilme) {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.post(`/lista-favoritos/filmes/${user.username}?idLista=${idLista}&idFilme=${idFilme}`);
    },

    async adicionarSerie(idLista, idSerie) {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.post(`/lista-favoritos/series/${user.username}?idLista=${idLista}&idSerie=${idSerie}`);
    },

    async removerFilme(idLista, idFilme) {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.delete(`/lista-favoritos/filmes/${user.username}?idLista=${idLista}&idFilme=${idFilme}`);
    },

    async removerSerie(idLista, idSerie) {
        const user = authService.getCurrentUser();
        if (!user) throw new Error('Usuário não autenticado.');
        return await apiClient.delete(`/lista-favoritos/series/${user.username}?idLista=${idLista}&idSerie=${idSerie}`);
    }
};