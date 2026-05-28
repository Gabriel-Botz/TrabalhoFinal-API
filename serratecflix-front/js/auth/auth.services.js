import { apiClient } from '../api/client.js';

export const authService = {
    async login(username, senha) {
        const response = await fetch('http://localhost:8082/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, senha })
        });

        if (!response.ok) throw new Error('Credenciais inválidas');

        const token = response.headers.get('Authorization');
        if (!token) throw new Error('Token não recebido no header');

        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify({ username }));

        return { token, username };
    },

    async cadastrar(nome, email, username, senha, fotoUrl) {
        return apiClient.request('/usuarios', {
            method: 'POST',
            body: JSON.stringify({
                nome,
                email,
                username,
                senha,
                dataCriacao: '2025-01-01T00:00:00',
                tipoUsuario: 'USER',
                fotoPerfil: fotoUrl || null
            })
        });
    },

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.reload();
    },

    getCurrentUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    isAuthenticated() {
        return localStorage.getItem('token') !== null;
    }
};