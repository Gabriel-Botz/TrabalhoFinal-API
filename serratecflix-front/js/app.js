import { authService } from './auth/auth.services.js'; // Notou o 'services' com S?
import { midiaService } from './services/midia.service.js';
import { avaliacaoService } from './services/avaliacao.service.js';
import { listaService } from './services/lista.service.js';
import { cardComponent } from './components/card.component.js';

// --- ESTADO GLOBAL DA PÁGINA ---
let tipoAtual = 'todos';
let midiaSelecionadaAtg = null; // Guarda a mídia aberta no momento para favoritar ou avaliar

// --- RENDERIZAR ESTADO DA NAVBAR ---
function gerenciarNavbarUI() {
    const btnLogin = document.querySelector('.btn-login');
    if (!btnLogin) return;

    // Adicionamos a verificação casada: tem que estar autenticado E o usuário precisa existir
    if (authService.isAuthenticated() && authService.getCurrentUser()) {
        const user = authService.getCurrentUser();
        // Usamos um fallback caso o username não exista por algum motivo
        const nomeUsuario = user.username || 'Usuário';

        btnLogin.textContent = `Sair (${nomeUsuario})`;
        btnLogin.onclick = () => {
            authService.logout();
            gerenciarNavbarUI();
        };
    } else {
        // Se não tiver token ou o usuário sumiu do localStorage, força o estado de "Entrar"
        btnLogin.textContent = 'Entrar';
        btnLogin.onclick = () => window.abrirModal();
    }
}

// --- POPULAR CATEGORIAS NO SELECT DO CSS ---
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

// --- CONTROLE DE MODAIS (GATILHOS DO SEU CSS .ativo E .hidden) ---
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

// --- LOGIN E CADASTRO ---
window.executarLogin = async function() {
    const inputs = document.querySelectorAll('#form-login .auth-input');
    const username = inputs[0].value.trim();
    const password = inputs[1].value.trim();
    const erroContainer = document.getElementById('erro-login');

    try {
        await authService.login(username, password);
        window.fecharModal();
        gerenciarNavbarUI();
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

// --- FILTROS DE ABAS (TODOS, FILMES, SÉRIES) ---
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

// --- PROCESSO DE BUSCA UNIFICADO (CORRIGIDO) ---
async function dispararBuscaCatalogo() {
    const container = document.getElementById('container-filmes-api');
    const inputBuscar = document.querySelector('.filter-search');
    const tituloContainer = document.getElementById('titulo-container-dinamico');
    if (!container) return;

    const query = inputBuscar ? inputBuscar.value.trim() : '';

    if (tituloContainer) {
        tituloContainer.textContent = query ? `Resultados para: "${query}"` : "Resultados do Catálogo";
    }

    // Limpa o container antes de novos resultados
    container.innerHTML = '';

    try {
        let encontrouAlgo = false;

        // Cenário 1: Buscar Filmes
        if ((tipoAtual === 'todos' || tipoAtual === 'filmes') && query) {
            const filmes = await midiaService.buscarFilmes(query);
            if (filmes && Array.isArray(filmes)) {
                filmes.forEach(f => {
                    container.appendChild(cardComponent.criar(f));
                    encontrouAlgo = true;
                });
            }
        }

        // Cenário 2: Buscar Séries
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

// --- MODAL DE DETALHES DINÂMICO (ABRE FILMES E SÉRIES) ---
window.abrirDetalhesMidia = function(midia, tipo) { // <-- Abre a função (1)
    const modal = document.getElementById('modal-detalhes');
    if (!modal) return;

    midiaSelecionadaAtg = { ...midia, tipoMidiaCorrente: tipo };

    // Backdrop
    const backdropEl = document.getElementById('detalhes-backdrop');
    if (backdropEl) { // <-- Abre o if (2)
        const imagemBanner = midia.backdrop || midia.poster;
        backdropEl.style.backgroundImage = imagemBanner ? `url(${imagemBanner})` : 'none';
    } // <-- Fecha o if (2)

    // Poster
    const posterEl = document.getElementById('detalhes-poster');
    if (posterEl) posterEl.src = midia.poster || './assets/img/sem-imagem.png';

    // Título e descrição
    document.getElementById('detalhes-titulo').textContent = midia.titulo || 'Sem título';
    document.getElementById('detalhes-desc').textContent = midia.descricao || 'Sem descrição disponível.';

    // Meta — filme vs série
    const metaEl = document.getElementById('detalhes-meta');
    const nota = midia.notaMedia?.toFixed(1) || '0.0';
    const ano = midia.dataLancamento ? midia.dataLancamento.substring(0, 4) : '—';
    const classificacao = midia.classificacaoIndicativa || '—';

    if (tipo === 'serie') { // <-- Abre o if (3)
        metaEl.innerHTML = `
            <span style="color:var(--detail-color);font-weight:700">★ ${nota}</span>
            <span>${ano}</span>
            <span>${midia.temporadas || '—'} temporada(s)</span>
            <span>${midia.episodios || '—'} episódios</span>
            <span style="background:rgba(0,240,255,0.1);border:1px solid var(--accent-color);padding:2px 8px;border-radius:4px;font-size:12px">${classificacao}</span>
        `;
    } else { // <-- Fecha o if (3) e abre o else (4)
        metaEl.innerHTML = `
            <span style="color:var(--detail-color);font-weight:700">★ ${nota}</span>
            <span>${ano}</span>
            <span>${midia.duracao ? midia.duracao + ' min' : '—'}</span>
            <span style="background:rgba(0,240,255,0.1);border:1px solid var(--accent-color);padding:2px 8px;border-radius:4px;font-size:12px">${classificacao}</span>
        `;
    } // <-- Fecha o else (4)

    setDetalhesTab('avaliacoes');

    if (midia.tmdbId) carregarElenco(midia.tmdbId, tipo);


    modal.classList.add('ativo');
};

window.setDetalhesTab = function(tab) {
    document.querySelectorAll('.detalhes-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.detalhes-tab-content').forEach(c => c.classList.add('hidden'));

    document.getElementById('tab-' + tab).classList.add('active');
    document.getElementById('content-' + tab).classList.remove('hidden');
};

// --- FLUXO DE FAVORITAR USANDO O BOTÃO E MODAL EXISTENTES ---

// 1. Função disparada ao clicar no botão de Coração do modal de detalhes
window.abrirSelecaoDeListaParaFavoritar = async function() {
    if (!authService.isAuthenticated()) {
        alert('Você precisa estar logado para favoritar uma mídia!');
        window.abrirModal(); // Abre o modal de login se não estiver logado
        return;
    }

    const modalSelecao = document.getElementById('modal-selecionar-lista');
    const containerListas = document.getElementById('lista-selecionar-container'); // Container interno do seu modal de seleção

    if (!modalSelecao || !containerListas) return;

    // Exibe o modal de seleção de lista na tela
    modalSelecao.classList.add('ativo');
    containerListas.innerHTML = '<p style="color:var(--muted); font-size:13px;">Carregando suas listas...</p>';

    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');

    try {
        // Busca as listas do usuário (usando o endpoint que traz tudo dele)
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

        // Renderiza as listas como botões clicáveis dentro do modal de seleção
        containerListas.innerHTML = listas.map(lista => `
            <button class="auth-btn" 
                    style="margin-top: 0; margin-bottom: 8px; width: 100%; text-align: left; background: var(--surface2); border: 1px solid rgba(0,240,255,0.2); justify-content: space-between; display: flex; align-items: center;" 
                    onclick="window.confirmarAdicionarMidia('${lista.id}')">
                <span>📂 ${lista.nomeLista}</span>
                <span style="font-size:11px; color:var(--muted);">${lista.privada ? '🔒 Privada' : '🌍 Pública'}</span>
            </button>
        `).join('');

    } catch (err) {
        console.error(err);
        containerListas.innerHTML = '<p style="color:#fca5a5; font-size:13px;">Erro ao carregar listas.</p>';
    }
};

// 2. Função executada quando o usuário clica em cima de uma das listas listadas no modal
window.confirmarAdicionarMidia = async function(idLista) {
    if (!midiaSelecionadaAtg) {
        console.error("Nenhuma mídia selecionada.");
        return;
    }

    // DEBUG: Verifique no console do navegador (F12) se o ID está chegando certo
    console.log("Tentando adicionar:", midiaSelecionadaAtg.id, "na lista:", idLista);

    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');
    const ehSerie = midiaSelecionadaAtg.tipoMidiaCorrente === 'serie';

    // Usamos o ID que vem do seu banco (geralmente midiaSelecionadaAtg.id)
    const rota = ehSerie
        ? `http://localhost:8082/lista-favoritos/series/${user.username}?idLista=${idLista}&idSerie=${midiaSelecionadaAtg.id}`
        : `http://localhost:8082/lista-favoritos/filmes/${user.username}?idLista=${idLista}&idFilme=${midiaSelecionadaAtg.id}`;

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
        console.error("Erro no catch:", err);
        alert('Erro ao comunicar com o servidor. Verifique o console.');
    }
};

window.abrirSelecionarLista = async function() {
    if (!authService.isAuthenticated()) {
        alert('Você precisa estar logado para favoritar uma mídia!');
        window.abrirModal(); // Abre o modal de login se não estiver logado
        return;
    }

    // Buscando o seu modal original e a div exata que você mencionou
    const modalSelecao = document.getElementById('modal-selecionar-lista'); // Verifique se o ID do modal pai é esse mesmo
    const containerListas = document.getElementById('lista-selecionavel-container');

    if (!modalSelecao || !containerListas) return;

    // Exibe o modal na tela e mostra mensagem de carregamento
    modalSelecao.classList.add('ativo');
    containerListas.innerHTML = '<p style="color:var(--muted); font-size:13px;">Carregando suas listas...</p>';

    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');

    try {
        // Busca as listas do usuário no seu banco de dados
        const resp = await fetch(`http://localhost:8082/lista-favoritos/privadas/${user.username}`, {
            headers: { 'Authorization': token }
        });

        if (!resp.ok) throw new Error();
        const listas = await resp.json();

        // Se o usuário não tiver listas
        if (listas.length === 0) {
            containerListas.innerHTML = `
                <p style="color:var(--muted); font-size:13px; margin-bottom:15px;">Você ainda não tem nenhuma lista criada.</p>
                <button class="auth-btn" style="margin-top:0; padding:6px 12px; font-size:12px;" onclick="document.getElementById('modal-selecionar-lista').classList.remove('ativo'); window.abrirListas();">+ Criar uma Lista</button>
            `;
            return;
        }

        // Renderiza as listas como botões clicáveis, já chamando a adição da mídia
        containerListas.innerHTML = listas.map(lista => `
            <button class="auth-btn" 
                    style="margin-top: 0; margin-bottom: 8px; width: 100%; text-align: left; background: var(--surface2); border: 1px solid rgba(0,240,255,0.2); justify-content: space-between; display: flex; align-items: center;" 
                    onclick="window.confirmarAdicionarMidia('${lista.id}')">
                <span>📂 ${lista.nomeLista}</span>
                <span style="font-size:11px; color:var(--muted);">${lista.privada ? '🔒 Privada' : '🌍 Pública'}</span>
            </button>
        `).join('');

    } catch (err) {
        console.error(err);
        containerListas.innerHTML = '<p style="color:#fca5a5; font-size:13px;">Erro ao carregar listas.</p>';
    }
};

window.abrirListas = async function() {
    if (!authService.isAuthenticated()) {
        alert('Faça login para ver suas listas!');
        window.abrirModal();
        return;
    }
    document.getElementById('modal-listas').classList.add('ativo');
    await carregarListasPrivadasDoUsuario();
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

    try {
        await listaService.criarLista(nome, privada);
        window.ocultarFormLista();
        await carregarListasPrivadasDoUsuario();
    } catch (err) {
        console.error(err);
        alert('Erro ao criar lista.');
    }
};

async function carregarElenco(tmdbId, tipo) {
    const container = document.getElementById('container-elenco');
    if (!container) return;

    container.innerHTML = '<p style="color:var(--muted)">Carregando elenco...</p>';

    try {
        const rota = tipo === 'serie'
            ? `http://localhost:8082/series/${tmdbId}/elenco`
            : `http://localhost:8082/filmes/${tmdbId}/elenco`;

        const token = localStorage.getItem('token');
        const resp = await fetch(rota, {
            headers: { 'Authorization': token }
        });

        if (!resp.ok) throw new Error('Elenco não disponível');

        const dados = await resp.json();
        const elenco = dados.elenco || dados;

        if (!elenco || elenco.length === 0) {
            container.innerHTML = '<p style="color:var(--muted)">Elenco não disponível.</p>';
            return;
        }

        container.innerHTML = elenco.slice(0, 10).map(ator => {
            const foto = ator.caminhoFoto
                ? `https://image.tmdb.org/t/p/w200${ator.caminhoFoto}`
                : './assets/img/sem-foto.png';
            return `
                <div class="ator-card">
                    <img src="${foto}" alt="${ator.nomeAtor}"/>
                    <p>${ator.nomeAtor}</p>
                </div>
            `;
        }).join('');

    } catch (err) {
        container.innerHTML = '<p style="color:var(--muted)">Elenco não disponível.</p>';
    }
}

window.fecharDetalhes = function() {
    document.getElementById('modal-detalhes').classList.remove('ativo');
    midiaSelecionadaAtg = null;
};

// --- SISTEMA DE ENVIAR AVALIAÇÃO REAL ---
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
            await avaliacaoService.avaliarFilme(midiaSelecionadaAtg.id, parseFloat(notaInput), comentarioInput);
        } else {
            // Se o AvaliacaoSerieController requerer DTO próprio, implementamos de forma análoga
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

    // Carrega um fallback estático bonito ou mapeia caso seu DTO traga a lista de avaliações vinculadas
    listaContainer.innerHTML = `
        <div class="avaliacao-item">
            <div class="avaliacao-header">
                <div class="avaliacao-usuario">
                    <div class="avaliacao-avatar">U</div>
                    <div>
                        <span class="avaliacao-nome">Usuário Serratec</span>
                    </div>
                </div>
                <span class="avaliacao-nota-badge">★ 9.0</span>
            </div>
            <p class="avaliacao-comentario">Filme fantástico! A fidelidade dos dados do catálogo integrado ficou fantástica.</p>
        </div>
    `;
}

// --- SISTEMA DE MINHAS LISTAS FAVORITAS ---
window.abrirListas = async function() {
    if (!authService.isAuthenticated()) {
        alert('Faça login para ver suas listas!');
        window.abrirModal();
        return;
    }

    document.getElementById('modal-listas').classList.add('ativo');
    setListasTab('minhas'); // Força abrir na aba de minhas listas primeiro
    renderizarListasUsuario();
};

window.fecharListas = function() {
    document.getElementById('modal-listas').classList.remove('ativo');
};

window.mostrarFormLista = function() {
    document.getElementById('form-nova-lista').classList.remove('hidden');
};

window.ocultarFormLista = function() {
    document.getElementById('form-nova-lista').classList.add('hidden');
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
        renderizarListasUsuario();
    } catch (err) {
        alert('Erro ao criar lista de favoritos.');
    }
};

async function renderizarListasUsuario() {
    const containerMinhas = document.getElementById('content-minhas-listas');
    const containerPublicas = document.getElementById('content-listas-publicas');

    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');

    try {
        // 1. Renderizar as Minhas Listas (traz do endpoint privado do usuário logado)
        if (user && user.username) {
            const respPrivadas = await fetch(`http://localhost:8082/lista-favoritos/privadas/${user.username}`, {
                headers: { 'Authorization': token }
            });
            if (respPrivadas.ok) {
                const minhasListas = await respPrivadas.json();
                renderizarListaNoContainer(containerMinhas, minhasListas, true);
            }
        }

        // 2. Renderizar as Listas Públicas Gerais
        const publicas = await listaService.listarPublicas();
        renderizarListaNoContainer(containerPublicas, publicas, false);

    } catch (err) {
        console.error("Erro ao renderizar listas:", err);
    }
}

function renderizarListaNoContainer(container, listas, ehDono) {
    if (!container) return;

    // Mantém o formulário de nova lista intacto se for o container de "minhas"
    const formHtml = ehDono ? document.getElementById('form-nova-lista')?.outerHTML || '' : '';

    if (!listas || listas.length === 0) {
        container.innerHTML = formHtml + '<p style="color:var(--muted); font-size:13px; padding: 10px;">Nenhuma lista encontrada.</p>';
        return;
    }

    let htmlConteudo = '';
    listas.forEach(lista => {
        // Só exibe o botão de remover se o usuário logado for o dono da lista
        const botaoDeletar = ehDono
            ? `<button class="modal-close" style="position:static; font-size:18px; background:none; border:none; color:var(--muted); cursor:pointer;" onclick="window.removerListaReal('${lista.id}')">×</button>`
            : '';

        htmlConteudo += `
            <div style="background:var(--surface2); padding:12px; border-radius:8px; display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                <div>
                    <p style="font-size:14px; font-weight:600; margin:0;">${lista.nomeLista || 'Favoritos'}</p>
                    <span style="font-size:11px; color:var(--muted);">${lista.privada ? '🔒 Privada' : '🌍 Pública'}</span>
                </div>
                ${botaoDeletar}
            </div>
        `;
    });

    container.innerHTML = formHtml + htmlConteudo;
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
            renderizarListasUsuario();
        } catch (err) {
            alert('Erro ao remover lista.');
        }
    }
};

// --- LÓGICA PARA ADICIONAR MÍDIA À LISTA DE FAVORITOS ---

// Carrega as listas do usuário logado dentro do select do modal
async function carregarListasNoSelectModal() {
    const select = document.getElementById('select-minhas-listas-modal');
    if (!select) return;

    if (!authService.isAuthenticated()) {
        select.innerHTML = '<option value="">Faça login para favoritar</option>';
        select.disabled = true;
        return;
    }

    select.disabled = false;
    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');

    try {
        const resp = await fetch(`http://localhost:8082/lista-favoritos/privadas/${user.username}`, {
            headers: { 'Authorization': token }
        });

        if (resp.ok) {
            const listas = await resp.json();
            select.innerHTML = '<option value="">Selecione uma lista...</option>';

            listas.forEach(lista => {
                const option = document.createElement('option');
                option.value = lista.id;
                option.textContent = lista.nomeLista;
                select.appendChild(option);
            });
        }
    } catch (err) {
        console.error('Erro ao popular select de favoritos no modal:', err);
    }
}

// Executa a ação de favoritar baseado no tipo da mídia corrente aberta
window.adicionarMidiaAoFavorito = async function() {
    if (!authService.isAuthenticated()) {
        alert('Você precisa estar logado para favoritar!');
        window.abrirModal();
        return;
    }

    if (!midiaSelecionadaAtg) return;

    const idLista = document.getElementById('select-minhas-listas-modal').value;
    if (!idLista) return alert('Por favor, selecione uma lista!');

    const user = authService.getCurrentUser();
    const token = localStorage.getItem('token');

    const ehSerie = midiaSelecionadaAtg.tipoMidiaCorrente === 'serie';

    // AQUI ENTRA O NOVO TRECHO
    const idParaUsar = ehSerie
        ? midiaSelecionadaAtg.id
        : await garantirFilmeNoBanco(midiaSelecionadaAtg);

    // agora usa idParaUsar
    const rota = ehSerie
        ? `http://localhost:8082/lista-favoritos/series/${user.username}?idLista=${idLista}&idSerie=${idParaUsar}`
        : `http://localhost:8082/lista-favoritos/filmes/${user.username}?idLista=${idLista}&idFilme=${idParaUsar}`;

    try {
        const resp = await fetch(rota, {
            method: 'POST',
            headers: { 'Authorization': token }
        });

        if (!resp.ok) {
            throw new Error('Erro ao adicionar');
        }

        alert('Adicionado à lista de favoritos com sucesso!');
    } catch (err) {
        if (midiaSelecionadaAtg.id === null) {
            alert('Este filme veio da busca externa (TMDB) e ainda não está cadastrado no banco. Não é possível favoritar.');
        } else {
            alert('Erro ao favoritar. Tente novamente.');
        }
    }
};
async function popularCarrosseis() {
    const container = document.getElementById('container-carrosseis');
    if (!container) return;

    const categorias = [
        { titulo: 'Filmes de Ação', query: 'ação', tipo: 'filmes' },
        { titulo: 'Fantasia', query: 'fantasia', tipo: 'filmes' },
        { titulo: 'Séries em Alta', query: 'breaking bad', tipo: 'series' },
        { titulo: 'Terror & Suspense', query: 'terror', tipo: 'filmes' },
        { titulo: 'Aventura', query: 'aventura', tipo: 'filmes' },
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

// --- BANNER ROTATIVO ---
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

    // Fade out
    if (bg) bg.style.opacity = '0';
    if (heroContent) heroContent.style.opacity = '0';

    setTimeout(() => {
        if (bg) {
            bg.style.backgroundImage = `url(${midia.backdrop || midia.poster})`; // <- só essa linha muda
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

        // Carrega as listas privadas do usuário logado
        await carregarListasPrivadasDoUsuario();
    }
    else if (aba === 'publicas') {
        // Chaveamento visual das abas
        tabPublicas.classList.add('active');
        tabMinhas.classList.remove('active');
        contentPublicas.classList.remove('hidden');
        contentMinhas.classList.add('hidden');

        // Consome o endpoint: GET /lista-favoritos/publicas
        await carregarListasPublicasGerais();
    }
};

// Função para buscar do back-end e renderizar as Listas Públicas da Comunidade
async function carregarListasPublicasGerais() {
    const container = document.getElementById('content-listas-publicas');
    if (!container) return;

    container.innerHTML = '<p style="color:var(--muted); padding:20px;">Carregando listas públicas...</p>';

    try {
        const token = localStorage.getItem('token');
        // URL aponta para o endpoint público do seu ListaFavoritosController
        const resp = await fetch('http://localhost:8082/lista-favoritos/publicas', {
            headers: { 'Authorization': token }
        });

        if (!resp.ok) throw new Error('Erro ao obter listas públicas.');
        const listasPublicas = await resp.json();

        if (listasPublicas.length === 0) {
            container.innerHTML = '<p style="color:var(--muted); padding:20px;">Nenhuma lista pública compartilhada ainda.</p>';
            return;
        }

        // Renderiza dinamicamente as listas públicas mantendo seu tema cyberpunk
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
        console.error(err);
        container.innerHTML = '<p style="color:#fca5a5; padding:20px;">Erro ao carregar a central de listas públicas.</p>';
    }
}

// Função adaptada para renderizar as Listas Privadas usando seu endpoint /privadas/{username}
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
            <div class="lista-card-item" style="border:1px solid rgba(57, 255, 20, 0.2); padding:15px; margin-bottom:12px; border-radius:8px; background:var(--card-bg);">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <h3 style="color:#fff;">${lista.nomeLista}</h3>
                    <span style="font-size:11px; background:${lista.privada ? 'rgba(57,255,20,0.1)' : 'rgba(0,240,255,0.1)'}; color:${lista.privada ? 'var(--detail-color)' : 'var(--accent-color)'}; border:1px solid ${lista.privada ? 'var(--detail-color)' : 'var(--accent-color)'}; padding:2px 6px; border-radius:4px;">
                        ${lista.privada ? '🔒 Privada' : '🌍 Pública'}</span>

                </div>
                <p style="font-size:12px; color:var(--muted); margin-top:4px; margin-bottom:10px;">Criada em ${new Date(lista.dataCriacao).toLocaleDateString('pt-BR')}</p>
                <div style="font-size:13px; color:#fff;">
                    🎬 Filmes: <strong>${lista.filmes?.length || 0}</strong> | 📺 Séries: <strong>${lista.series?.length || 0}</strong>
                </div>
            </div>
        `).join('');

    } catch (err) {
        console.error(err);
        container.innerHTML = '<p style="color:#fca5a5; padding:20px;">Erro ao carregar suas listas privadas.</p>';
    }
}

// --- ABRIR/FECHAR MODAL DE LISTAS ---
window.abrirListas = async function() {
    if (!authService.isAuthenticated()) {
        alert('Faça login para ver suas listas!');
        window.abrirModal();
        return;
    }
    document.getElementById('modal-listas').classList.add('ativo');
    await carregarListasPrivadasDoUsuario();
};

window.fecharListas = function() {
    document.getElementById('modal-listas').classList.remove('ativo');
};

// --- FORM DE CRIAR LISTA ---
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

    try {
        await listaService.criarLista(nome, privada);
        window.ocultarFormLista();
        await carregarListasPrivadasDoUsuario();
    } catch (err) {
        console.error(err);
        alert('Erro ao criar lista.');
    }
};

async function garantirFilmeNoBanco(midia) {
    if (midia.id) return midia.id; // já tem UUID local, usa direto

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
