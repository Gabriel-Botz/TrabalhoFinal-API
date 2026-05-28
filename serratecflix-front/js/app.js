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
    const btnGerenciar = document.getElementById('btn-gerenciar-midias'); // Captura o novo botão

    const alternarBotoes = (style) => {
        if (btnCadastro) btnCadastro.style.display = style;
        if (btnGerenciar) btnGerenciar.style.display = style;
    };

    if (!token) {
        alternarBotoes('none');
        return;
    }

    const dadosToken = decodificarJWT(token);
    if (!dadosToken) {
        alternarBotoes('none');
        return;
    }

    const payloadTexto = JSON.stringify(dadosToken).toUpperCase();
    if (payloadTexto.includes('ADMIN')) {
        alternarBotoes('block'); // Exibe ambos se for admin
    } else {
        alternarBotoes('none');
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

// Garante que a função fique global e visível para o HTML
window.setTabCadastro = function(tipo) {
    const tabFilme = document.getElementById('tab-cadastro-filme');
    const tabSerie = document.getElementById('tab-cadastro-serie');
    const formFilme = document.getElementById('form-cadastro-filme');
    const formSerie = document.getElementById('form-cadastro-serie');

    if (tipo === 'filme') {
        tabFilme.classList.add('active');
        tabSerie.classList.remove('active');
        formFilme.classList.remove('hidden');
        formSerie.classList.add('hidden');
    } else {
        tabSerie.classList.add('active');
        tabFilme.classList.remove('active');
        formSerie.classList.remove('hidden');
        formFilme.classList.add('hidden');
    }
};

window.fecharModalCadastro = function() {
    document.getElementById('modal-cadastro-filme').style.display = 'none';
    document.getElementById('form-cadastro-filme').reset();
    document.getElementById('form-cadastro-serie').reset();
    window.setTabCadastro('filme');
};

window.enviarNovaSerie = async function() {
    const token = localStorage.getItem('token');

    const titulo = document.getElementById('cad-serie-titulo').value.trim();
    const descricao = document.getElementById('cad-serie-descricao').value.trim();
    const temporadas = document.getElementById('cad-serie-temporadas').value;
    const episodios = document.getElementById('cad-serie-episodios').value;
    const dataLancamento = document.getElementById('cad-serie-data').value || '2000-01-01';

    // Mapeamento EXATO para o SeriesRequestDTO
    const body = {
        titulo: titulo,
        descricao: descricao || 'Sem descrição',
        temporadas: temporadas ? parseInt(temporadas) : 0,
        episodios: episodios ? parseInt(episodios) : 0,
        dataLancamento: dataLancamento,
        notaMedia: 0.0,    // O DTO exige Double e NotNull
        idCategorias: []   // O DTO tem a lista, enviamos vazia
    };

    try {
        const resp = await fetch('http://localhost:8082/series', {
            method: 'POST',
            headers: { 'Authorization': token, 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (resp.ok) {
            mostrarAlerta('📺 Série cadastrada com sucesso!', 'sucesso');
            fecharModalCadastro();
            if (typeof dispararBuscaCatalogo === 'function') dispararBuscaCatalogo();
        } else {
            const erroTxt = await resp.text();
            mostrarAlerta(`Erro da API (${resp.status}): ${erroTxt}`, 'erro');
        }
    } catch (err) {
        mostrarAlerta('Erro de conexão com o servidor.', 'erro');
    }
};

window.fecharModalCadastro = function() {
    document.getElementById('modal-cadastro-filme').style.display = 'none';
    document.getElementById('form-cadastro-filme').reset();
    document.getElementById('form-cadastro-serie').reset();
    window.setTabCadastro('filme'); // Reseta para a aba Filme ao fechar
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

    const containerAdmin = document.getElementById('botoes-admin-detalhes');
    if (containerAdmin) {
        const token = localStorage.getItem('token');
        const dadosToken = decodificarJWT(token);
        const payloadTexto = dadosToken ? JSON.stringify(dadosToken).toUpperCase() : '';

        // Se for admin, mostra a barra de ferramentas admin, se não, esconde
        if (payloadTexto.includes('ADMIN')) {
            containerAdmin.style.display = 'flex';
        } else {
            containerAdmin.style.display = 'none';
        }
    }

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
    // 1. Substitui o confirm nativo pelo seu modal customizado no meio da tela
    const confirmado = await window.mostrarConfirmacao("Deseja realmente apagar essa lista? Todos os itens salvos nela serão perdidos.");

    if (confirmado) {
        const user = authService.getCurrentUser();
        const token = localStorage.getItem('token');
        try {
            const resp = await fetch(`http://localhost:8082/lista-favoritos/${user.username}/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': token }
            });
            if (!resp.ok) throw new Error();

            // 2. Substitui o alert comum pelo seu Toast Verde Neon de Sucesso
            window.mostrarAlerta('🗑️ Lista apagada com sucesso!', 'sucesso');

            // Atualiza a tela
            await carregarListasPrivadasDoUsuario();
        } catch (err) {
            // 3. Substitui o alert comum pelo seu Toast Rosa Neon de Erro
            window.mostrarAlerta('Erro ao remover lista.', 'erro');
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

    if (!nome) return window.mostrarAlerta("Insira o nome da lista!", "erro");

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
// --- INICIALIZAÇÃO AUTOMÁTICA ---
document.addEventListener('DOMContentLoaded', () => {
    gerenciarNavbarUI();
    carregarCategoriasNoSelect();
    popularCarrosseis();
    inicializarBanner();

    const btnBuscar = document.querySelector('.btn-buscar');
    if (btnBuscar) btnBuscar.addEventListener('click', dispararBuscaCatalogo);

    // filtro por categoria
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
});

// --- CONTROLE DAS ABAS DA CENTRAL DE LISTAS (MINHAS VS PÚBLICAS) ---
window.setListasTab = async function(aba) {
    const tabMinhas = document.getElementById('tab-minhas-listas');
    const tabPublicas = document.getElementById('tab-listas-publicas');
    const contentMinhas = document.getElementById('content-minhas-listas');
    const contentPublicas = document.getElementById('content-listas-publicas');

    if (!tabMinhas || !tabPublicas || !contentMinhas || !contentPublicas) return;

    if (aba === 'minhas') {
        // Chaveamento visual das abas
        tabMinhas.classList.add('active');
        tabPublicas.classList.remove('active');
        contentMinhas.classList.remove('hidden');
        contentPublicas.classList.add('hidden');

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

    // --- EXCLUSÃO DE MÍDIA (ADMIN) ---
    window.excluirMidiaAdmin = async function() {
        if (!midiaSelecionadaAtg) return;

        const tipo = midiaSelecionadaAtg.tipoMidiaCorrente; // 'filme' ou 'serie'
        const id = midiaSelecionadaAtg.id;
        const token = localStorage.getItem('token');

        // 1. Pergunta usando seu modal customizado
        const confirmado = await window.mostrarConfirmacao(`Tem certeza que deseja excluir permanentemente este(a) ${tipo}?`);
        if (!confirmado) return;

        // 2. Define a rota correta da API baseado no tipo
        const rota = tipo === 'serie'
            ? `http://localhost:8082/series/${id}`
            : `http://localhost:8082/filmes/${id}`;

        try {
            const resp = await fetch(rota, {
                method: 'DELETE',
                headers: { 'Authorization': token }
            });

            if (resp.ok) {
                window.mostrarAlerta('🗑️ Título excluído com sucesso!', 'sucesso');
                window.fecharDetalhes(); // Fecha o modal de detalhes

                // Recarrega o catálogo para sumir com o card da tela
                if (typeof dispararBuscaCatalogo === 'function') {
                    dispararBuscaCatalogo();
                } else {
                    window.location.reload();
                }
            } else {
                const txt = await resp.text();
                window.mostrarAlerta(`Erro ao excluir: ${txt}`, 'erro');
            }
        } catch (err) {
            console.error(err);
            window.mostrarAlerta('Erro de conexão com o servidor.', 'erro');
        }
    };

    // --- EDIÇÃO DE MÍDIA (ADMIN) ---

// Abre o modal de edição e joga os dados atuais dentro dos inputs
    // Ache a função que abre o modal de edição no seu js/app.js
// Ela deve ser parecida com isso:
    window.abrirModalEditar = function(midia) {
        // ... (outros códigos que preenchem o título, sinopse, etc.) ...
        document.getElementById('edit-titulo').value = midia.titulo;
        document.getElementById('edit-descricao').value = midia.descricao || '';

        // VEJA SE ESSA PARTE ABAIXO EXISTE NO SEU CÓDIGO. SE NÃO EXISTIR, ADICIONE:
        const camposFilme = document.getElementById('edit-campos-filme');
        const camposSerie = document.getElementById('edit-campos-serie');

        // Verifica se a mídia é um Filme (pode ser checando o tipo ou se existe duracao)
        if (midia.duracao !== undefined || midia.tipo === 'FILME') {
            // É FILME: Mostra campos de filme e esconde os de série
            camposFilme.classList.remove('hidden');
            camposSerie.classList.add('hidden');

            // Preenche os campos específicos de filme
            document.getElementById('edit-duracao').value = midia.duracao || '';
            document.getElementById('edit-data-filme').value = midia.dataLancamento ? midia.dataLancamento.substring(0, 10) : '';
            document.getElementById('edit-classificacao').value = midia.classificacaoIndicativa || 'LIVRE';
        } else {
            // É SÉRIE: Mostra campos de série e esconde os de filme
            camposFilme.classList.add('hidden');
            camposSerie.classList.remove('hidden');

            // Preenche os campos específicos de série
            document.getElementById('edit-temporadas').value = midia.temporadas || '';
            document.getElementById('edit-episodios').value = midia.episodios || '';
            document.getElementById('edit-data-serie').value = midia.dataLancamento ? midia.dataLancamento.substring(0, 10) : '';
        }

        // Abre o modal adicionando a classe ativo
        document.getElementById('modal-editar-midia').classList.add('ativo');
    }

    window.fecharModalEditarAdmin = function() {
        document.getElementById('modal-editar-midia').classList.remove('ativo');
    };

// Dispara o PUT para a API
    // 4. Envia o PUT mantendo a estrutura exata exigida pelo Java
    window.enviarEdicaoMidiaAdmin = async function() {
        if (!midiaSendoEditada) return;

        const { id, tipoMidiaCorrente } = midiaSendoEditada;
        const token = localStorage.getItem('token');
        const rota = tipoMidiaCorrente === 'filme' ? `http://localhost:8082/filmes/${id}` : `http://localhost:8082/series/${id}`;

        let body = {};

        if (tipoMidiaCorrente === 'filme') {
            body = {
                // 👇 ATENÇÃO: Repare que a linha do "id" foi deletada daqui!
                tmdbId: midiaSendoEditada.tmdbId || null,
                titulo: document.getElementById('edit-titulo').value.trim(),
                descricao: document.getElementById('edit-descricao').value.trim(),
                duracao: parseInt(document.getElementById('edit-duracao').value) || 0,
                dataLancamento: document.getElementById('edit-data-filme').value || '2000-01-01',
                classificacaoIndicativa: document.getElementById('edit-classificacao').value
            };
        } else {
            body = {
                // 👇 E também não tem "id" aqui na série!
                titulo: document.getElementById('edit-titulo').value.trim(),
                descricao: document.getElementById('edit-descricao').value.trim(),
                temporadas: parseInt(document.getElementById('edit-temporadas').value) || 0,
                episodios: parseInt(document.getElementById('edit-episodios').value) || 0,
                dataLancamento: document.getElementById('edit-data-serie').value || '2000-01-01',
                notaMedia: midiaSendoEditada.notaMedia || 0.0,
                idCategorias: midiaSendoEditada.idCategorias || []
            };
        }

        console.log("JSON que será enviado (verifique no console F12 se tem algum ID infiltrado aqui):", body);

        try {
            const resp = await fetch(rota, {
                method: 'PUT',
                headers: {
                    'Authorization': token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (resp.ok) {
                window.mostrarAlerta('✏️ Mídia reconfigurada com sucesso!', 'sucesso');
                document.getElementById('modal-editar-midia').classList.remove('ativo');
                window.abrirModalGerenciar(); // Atualiza a lista com as mudanças feitas
                if (typeof dispararBuscaCatalogo === 'function') dispararBuscaCatalogo();
            } else {
                const txtErro = await resp.text();
                console.error("Resposta detalhada do erro no Java:", txtErro);
                window.mostrarAlerta(`A API recusou a alteração (Status: ${resp.status}). Abra o Console (F12) para ver o erro.`, 'erro');
            }
        } catch (err) {
            window.mostrarAlerta('Erro na conexão com o servidor do banco.', 'erro');
        }
    };

    // Função global para substituir o alert() genérico
    window.mostrarAlerta = function(mensagem, tipo = 'info') {
        // Busca o container ou cria um se ele não existir na página
        let container = document.getElementById('notification-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'notification-container';
            document.body.appendChild(container);
        }

        // Cria o elemento da notificação
        const toast = document.createElement('div');
        toast.className = `toast-alerta ${tipo}`;

        // Define um ícone baseado no tipo
        let icone = 'ℹ️';
        if (tipo === 'sucesso') icone = '⚡';
        if (tipo === 'erro') icone = '⚠️';

        // Monta o conteúdo estruturado
        toast.innerHTML = `
        <span style="font-size: 18px;">${icone}</span>
        <span style="flex: 1; line-height: 1.4;">${mensagem}</span>
    `;

        // Adiciona ao container da tela
        container.appendChild(toast);

        // Pequeno delay para o CSS registrar a animação de entrada
        setTimeout(() => {
            toast.classList.add('mostrar');
        }, 10);

        // Remove automaticamente após 4 segundos
        setTimeout(() => {
            toast.classList.remove('mostrar');
            // Aguarda a animação de saída terminar para remover do HTML definitivamente
            setTimeout(() => {
                toast.remove();
            }, 400);
        }, 4000);
    };
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

        const tmdbId = document.getElementById('cad-tmdbId').value;
        const titulo = document.getElementById('cad-titulo').value.trim();
        const descricao = document.getElementById('cad-descricao').value.trim();
        const duracao = document.getElementById('cad-duracao').value;
        const dataLancamento = document.getElementById('cad-data').value || '2000-01-01';
        const classificacao = document.getElementById('cad-classificacao').value;

        // Mapeamento EXATO para o FilmeRequestDTO
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
                headers: { 'Authorization': token, 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });

            if (resp.ok) {
                mostrarAlerta('🎬 Filme cadastrado com sucesso!', 'sucesso');
                fecharModalCadastro();
                if (typeof dispararBuscaCatalogo === 'function') dispararBuscaCatalogo();
            } else {
                const erroTxt = await resp.text();
                mostrarAlerta(`Erro da API (${resp.status}): ${erroTxt}`, 'erro');
            }
        } catch (err) {
            mostrarAlerta('Erro de conexão com o servidor.', 'erro');
        }
    };

    // Função da INTERFACE (app.js) que é acionada pelo botão "Apagar" do HTML
    window.tratarExclusaoLista = async function(idLista) {

        // 1. Dispara a nossa janela centralizada e aguarda o clique do usuário (Sim ou Não)
        const confirmado = await mostrarConfirmacao("Deseja realmente apagar essa lista? Todos os itens salvos nela serão perdidos.");

        // Se o usuário clicou em "Cancelar", a função para aqui e nada acontece
        if (!confirmado) return;

        try {
            // 2. Usuário confirmou! Chamamos o seu método do lista.service
            // (Certifique-se de usar o nome correto do objeto do seu serviço, ex: listaService)
            await listaService.deletarLista(idLista);

            // 3. Se a API respondeu com sucesso, exibe o Toast Verde Neon
            mostrarAlerta('🗑️ Lista apagada com sucesso!', 'sucesso');

            // 4. Chame aqui a sua função que atualiza as listas na tela automaticamente
            if (typeof carregarListasUsuarios === 'function') {
                carregarListasUsuarios();
            } else if (typeof dispararBuscaCatalogo === 'function') {
                dispararBuscaCatalogo(); // ou a função que você usa para recarregar as listas
            }

        } catch (err) {
            console.error(err);
            // 5. Se der erro (ex: Usuário não autenticado ou erro 400/500 da API), exibe o Toast Vermelho Neon
            mostrarAlerta(`Não foi possível apagar a lista: ${err.message || 'Erro interno'}`, 'erro');
        }
    };

    // Função global para substituir o confirm() nativo
    window.mostrarConfirmacao = function(mensagem) {
        return new Promise((resolve) => {
            // Cria o elemento de overlay do modal
            const overlay = document.createElement('div');
            overlay.className = 'confirm-overlay';

            overlay.innerHTML = `
            <div class="confirm-box">
                <p class="confirm-msg">${mensagem}</p>
                <div class="confirm-buttons">
                    <button class="btn-confirm-nao" id="confirm-btn-nao">Cancelar</button>
                    <button class="btn-confirm-sim" id="confirm-btn-sim">Excluir</button>
                </div>
            </div>
        `;

            document.body.appendChild(overlay);

            // Ativa a animação de entrada
            setTimeout(() => overlay.classList.add('mostrar'), 10);

            // Trata o fechamento e retorna o resultado
            const fecharModal = (resposta) => {
                overlay.classList.remove('mostrar');
                setTimeout(() => {
                    overlay.remove();
                    resolve(resposta); // Retorna true ou false para quem chamou
                }, 250);
            };

            // Vincula os cliques aos botões
            document.getElementById('confirm-btn-nao').onclick = () => fecharModal(false);
            document.getElementById('confirm-btn-sim').onclick = () => fecharModal(true);
        });
    };

// =====================================================================
    // CONTROLADOR DE GERENCIAMENTO (EDIÇÃO E EXCLUSÃO PELO ID)
    // =====================================================================
    let midiaSendoEditada = null; // Guardará o objeto completo vindo do banco para preservar o DTO

    // 1. Abre o modal e busca a lista de mídias direto pelos IDs da API
    window.abrirModalGerenciar = async function() {
        const modal = document.getElementById('modal-gerenciar-midias');
        const container = document.getElementById('lista-gerenciamento-container');

        if (!modal || !container) {
            console.error("Erro: Verifique se você adicionou os modais correspondentes no index.html");
            return;
        }

        // Abre visualmente o modal primeiro
        modal.classList.add('ativo');
        container.innerHTML = `<p style="color: var(--muted); font-size: 14px;">Carregando catálogo do sistema...</p>`;

        try {
            const [resFilmes, resSeries] = await Promise.all([
                fetch('http://localhost:8082/filmes'),
                fetch('http://localhost:8082/series')
            ]);

            const filmes = resFilmes.ok ? await resFilmes.json() : [];
            const series = resSeries.ok ? await resSeries.json() : [];

            container.innerHTML = '';

            if (filmes.length === 0 && series.length === 0) {
                container.innerHTML = `<p style="color: var(--muted);">Nenhuma mídia encontrada no banco de dados.</p>`;
                return;
            }

            // Renderiza Filmes vinculando o filme.id correto
            filmes.forEach(filme => {
                const item = document.createElement('div');
                item.style = "background: rgba(255,255,255,0.03); padding: 12px 16px; border-radius: 6px; border: 1px solid rgba(255,255,255,0.08); display: flex; justify-content: space-between; align-items: center; gap: 15px; margin-bottom: 8px;";
                item.innerHTML = `
                    <div style="display: flex; flex-direction: column; gap: 2px;">
                        <span style="color: #fff; font-weight: 500; font-size: 14px;">${filme.titulo}</span>
                        <span style="color: #00f0ff; font-size: 11px; font-weight: bold; text-transform: uppercase;">FILME (ID: ${filme.id})</span>
                    </div>
                    <div style="display: flex; gap: 8px;">
                        <button class="btn-buscar" style="background: #eab308; color: #000; padding: 6px 12px; font-size: 12px; font-weight: bold;" onclick="window.prepararEdicaoDireta('${filme.id}', 'filme')">✏️ Editar</button>
                        <button class="btn-fav" style="background: #ef4444; border-color: #ef4444; color: #fff; padding: 6px 12px; font-size: 12px;" onclick="window.executarExclusaoDireta('${filme.id}', 'filme')">🗑️ Excluir</button>
                    </div>
                `;
                container.appendChild(item);
            });

            // Renderiza Séries vinculando o serie.id correto
            series.forEach(serie => {
                const item = document.createElement('div');
                item.style = "background: rgba(255,255,255,0.03); padding: 12px 16px; border-radius: 6px; border: 1px solid rgba(255,255,255,0.08); display: flex; justify-content: space-between; align-items: center; gap: 15px; margin-bottom: 8px;";
                item.innerHTML = `
                    <div style="display: flex; flex-direction: column; gap: 2px;">
                        <span style="color: #fff; font-weight: 500; font-size: 14px;">${serie.titulo}</span>
                        <span style="color: #ff007f; font-size: 11px; font-weight: bold; text-transform: uppercase;">SÉRIE (ID: ${serie.id})</span>
                    </div>
                    <div style="display: flex; gap: 8px;">
                        <button class="btn-buscar" style="background: #eab308; color: #000; padding: 6px 12px; font-size: 12px; font-weight: bold;" onclick="window.prepararEdicaoDireta('${serie.id}', 'serie')">✏️ Editar</button>
                        <button class="btn-fav" style="background: #ef4444; border-color: #ef4444; color: #fff; padding: 6px 12px; font-size: 12px;" onclick="window.executarExclusaoDireta('${serie.id}', 'serie')">🗑️ Excluir</button>
                    </div>
                `;
                container.appendChild(item);
            });

        } catch (err) {
            console.error(err);
            container.innerHTML = `<p style="color: #ef4444;">Erro ao tentar conectar à API do catálogo.</p>`;
        }
    };

    // 2. Executa a exclusão na API baseando-se estritamente no ID da mídia
    window.executarExclusaoDireta = async function(id, tipo) {
        const token = localStorage.getItem('token');
        const confirmado = await window.mostrarConfirmacao(`Tem certeza que deseja deletar este ${tipo} permanentemente (ID do item: ${id})?`);
        if (!confirmado) return;

        const rota = tipo === 'filme' ? `http://localhost:8082/filmes/${id}` : `http://localhost:8082/series/${id}`;

        try {
            const resp = await fetch(rota, {
                method: 'DELETE',
                headers: { 'Authorization': token }
            });

            if (resp.ok) {
                window.mostrarAlerta('🗑️ Mídia removida do banco com sucesso!', 'sucesso');
                window.abrirModalGerenciar(); // Atualiza a lista em tempo real
                if (typeof dispararBuscaCatalogo === 'function') dispararBuscaCatalogo();
            } else {
                window.mostrarAlerta('A API recusou a exclusão deste ID.', 'erro');
            }
        } catch (err) {
            window.mostrarAlerta('Erro de rede ao tentar deletar.', 'erro');
        }
    };

    // 3. Busca os dados atuais e preserva TODAS as propriedades originais do banco (CORRIGIDO)
    window.prepararEdicaoDireta = async function(id, tipo) {
        const rota = tipo === 'filme' ? `http://localhost:8082/filmes/${id}` : `http://localhost:8082/series/${id}`;

        try {
            const resp = await fetch(rota);
            if (!resp.ok) throw new Error();
            const midia = await resp.json();

            // GUARDAMOS O OBJETO INTEIRO (Preserva tmdbId, idCategorias, etc.)
            midiaSendoEditada = { ...midia, tipoMidiaCorrente: tipo };

            document.getElementById('edit-titulo').value = midia.titulo || '';
            document.getElementById('edit-descricao').value = midia.descricao || '';

            const camposFilme = document.getElementById('edit-campos-filme');
            const camposSerie = document.getElementById('edit-campos-serie');

            if (tipo === 'filme') {
                // BLINDAGEM INFALÍVEL: Força a exibição e oculta o outro lado
                if (camposFilme) camposFilme.style.display = 'block';
                if (camposSerie) camposSerie.style.display = 'none';

                document.getElementById('edit-duracao').value = midia.duracao || '';
                document.getElementById('edit-data-filme').value = midia.dataLancamento ? midia.dataLancamento.substring(0, 10) : '';
                document.getElementById('edit-classificacao').value = midia.classificacaoIndicativa || 'LIVRE';
            } else {
                // BLINDAGEM INFALÍVEL: Força a exibição e oculta o outro lado
                if (camposFilme) camposFilme.style.display = 'none';
                if (camposSerie) camposSerie.style.display = 'block';

                document.getElementById('edit-temporadas').value = midia.temporadas || '';
                document.getElementById('edit-episodios').value = midia.episodios || '';
                document.getElementById('edit-data-serie').value = midia.dataLancamento ? midia.dataLancamento.substring(0, 10) : '';
            }

            document.getElementById('modal-editar-midia').classList.add('ativo');

        } catch (err) {
            window.mostrarAlerta('Erro ao resgatar informações do ID fornecido.', 'erro');
        }
    };

    // 4. Envia o PUT mantendo a estrutura exata exigida pelo Java (CORRIGIDO)
    window.enviarEdicaoMidiaAdmin = async function() {
        if (!midiaSendoEditada) return;

        const { id, tipoMidiaCorrente } = midiaSendoEditada;
        const token = localStorage.getItem('token');
        const rota = tipoMidiaCorrente === 'filme' ? `http://localhost:8082/filmes/${id}` : `http://localhost:8082/series/${id}`;

        let body = {};

        // Montamos o JSON mantendo as propriedades necessárias que o banco exige
        if (tipoMidiaCorrente === 'filme') {
            body = {
                id: parseInt(id),
                tmdbId: midiaSendoEditada.tmdbId || null, // Mantém o tmdbId original se existir
                titulo: document.getElementById('edit-titulo').value.trim(),
                descricao: document.getElementById('edit-descricao').value.trim(),
                duracao: parseInt(document.getElementById('edit-duracao').value) || 0,
                dataLancamento: document.getElementById('edit-data-filme').value || '2000-01-01',
                classificacaoIndicativa: document.getElementById('edit-classificacao').value
            };
        } else {
            body = {
                id: parseInt(id),
                titulo: document.getElementById('edit-titulo').value.trim(),
                descricao: document.getElementById('edit-descricao').value.trim(),
                temporadas: parseInt(document.getElementById('edit-temporadas').value) || 0,
                episodios: parseInt(document.getElementById('edit-episodios').value) || 0,
                dataLancamento: document.getElementById('edit-data-serie').value || '2000-01-01',
                notaMedia: midiaSendoEditada.notaMedia || 0,       // Mantém a nota original do banco
                idCategorias: midiaSendoEditada.idCategorias || [] // Mantém as categorias originais do banco
            };
        }

        console.log("Enviando JSON de atualização:", body);

        try {
            const resp = await fetch(rota, {
                method: 'PUT',
                headers: {
                    'Authorization': token,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (resp.ok) {
                window.mostrarAlerta('✏️ Mídia reconfigurada com sucesso!', 'sucesso');
                document.getElementById('modal-editar-midia').classList.remove('ativo');
                window.abrirModalGerenciar(); // Atualiza a lista com as mudanças feitas
                if (typeof dispararBuscaCatalogo === 'function') dispararBuscaCatalogo();
            } else {
                const txtErro = await resp.text();
                console.error("Resposta detalhada do erro no Java:", txtErro);
                window.mostrarAlerta(`A API recusou a alteração (Status: ${resp.status}). Abra o Console (F12) para ver o erro.`, 'erro');
            }
        } catch (err) {
            window.mostrarAlerta('Erro na conexão com o servidor do banco.', 'erro');
        }
    };
});
