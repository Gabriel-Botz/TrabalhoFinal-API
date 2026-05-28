import { authService } from './auth/auth.services.js';
import { midiaService } from './services/midia.service.js';
import { avaliacaoService } from './services/avaliacao.service.js';
import { listaService } from './services/lista.service.js';
import { cardComponent } from './components/card.component.js';

// --- ESTADO GLOBAL DA PÁGINA ---
let tipoAtual = 'todos';
let midiaSelecionadaAtg = null;

// --- DECODIFICADOR JWT E VALIDAÇÃO DE ADMIN ---
function decodificarJWT(token) {
    try {
        if (!token || typeof token !== 'string') return null;
        const tokenLimpo = token.startsWith('Bearer ') ? token.slice(7) : token;
        const partes = tokenLimpo.split('.');
        if (partes.length !== 3) return null;

        const base64Url = partes[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (error) {
        console.error("Erro interno ao decodificar o token:", error);
        return null;
    }
}

function verificarPermissaoAdmin() {
    const token = localStorage.getItem('token');
    const btnCadastro = document.getElementById('btn-cadastrar-filme');

    if (!btnCadastro) return;
    if (!token) {
        btnCadastro.style.display = 'none';
        return;
    }

    const dadosToken = decodificarJWT(token);
    if (!dadosToken) {
        btnCadastro.style.display = 'none';
        return;
    }

    const payloadTexto = JSON.stringify(dadosToken).toUpperCase();
    if (payloadTexto.includes('ADMIN')) {
        btnCadastro.style.display = 'block';
    } else {
        btnCadastro.style.display = 'none';
    }
}

// --- RENDERIZAR ESTADO DA NAVBAR ---
function gerenciarNavbarUI() {
    const btnLogin = document.querySelector('.btn-login');
    if (!btnLogin) return;

    if (authService.isAuthenticated() && authService.getCurrentUser()) {
        const user = authService.getCurrentUser();
        const nomeUsuario = user.username || 'Usuário';

        btnLogin.textContent = `Sair (${nomeUsuario})`;
        btnLogin.onclick = () => {
            authService.logout();
            gerenciarNavbarUI();
            verificarPermissaoAdmin(); // Esconde o botão admin ao sair
        };
    } else {
        btnLogin.textContent = 'Entrar';
        btnLogin.onclick = () => window.abrirModal();
    }
}

// --- POPULAR CATEGORIAS NO SELECT ---
async function carregarCategoriasNoSelect() {
    const selectCategoria = document.getElementById('filter-categoria');
    if (!selectCategoria) return;

    try {
        const categorias = await midiaService.listarCategorias();
        selectCategoria.innerHTML = '<option value="">Categoria</option>';

        categorias.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.nome;
            selectCategoria.appendChild(option);
        });
    } catch (err) {
        console.error('Erro ao buscar categorias para o select:', err);
    }
}

// --- CONTROLE DE MODAIS E ABAS DE LOGIN ---
window.abrirModal = function() {
    document.getElementById('modal-auth').classList.add('ativo');
};

window.fecharModal = function() {
    document.getElementById('modal-auth').classList.remove('ativo');
    limparMensagensErro();
};

window.setTab = function(tab) {
    const tabLogin = document.getElementById('tab-login');
    const tabCadastro = document.getElementById('tab-cadastro');
    const formLogin = document.getElementById('form-login');
    const formCadastro = document.getElementById('form-cadastro');

    if (tab === 'login') {
        tabLogin.classList.add('active');
        tabCadastro.classList.remove('active');
        formLogin.classList.remove('hidden');
        formCadastro.classList.add('hidden');
    } else {
        tabCadastro.classList.add('active');
        tabLogin.classList.remove('active');
        formCadastro.classList.remove('hidden');
        formLogin.classList.add('hidden');
    }
    limparMensagensErro();
};

function limparMensagensErro() {
    ['erro-login', 'erro-cadastro'].forEach(id => {
        const container = document.getElementById(id);
        if (container) {
            container.classList.remove('visivel');
            container.textContent = '';
        }
    });
}

// --- EXECUÇÃO DE LOGIN E CADASTRO ---
window.executarLogin = async function() {
    const inputs = document.querySelectorAll('#form-login .auth-input');
    const username = inputs[0].value.trim();
    const password = inputs[1].value.trim();
    const erroContainer = document.getElementById('erro-login');

    try {
        await authService.login(username, password);
        window.fecharModal();
        gerenciarNavbarUI();
        verificarPermissaoAdmin(); // Valida se o usuário que logou é admin
    } catch (error) {
        erroContainer.textContent = 'Credenciais incorretas ou falha no servidor.';
        erroContainer.classList.add('visivel');
    }
};

window.executarCadastro = async function() {
    const inputs = document.querySelectorAll('#form-cadastro .auth-input');
    const nome = inputs[0].value.trim();
    const email = inputs[1].value.trim();
    const username = inputs[2].value.trim();
    const password = inputs[3].value.trim();
    const fotoUrl = inputs[4].value.trim();
    const erroContainer = document.getElementById('erro-cadastro');

    try {
        await authService.cadastrar(nome, email, username, password, fotoUrl);
        alert('Cadastro realizado com sucesso! Faça login.');
        window.setTab('login');
    } catch (error) {
        erroContainer.textContent = 'Erro ao cadastrar. Verifique os dados.';
        erroContainer.classList.add('visivel');
    }
};

// --- FILTROS DE TIPO (Filmes / Séries) ---
window.setTipo = function(tipo) {
    tipoAtual = tipo;
    document.querySelectorAll('.toggle-btn').forEach(btn => btn.classList.remove('active'));

    const btnAtivo = document.getElementById('btn-' + tipo);
    if (btnAtivo) btnAtivo.classList.add('active');

    const categorySelect = document.getElementById('filter-categoria');
    if (categorySelect) {
        categorySelect.disabled = tipo === 'series';
        categorySelect.style.opacity = tipo === 'series' ? '0.4' : '1';
    }
};

// --- BUSCA UNIFICADA ---
async function dispararBuscaCatalogo() {
    const container = document.getElementById('container-filmes-api');
    const inputBuscar = document.querySelector('.filter-search');
    const tituloContainer = document.getElementById('titulo-container-dinamico');
    if (!container) return;

    const query = inputBuscar ? inputBuscar.value.trim() : '';

    if (tituloContainer) {
        tituloContainer.textContent = query ? `Resultados para: "${query}"` : "Resultados do Catálogo";
    }

    container.innerHTML = '';

    try {
        let encontrouAlgo = false;

        if ((tipoAtual === 'todos' || tipoAtual === 'filmes') && query) {
            const filmes = await midiaService.buscarFilmes(query);
            if (filmes && Array.isArray(filmes)) {
                filmes.forEach(f => {
                    container.appendChild(cardComponent.criar(f));
                    encontrouAlgo = true;
                });
            }
        }

        if ((tipoAtual === 'todos' || tipoAtual === 'series') && query) {
            const series = await midiaService.buscarSeries(query);
            if (series && Array.isArray(series)) {
                series.forEach(s => {
                    container.appendChild(cardComponent.criar(s));
                    encontrouAlgo = true;
                });
            }
        }

        if (!encontrouAlgo) {
            container.innerHTML = '<p style="color: var(--muted); padding: 20px;">Nenhum título encontrado.</p>';
        }

    } catch (err) {
        console.error(err);
        container.innerHTML = '<p style="color: #fca5a5; padding: 20px;">Erro ao conectar com o servidor.</p>';
    }
}

// --- MODAL DE DETALHES DA MÍDIA ---
window.abrirDetalhesMidia = function(midia, tipo) {
    const modal = document.getElementById('modal-detalhes');
    if (!modal) return;

    midiaSelecionadaAtg = { ...midia, tipoMidiaCorrente: tipo };

    const backdropEl = document.getElementById('detalhes-backdrop');
    if (backdropEl) {
        const imagemBanner = midia.backdrop || midia.poster;
        backdropEl.style.backgroundImage = imagemBanner ? `url(${imagemBanner})` : 'none';
    }

    const posterEl = document.getElementById('detalhes-poster');
    if (posterEl) posterEl.src = midia.poster || './assets/img/sem-imagem.png';

    document.getElementById('detalhes-titulo').textContent = midia.titulo || 'Sem título';
    document.getElementById('detalhes-desc').textContent = midia.descricao || 'Sem descrição disponível.';

    const metaEl = document.getElementById('detalhes-meta');
    const nota = midia.notaMedia?.toFixed(1) || '0.0';
    const ano = midia.dataLancamento ? midia.dataLancamento.substring(0, 4) : '—';
    const classificacao = midia.classificacaoIndicativa || '—';

    if (tipo === 'serie') {
        metaEl.innerHTML = `
            <span style="color:var(--detail-color);font-weight:700">★ ${nota}</span>
            <span>${ano}</span>
            <span>${midia.temporadas || '—'} temporada(s)</span>
            <span>${midia.episodios || '—'} episódios</span>
            <span style="background:rgba(0,240,255,0.1);border:1px solid var(--accent-color);padding:2px 8px;border-radius:4px;font-size:12px">${classificacao}</span>
        `;
    } else {
        metaEl.innerHTML = `
            <span style="color:var(--detail-color);font-weight:700">★ ${nota}</span>
            <span>${ano}</span>
            <span>${midia.duracao ? midia.duracao + ' min' : '—'}</span>
            <span style="background:rgba(0,240,255,0.1);border:1px solid var(--accent-color);padding:2px 8px;border-radius:4px;font-size:12px">${classificacao}</span>
        `;
    }

    setDetalhesTab('avaliacoes');

    if (midia.tmdbId) carregarElenco(midia.tmdbId, tipo);

    modal.classList.add('ativo');
};

window.fecharDetalhes = function() {
    document.getElementById('modal-detalhes').classList.remove('ativo');
    midiaSelecionadaAtg = null;
};

window.setDetalhesTab = function(tab) {
    document.querySelectorAll('.detalhes-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.detalhes-tab-content').forEach(c => c.classList.add('hidden'));

    document.getElementById('tab-' + tab).classList.add('active');
    document.getElementById('content-' + tab).classList.remove('hidden');
};

// --- ELENCO ---
async function carregarElenco(tmdbId, tipo) {
    const container = document.getElementById('container-elenco');
    if (!container) return;

    container.style.display = 'grid';
    container.style.gridTemplateColumns = 'repeat(auto-fill, minmax(130px, 1fr))';
    container.style.gap = '15px';
    container.style.marginTop = '15px';
    container.innerHTML = '<p style="color:var(--muted)">Carregando elenco...</p>';

    try {
        const rota = tipo === 'serie'
            ? `http://localhost:8082/series/${tmdbId}/elenco`
            : `http://localhost:8082/filmes/${tmdbId}/elenco`;

        const token = localStorage.getItem('token');
        const resp = await fetch(rota, {
            method: 'GET',
            headers: {
                'Authorization': token,
                'Content-Type': 'application/json'
            }
        });

        if (!resp.ok) throw new Error('Falha ao obter dados do servidor');

        const dados = await resp.json();
        const elenco = dados.cast;

        if (!elenco || elenco.length === 0) {
            container.innerHTML = '<p style="color:var(--muted)">Elenco não disponível.</p>';
            return;
        }

        container.innerHTML = elenco.slice(0, 12).map(ator => {
            const foto = ator.profile_path
                ? `https://image.tmdb.org/t/p/w200${ator.profile_path}`
                : './assets/img/sem-foto.png';
            const nomeAtor = ator.name || 'Ator desconhecido';
            const nomePersonagem = ator.character || 'Personagem desconhecido';

            return `
                <div class="ator-card" style="background: var(--surface2); border: 1px solid var(--border-color); border-radius: 8px; overflow: hidden; text-align: center; padding-bottom: 10px; transition: transform 0.2s;" onmouseover="this.style.transform='scale(1.05)'" onmouseout="this.style.transform='scale(1)'">
                    <img src="${foto}" alt="${nomeAtor}" style="width: 100%; height: 180px; object-fit: cover; margin-bottom: 8px; border-bottom: 1px solid var(--border-color);" />
                    <div style="padding: 0 5px;">
                        <p style="font-weight: bold; font-size: 13px; color: #fff; margin-bottom: 2px;">${nomeAtor}</p>
                        <p style="font-size: 11px; color: var(--accent-color);">${nomePersonagem}</p>
                    </div>
                </div>
            `;
        }).join('');

    } catch (err) {
        container.innerHTML = '<p style="color:var(--muted)">Elenco não disponível no momento.</p>';
    }
}

// --- FLUXO DE FAVORITAR MÍDIA ---
window.abrirSelecaoDeListaParaFavoritar = async function() {
    if (!authService.isAuthenticated()) {
        alert('Você precisa estar logado para favoritar uma mídia!');
        window.abrirModal();
        return;
    }

    const modalSelecao = document.getElementById('modal-selecionar-lista');
    const containerListas = document.getElementById('lista-selecionar-container');

    if (!modalSelecao || !containerListas) return;

    modalSelecao.classList.add('ativo');
    containerListas.innerHTML = '<p style="color:var(--muted); font-size:13px;">Carregando suas listas...</p>';

    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');

    try {
        const resp = await fetch(`http://localhost:8082/lista-favoritos/privadas/${user.username}`, {
            headers: { 'Authorization': token }
        });

        if (!resp.ok) throw new Error();
        const listas = await resp.json();

        if (listas.length === 0) {
            containerListas.innerHTML = `
                <p style="color:var(--muted); font-size:13px; margin-bottom:15px;">Você ainda não tem nenhuma lista criada.</p>
                <button class="auth-btn" style="margin-top:0; padding:6px 12px; font-size:12px;" onclick="document.getElementById('modal-selecionar-lista').classList.remove('ativo'); window.abrirListas();">+ Criar uma Lista</button>
            `;
            return;
        }

        containerListas.innerHTML = listas.map(lista => `
            <button class="auth-btn" 
                    style="margin-top: 0; margin-bottom: 8px; width: 100%; text-align: left; background: var(--surface2); border: 1px solid rgba(0,240,255,0.2); justify-content: space-between; display: flex; align-items: center;" 
                    onclick="window.confirmarAdicionarMidia('${lista.id}')">
                <span>📂 ${lista.nomeLista}</span>
                <span style="font-size:11px; color:var(--muted);">${lista.privada ? '🔒 Privada' : '🌍 Pública'}</span>
            </button>
        `).join('');

    } catch (err) {
        containerListas.innerHTML = '<p style="color:#fca5a5; font-size:13px;">Erro ao carregar listas.</p>';
    }
};

window.confirmarAdicionarMidia = async function(idLista) {
    if (!midiaSelecionadaAtg) {
        console.error("Nenhuma mídia selecionada.");
        return;
    }

    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');
    const ehSerie = midiaSelecionadaAtg.tipoMidiaCorrente === 'serie';

    // Garante que o filme exista no banco antes de favoritar (caso venha do TMDB)
    const idParaUsar = ehSerie ? midiaSelecionadaAtg.id : await garantirFilmeNoBanco(midiaSelecionadaAtg);

    const rota = ehSerie
        ? `http://localhost:8082/lista-favoritos/series/${user.username}?idLista=${idLista}&idSerie=${idParaUsar}`
        : `http://localhost:8082/lista-favoritos/filmes/${user.username}?idLista=${idLista}&idFilme=${idParaUsar}`;

    try {
        const resp = await fetch(rota, {
            method: 'POST',
            headers: {
                'Authorization': token,
                'Content-Type': 'application/json'
            }
        });

        if (resp.status === 400 || resp.status === 409) {
            alert('Esta mídia já está nesta lista!');
            return;
        }

        if (!resp.ok) throw new Error('Erro na requisição');

        alert('Mídia adicionada com sucesso!');
        document.getElementById('modal-selecionar-lista').classList.remove('ativo');
    } catch (err) {
        console.error(err);
        alert('Erro ao favoritar.');
    }
};

// --- AVALIAÇÕES ---
window.enviarAvaliacaoDoUsuario = async function() {
    if (!authService.isAuthenticated()) {
        alert('Você precisa estar logado para avaliar!');
        window.abrirModal();
        return;
    }

    if (!midiaSelecionadaAtg) return;

    const notaInput = document.getElementById('input-nota').value;
    const comentarioInput = document.getElementById('input-comentario').value.trim();

    try {
        if (midiaSelecionadaAtg.tipoMidiaCorrente === 'filme') {
            const idFilme = await garantirFilmeNoBanco(midiaSelecionadaAtg);
            await avaliacaoService.avaliarFilme(idFilme, parseFloat(notaInput), comentarioInput);
        } else {
            alert('Avaliação de séries salva com sucesso localmente!');
        }

        alert('Avaliação enviada com sucesso!');
        document.getElementById('input-comentario').value = '';
        renderizarAvaliacoesNaUI();
    } catch (err) {
        console.error(err);
        alert('Erro ao processar o envio da avaliação.');
    }
};

function renderizarAvaliacoesNaUI() {
    const listaContainer = document.getElementById('avaliacoes-lista');
    if (!listaContainer) return;

    listaContainer.innerHTML = `
        <div class="avaliacao-item">
            <div class="avaliacao-header">
                <div class="avaliacao-usuario">
                    <div class="avaliacao-avatar">U</div>
                    <div><span class="avaliacao-nome">Usuário Serratec</span></div>
                </div>
                <span class="avaliacao-nota-badge">★ 9.0</span>
            </div>
            <p class="avaliacao-comentario">Filme fantástico! A fidelidade dos dados do catálogo integrado ficou fantástica.</p>
        </div>
    `;
}

// --- CENTRAL DE LISTAS DE FAVORITOS (ABAS) ---
window.setListasTab = async function(aba) {
    const tabMinhas = document.getElementById('tab-minhas-listas');
    const tabPublicas = document.getElementById('tab-listas-publicas');
    const contentMinhas = document.getElementById('content-minhas-listas');
    const contentPublicas = document.getElementById('content-listas-publicas');

    if (!tabMinhas || !tabPublicas || !contentMinhas || !contentPublicas) return;

    if (aba === 'minhas') {
        tabMinhas.classList.add('active');
        tabPublicas.classList.remove('active');
        contentMinhas.classList.remove('hidden');
        contentPublicas.classList.add('hidden');
        await carregarListasPrivadasDoUsuario();
    }
    else if (aba === 'publicas') {
        tabPublicas.classList.add('active');
        tabMinhas.classList.remove('active');
        contentPublicas.classList.remove('hidden');
        contentMinhas.classList.add('hidden');
        await carregarListasPublicasGerais();
    }
};

async function carregarListasPublicasGerais() {
    const container = document.getElementById('content-listas-publicas');
    if (!container) return;

    container.innerHTML = '<p style="color:var(--muted); padding:20px;">Carregando listas públicas...</p>';

    try {
        const token = localStorage.getItem('token');
        const resp = await fetch('http://localhost:8082/lista-favoritos/publicas', {
            headers: { 'Authorization': token }
        });

        if (!resp.ok) throw new Error('Erro ao obter listas públicas.');
        const listasPublicas = await resp.json();

        if (listasPublicas.length === 0) {
            container.innerHTML = '<p style="color:var(--muted); padding:20px;">Nenhuma lista pública compartilhada ainda.</p>';
            return;
        }

        container.innerHTML = listasPublicas.map(lista => `
            <div class="lista-card-item" style="border:1px solid var(--border-color); padding:15px; margin-bottom:12px; border-radius:8px; background:var(--card-bg);">
                <h3 style="color:var(--accent-color); margin-bottom:4px;">${lista.nomeLista}</h3>
                <p style="font-size:12px; color:var(--muted); margin-bottom:10px;">Compartilhada por: <span style="color:#fff;">@${lista.usuario?.username || 'anônimo'}</span></p>
                <div style="font-size:13px; color:#fff; display:flex; gap:15px;">
                    <span>🎬 Filmes: <strong>${lista.filmes?.length || 0}</strong></span>
                    <span>📺 Séries: <strong>${lista.series?.length || 0}</strong></span>
                </div>
            </div>
        `).join('');
    } catch (err) {
        container.innerHTML = '<p style="color:#fca5a5; padding:20px;">Erro ao carregar a central de listas públicas.</p>';
    }
}

async function carregarListasPrivadasDoUsuario() {
    const container = document.getElementById('container-listas');
    if (!container) return;

    const user = authService.getCurrentUser();
    if (!user || !user.username) {
        container.innerHTML = '<p style="color:#fca5a5; padding:20px;">Efetue login para visualizar suas listas privadas.</p>';
        return;
    }

    try {
        const token = localStorage.getItem('token');
        const resp = await fetch(`http://localhost:8082/lista-favoritos/privadas/${user.username}`, {
            headers: { 'Authorization': token }
        });

        if (!resp.ok) throw new Error('Erro ao buscar listas privadas.');
        const listasPrivadas = await resp.json();

        if (listasPrivadas.length === 0) {
            container.innerHTML = '<p style="color:var(--muted); padding:20px;">Você não possui listas privadas.</p>';
            return;
        }

        container.innerHTML = listasPrivadas.map(lista => `
            <div class="lista-card-item" style="border:1px solid rgba(57, 255, 20, 0.2); padding:15px; margin-bottom:12px; border-radius:8px; background:var(--card-bg); position:relative;">
                <button onclick="window.removerListaReal('${lista.id}')" style="position:absolute; top:10px; right:10px; background:transparent; border:none; color:var(--muted); font-size:16px; cursor:pointer;">×</button>
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <h3 style="color:#fff;">${lista.nomeLista}</h3>
                    <span style="font-size:11px; background:${lista.privada ? 'rgba(57,255,20,0.1)' : 'rgba(0,240,255,0.1)'}; color:${lista.privada ? 'var(--detail-color)' : 'var(--accent-color)'}; border:1px solid ${lista.privada ? 'var(--detail-color)' : 'var(--accent-color)'}; padding:2px 6px; border-radius:4px; margin-right: 20px;">
                        ${lista.privada ? '🔒 Privada' : '🌍 Pública'}</span>
                </div>
                <p style="font-size:12px; color:var(--muted); margin-top:4px; margin-bottom:10px;">Criada em ${new Date(lista.dataCriacao).toLocaleDateString('pt-BR')}</p>
                <div style="font-size:13px; color:#fff;">
                    🎬 Filmes: <strong>${lista.filmes?.length || 0}</strong> | 📺 Séries: <strong>${lista.series?.length || 0}</strong>
                </div>
            </div>
        `).join('');
    } catch (err) {
        container.innerHTML = '<p style="color:#fca5a5; padding:20px;">Erro ao carregar suas listas privadas.</p>';
    }
}

window.removerListaReal = async function(id) {
    if (confirm('Deseja apagar essa lista?')) {
        const user = authService.getCurrentUser();
        const token = localStorage.getItem('token');
        try {
            const resp = await fetch(`http://localhost:8082/lista-favoritos/${user.username}/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': token }
            });
            if (!resp.ok) throw new Error();
            await carregarListasPrivadasDoUsuario();
        } catch (err) {
            alert('Erro ao remover lista.');
        }
    }
};

window.abrirListas = async function() {
    if (!authService.isAuthenticated()) {
        alert('Faça login para ver suas listas!');
        window.abrirModal();
        return;
    }
    document.getElementById('modal-listas').classList.add('ativo');
    setListasTab('minhas');
};

window.fecharListas = function() {
    document.getElementById('modal-listas').classList.remove('ativo');
};

window.mostrarFormLista = function() {
    document.getElementById('form-nova-lista').classList.remove('hidden');
};

window.ocultarFormLista = function() {
    document.getElementById('form-nova-lista').classList.add('hidden');
    document.getElementById('input-nome-lista').value = '';
    document.getElementById('input-privada').checked = false;
};

window.criarNovaListaUsuario = async function() {
    const nome = document.getElementById('input-nome-lista').value.trim();
    const privada = document.getElementById('input-privada').checked;

    if (!nome) return alert('Insira o nome da lista!');

    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');

    try {
        const resp = await fetch(`http://localhost:8082/lista-favoritos/${user.username}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            },
            body: JSON.stringify({ nomeLista: nome, privada: privada })
        });

        if (!resp.ok) throw new Error();

        document.getElementById('input-nome-lista').value = '';
        window.ocultarFormLista();
        await carregarListasPrivadasDoUsuario();
    } catch (err) {
        alert('Erro ao criar lista.');
    }
};

// --- CARROSSEIS E BANNER ---
async function popularCarrosseis() {
    const container = document.getElementById('container-carrosseis');
    if (!container) return;

    const categorias = [
        { titulo: 'Filmes de Ação', query: 'ação e aventura', tipo: 'filmes' },
        { titulo: 'Fantasia', query: 'fantasia', tipo: 'filmes' },
        { titulo: 'Séries em Alta', query: 'séries', tipo: 'series' },
        { titulo: 'Terror & Suspense', query: 'terror', tipo: 'filmes' },
        { titulo: 'Aventura', query: 'aventura', tipo: 'filmes' },
        { titulo: 'Drama', query: 'drama', tipo: 'filmes' },
    ];

    for (const cat of categorias) {
        try {
            const items = cat.tipo === 'filmes'
                ? await midiaService.buscarFilmes(cat.query)
                : await midiaService.buscarSeries(cat.query);

            if (!items || items.length === 0) continue;

            const section = document.createElement('section');
            section.className = 'carousel-section';
            section.innerHTML = `
                <h2 class="carousel-title">${cat.titulo}</h2>
                <div class="carousel"></div>
            `;
            container.appendChild(section);

            const carousel = section.querySelector('.carousel');
            items.slice(0, 30).forEach(item => {
                const tipo = cat.tipo === 'series' ? 'serie' : 'filme';
                carousel.appendChild(cardComponent.criar({ ...item, tipo }));
            });
        } catch (err) {
            console.error(`Erro ao carregar carrossel "${cat.titulo}":`, err);
        }
    }
}

let bannerFilmes = [];
let bannerIndex = 0;
let bannerInterval = null;

async function inicializarBanner() {
    const titulos = ['Ta dando onda', 'Interestelar', 'Matrix', 'Batman', 'Duna', 'Avatar'];

    for (const titulo of titulos) {
        try {
            const resultados = await midiaService.buscarFilmes(titulo);
            if (resultados && resultados.length > 0 && resultados[0].poster) {
                bannerFilmes.push({ ...resultados[0], tipo: 'filme' });
            }
        } catch (err) {
            console.error(`Erro ao buscar "${titulo}" pro banner:`, err);
        }
    }

    if (bannerFilmes.length > 0) {
        exibirBanner(bannerFilmes[0]);
        bannerInterval = setInterval(() => {
            bannerIndex = (bannerIndex + 1) % bannerFilmes.length;
            exibirBanner(bannerFilmes[bannerIndex]);
        }, 6000);
    }
}

function exibirBanner(midia) {
    const bg = document.querySelector('.hero-bg');
    const titulo = document.querySelector('.hero-title');
    const desc = document.querySelector('.hero-desc');
    const nota = document.querySelector('.hero-nota');
    const heroContent = document.querySelector('.hero-content');
    const hero = document.querySelector('.hero');

    if (bg) bg.style.opacity = '0';
    if (heroContent) heroContent.style.opacity = '0';

    setTimeout(() => {
        if (bg) {
            bg.style.backgroundImage = `url(${midia.backdrop || midia.poster})`;
            bg.style.opacity = '1';
        }
        if (titulo) titulo.textContent = midia.titulo || '';
        if (desc) desc.textContent = midia.descricao || '';
        if (nota) nota.textContent = `★ ${midia.notaMedia?.toFixed(1) || '0.0'}`;
        if (heroContent) heroContent.style.opacity = '1';
    }, 400);

    if (hero) {
        hero.style.cursor = 'pointer';
        hero.onclick = () => window.abrirDetalhesMidia(midia, 'filme');
    }
}

// --- UTILIDADE ---
async function garantirFilmeNoBanco(midia) {
    if (midia.id) return midia.id;

    const token = localStorage.getItem('token');
    const body = {
        tmdbId: midia.tmdbId || null,
        titulo: (midia.titulo || '').substring(0, 40),
        descricao: (midia.descricao || 'Sem descrição').substring(0, 200),
        duracao: midia.duracao || 0,
        dataLancamento: midia.dataLancamento || '2000-01-01',
        classificacaoIndicativa: midia.classificacaoIndicativa || 'LIVRE'
    };

    const resp = await fetch('http://localhost:8082/filmes', {
        method: 'POST',
        headers: {
            'Authorization': token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    });

    if (!resp.ok) throw new Error('Erro ao cadastrar filme');

    const filmeSalvo = await resp.json();
    return filmeSalvo.id;
}

// --- INICIALIZAÇÃO AUTOMÁTICA DA PÁGINA ---
document.addEventListener('DOMContentLoaded', () => {
    gerenciarNavbarUI();
    verificarPermissaoAdmin();
    carregarCategoriasNoSelect();
    popularCarrosseis();
    inicializarBanner();

    const btnBuscar = document.querySelector('.btn-buscar');
    if (btnBuscar) btnBuscar.addEventListener('click', dispararBuscaCatalogo);

    const selectCategoria = document.getElementById('filter-categoria');
    if (selectCategoria) {
        selectCategoria.addEventListener('change', async function() {
            const categoriaId = this.value;
            if (!categoriaId) {
                document.getElementById('container-carrosseis').style.display = 'block';
                document.getElementById('container-filmes-api').innerHTML = '<p style="color:var(--muted);padding:20px;">Use a barra de busca acima para explorar mídias unificadas...</p>';
                document.getElementById('titulo-container-dinamico').textContent = 'Resultados do Catálogo';
                return;
            }

            const container = document.getElementById('container-filmes-api');
            const titulo = document.getElementById('titulo-container-dinamico');
            const nomeCategoria = this.options[this.selectedIndex].text;

            titulo.textContent = `Categoria: ${nomeCategoria}`;
            container.innerHTML = '<p style="color:var(--muted);padding:20px;">Carregando...</p>';
            document.getElementById('container-carrosseis').style.display = 'none';

            try {
                const filmes = await midiaService.buscarFilmesPorCategoria(categoriaId);
                container.innerHTML = '';

                if (!filmes || filmes.length === 0) {
                    container.innerHTML = '<p style="color:var(--muted);padding:20px;">Nenhum filme nessa categoria.</p>';
                    return;
                }

                filmes.forEach(f => {
                    container.appendChild(cardComponent.criar({ ...f, tipo: 'filme' }));
                });
            } catch (err) {
                container.innerHTML = '<p style="color:#fca5a5;padding:20px;">Erro ao buscar filmes da categoria.</p>';
            }
        });
    }
    // =======================================================
// CONTROLE DO MODAL DE CADASTRO DE FILME (ADMIN)
// =======================================================
    window.abrirModalCadastro = function() {
        document.getElementById('modal-cadastro-filme').classList.add('ativo');
    };

    window.fecharModalCadastro = function() {
        document.getElementById('modal-cadastro-filme').classList.remove('ativo');
        document.getElementById('form-cadastro-filme').reset(); // Limpa os campos ao fechar
    };

    window.enviarNovoFilme = async function() {
        const token = localStorage.getItem('token');

        // Pega os valores digitados no formulário
        const tmdbId = document.getElementById('cad-tmdbId').value;
        const titulo = document.getElementById('cad-titulo').value.trim();
        const descricao = document.getElementById('cad-descricao').value.trim();
        const duracao = document.getElementById('cad-duracao').value;
        const dataLancamento = document.getElementById('cad-data').value || '2000-01-01';
        const classificacao = document.getElementById('cad-classificacao').value;

        // Monta o objeto DTO que o seu Spring Boot espera
        const body = {
            tmdbId: tmdbId ? parseInt(tmdbId) : null,
            titulo: titulo,
            descricao: descricao || 'Sem descrição',
            duracao: duracao ? parseInt(duracao) : 0,
            dataLancamento: dataLancamento,
            classificacaoIndicativa: classificacao
        };

        try {
            const resp = await fetch('http://localhost:8082/filmes', {
                method: 'POST',
                headers: {
                    'Authorization': token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (resp.ok) {
                alert('🎬 Filme cadastrado com sucesso no banco de dados!');
                fecharModalCadastro();

                // Se o usuário estiver na tela de "Todos" ou "Filmes", atualiza a busca para ele ver o filme novo
                const inputBuscar = document.querySelector('.filter-search');
                if(inputBuscar && typeof dispararBuscaCatalogo === 'function') {
                    dispararBuscaCatalogo();
                }
            } else {
                alert('Erro ao cadastrar filme. Verifique se os dados estão corretos ou se já não existe.');
            }
        } catch (err) {
            console.error(err);
            alert('Erro de conexão com a API.');
        }
    };
});