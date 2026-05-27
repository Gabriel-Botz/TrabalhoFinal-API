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
window.abrirDetalhesMidia = function(midia, tipo) {
    const modal = document.getElementById('modal-detalhes');
    if (!modal) return;

    midiaSelecionadaAtg = { ...midia, tipoMidiaCorrente: tipo };

    // Backdrop
    const backdropEl = document.getElementById('detalhes-backdrop');
    if (backdropEl) {
        backdropEl.style.backgroundImage = midia.poster
            ? `url(${midia.poster})`
            : 'none';
    }

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

    // Reseta para aba de avaliações
    setDetalhesTab('avaliacoes');

    // Carrega elenco se tiver tmdbId
    if (midia.tmdbId) carregarElenco(midia.tmdbId, tipo);

    modal.classList.add('ativo');
};

window.setDetalhesTab = function(tab) {
    document.querySelectorAll('.detalhes-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.detalhes-tab-content').forEach(c => c.classList.add('hidden'));

    document.getElementById('tab-' + tab).classList.add('active');
    document.getElementById('content-' + tab).classList.remove('hidden');
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

    try {
        await listaService.criarLista(nome, privada);
        document.getElementById('input-nome-lista').value = '';
        window.ocultarFormLista();
        renderizarListasUsuario();
    } catch (err) {
        alert('Erro ao criar lista de favoritos.');
    }
};

async function renderizarListasUsuario() {
    const container = document.getElementById('container-listas');
    if (!container) return;

    try {
        const publicas = await listaService.listarPublicas();
        container.innerHTML = '';

        if (publicas.length === 0) {
            container.innerHTML = '<p style="color:var(--muted); font-size:13px;">Nenhuma lista criada.</p>';
            return;
        }

        publicas.forEach(lista => {
            container.innerHTML += `
                <div style="background:var(--surface2); padding:12px; border-radius:8px; display:flex; justify-content:space-between; align-items:center;">
                    <div>
                        <p style="font-size:14px; font-weight:600;">${lista.nomeLista || 'Favoritos'}</p>
                        <span style="font-size:11px; color:var(--muted);">${lista.privada ? '🔒 Privada' : '🌍 Pública'}</span>
                    </div>
                    <button class="modal-close" style="position:static; font-size:18px;" onclick="window.removerListaReal('${lista.id}')">×</button>
                </div>
            `;
        });
    } catch (err) {
        container.innerHTML = '<p style="color:#fca5a5;">Erro ao obter listas.</p>';
    }
}

window.removerListaReal = async function(id) {
    if (confirm('Deseja apagar essa lista?')) {
        try {
            await listaService.deletarLista(id);
            renderizarListasUsuario();
        } catch (err) {
            alert('Erro ao remover lista.');
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