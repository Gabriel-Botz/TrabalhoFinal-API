const API_BASE_URL = 'http://localhost:8082';

export const apiClient = {
    // Método base unificado que gerencia os cabeçalhos e tokens
    async request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint}`;

        options.headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...options.headers
        };

        const token = localStorage.getItem('token');
        if (token) {
            options.headers['Authorization'] = token;
        }

        const response = await fetch(url, options);

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.error || `Erro na requisição: ${response.status}`);
        }

        if (response.status === 204) return null;

        return response.json();
    },

    // ---- ATALHOS MAPEADOS PARA OS SERVICES (Evita o erro de "...is not a function") ----
    async get(endpoint, options = {}) {
        return this.request(endpoint, { ...options, method: 'GET' });
    },

    async post(endpoint, body, options = {}) {
        return this.request(endpoint, { ...options, method: 'POST', body: JSON.stringify(body) });
    },

    async put(endpoint, body, options = {}) {
        return this.request(endpoint, { ...options, method: 'PUT', body: JSON.stringify(body) });
    },

    async delete(endpoint, options = {}) {
        return this.request(endpoint, { ...options, method: 'DELETE' });
    }
};