const avaliacoesMock = [
    { nome: 'Ana Lima', username: 'ana_lima', nota: 9.0, comentario: 'Simplesmente incrível! Um dos melhores que já assisti.', data: '2025-03-10' },
    { nome: 'Carlos Mendes', username: 'carlos_m', nota: 7.5, comentario: 'Muito bom, roteiro bem construído.', data: '2025-02-20' },
    { nome: 'Julia Rocha', username: 'julia_r', nota: 8.5, comentario: 'Recomendo muito, vale cada minuto!', data: '2025-01-15' },
];

function abrirModal() {
    document.getElementById('modal-auth').classList.add('ativo');
}

function fecharModal() {
    document.getElementById('modal-auth').classList.remove('ativo');
}

function setTab(tab) {
    document.getElementById('form-login').classList.add('hidden');
    document.getElementById('form-cadastro').classList.add('hidden');
    document.getElementById('tab-login').classList.remove('active');
    document.getElementById('tab-cadastro').classList.remove('active');
    document.getElementById('form-' + tab).classList.remove('hidden');
    document.getElementById('tab-' + tab).classList.add('active');
}

function mostrarErro(id, mensagem) {
    const el = document.getElementById(id);
    el.textContent = mensagem;
    el.classList.add('visivel');
}

function limparErro(id) {
    const el = document.getElementById(id);
    el.textContent = '';
    el.classList.remove('visivel');
}

function login() {
    limparErro('erro-login');

    const username = document.querySelector('#form-login .auth-input:nth-child(1)').value;
    const senha = document.querySelector('#form-login .auth-input:nth-child(2)').value;

    if (!username || !senha) return mostrarErro('erro-login', '⚠ Preencha todos os campos.');
    if (senha.length < 6) return mostrarErro('erro-login', '⚠ Senha mínima de 6 caracteres.');

    alert('Login realizado! (integração com API em breve)');
    fecharModal();
}

function cadastrar() {
    limparErro('erro-cadastro');

    const inputs = document.querySelectorAll('#form-cadastro .auth-input');
    const nome = inputs[0].value;
    const email = inputs[1].value;
    const username = inputs[2].value;
    const senha = inputs[3].value;

    if (!nome || !email || !username || !senha) return mostrarErro('erro-cadastro', '⚠ Preencha todos os campos obrigatórios.');
    if (!email.includes('@')) return mostrarErro('erro-cadastro', '⚠ E-mail inválido.');
    if (senha.length < 6) return mostrarErro('erro-cadastro', '⚠ Senha mínima de 6 caracteres.');

    alert('Cadastro realizado! (integração com API em breve)');
    fecharModal();
}

function abrirDetalhes(titulo, descricao, nota, ano, poster, backdrop) {
    document.getElementById('detalhes-titulo').textContent = titulo;
    document.getElementById('detalhes-desc').textContent = descricao;
    document.getElementById('detalhes-poster').src = poster;
    document.getElementById('detalhes-backdrop').style.backgroundImage = `url(${backdrop})`;
    document.getElementById('detalhes-meta').innerHTML = `
    <span class="meta-nota">★ ${nota}</span>
    <span>${ano}</span>
  `;

    renderAvaliacoes(avaliacoesMock);
    document.getElementById('modal-detalhes').classList.add('ativo');
}

function fecharDetalhes() {
    document.getElementById('modal-detalhes').classList.remove('ativo');
}

function atualizarNota(valor) {
    document.getElementById('nota-valor').textContent = valor;
}

function renderAvaliacoes(lista) {
    const container = document.getElementById('avaliacoes-lista');
    container.innerHTML = lista.map(av => `
    <div class="avaliacao-item">
      <div class="avaliacao-header">
        <div class="avaliacao-usuario">
          <div class="avaliacao-avatar">${av.nome[0]}</div>
          <span class="avaliacao-nome">${av.nome}</span>
          <span class="avaliacao-username">@${av.username}</span>
        </div>
        <span class="avaliacao-nota-badge">★ ${av.nota}</span>
      </div>
      <p class="avaliacao-comentario">${av.comentario}</p>
      <p class="avaliacao-data">${av.data}</p>
    </div>
  `).join('');
}

function enviarAvaliacao() {
    const nota = document.getElementById('input-nota').value;
    const comentario = document.getElementById('input-comentario').value;

    if (!comentario.trim()) return alert('Escreva um comentário!');

    avaliacoesMock.unshift({
        nome: 'Você',
        username: 'usuario',
        nota: parseFloat(nota),
        comentario,
        data: new Date().toISOString().split('T')[0]
    });

    renderAvaliacoes(avaliacoesMock);
    document.getElementById('input-comentario').value = '';
}

function setTipo(tipo) {
    document.querySelectorAll('.toggle-btn').forEach(btn => btn.classList.remove('active'));
    document.getElementById('btn-' + tipo).classList.add('active');

    const categoriaSelect = document.getElementById('filter-categoria');
    categoriaSelect.disabled = tipo === 'series';
    categoriaSelect.style.opacity = tipo === 'series' ? '0.4' : '1';
}

document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('modal-auth').addEventListener('click', function (e) {
        if (e.target === this) fecharModal();
        document.getElementById('modal-selecionar-lista').addEventListener('click', function(e) {
            if (e.target === this) fecharSelecionarLista();
        });
    });

    document.getElementById('modal-detalhes').addEventListener('click', function (e) {
        if (e.target === this) fecharDetalhes();
        document.getElementById('modal-listas').addEventListener('click', function(e) {
            if (e.target === this) fecharListas();
        });
    });
});

let listas = [
    { id: 1, nomeLista: 'Meus Favoritos', privada: false, items: [] }
];

function abrirListas() {
    renderListas();
    document.getElementById('modal-listas').classList.add('ativo');
}

function fecharListas() {
    document.getElementById('modal-listas').classList.remove('ativo');
}

function mostrarFormLista() {
    document.getElementById('form-nova-lista').classList.remove('hidden');
}

function ocultarFormLista() {
    document.getElementById('form-nova-lista').classList.add('hidden');
    document.getElementById('input-nome-lista').value = '';
    document.getElementById('input-privada').checked = false;
}

function criarLista() {
    const nome = document.getElementById('input-nome-lista').value.trim();
    const privada = document.getElementById('input-privada').checked;

    if (!nome) return alert('Digite um nome para a lista!');

    listas.push({ id: Date.now(), nomeLista: nome, privada, items: [] });
    ocultarFormLista();
    renderListas();
}

function renderListas() {
    const container = document.getElementById('container-listas');
    container.innerHTML = listas.map(lista => `
    <div class="avaliacao-item" style="margin-bottom:12px;">
      <div style="display:flex; align-items:center; gap:10px; margin-bottom:8px;">
        <h3 style="font-size:15px; font-weight:600;">${lista.nomeLista}</h3>
        <span style="font-size:11px; padding:2px 8px; border-radius:4px; background:${lista.privada ? 'rgba(239,68,68,0.15)' : 'rgba(34,197,94,0.15)'}; color:${lista.privada ? '#fca5a5' : '#86efac'};">
          ${lista.privada ? 'Privada' : 'Pública'}
        </span>
        <span style="font-size:12px; color:var(--muted);">${lista.items.length} item(s)</span>
      </div>
      ${lista.items.length === 0
        ? `<p style="font-size:13px; color:var(--muted);">Nenhum item ainda.</p>`
        : `<div style="display:flex; gap:8px; flex-wrap:wrap;">${lista.items.map(i => `<span style="font-size:12px; background:var(--surface2); padding:4px 10px; border-radius:4px;">${i}</span>`).join('')}</div>`
    }
    </div>
  `).join('');
}

function filtrarPorTipo(tipo) {
    const secoes = document.querySelectorAll('.carousel-section');

    secoes.forEach(secao => {
        const titulo = secao.querySelector('.carousel-title').textContent.toLowerCase();

        if (tipo === 'todos') {
            secao.style.display = 'block';
        } else if (tipo === 'filmes') {
            secao.style.display = titulo.includes('série') ? 'none' : 'block';
        } else if (tipo === 'series') {
            secao.style.display = titulo.includes('série') ? 'block' : 'none';
        }
    });
}

let itemAtual = null;

function abrirDetalhes(titulo, descricao, nota, ano, poster, backdrop) {
    itemAtual = { titulo, descricao, nota, ano, poster, backdrop };

    document.getElementById('detalhes-titulo').textContent = titulo;
    document.getElementById('detalhes-desc').textContent = descricao;
    document.getElementById('detalhes-poster').src = poster;
    document.getElementById('detalhes-backdrop').style.backgroundImage = `url(${backdrop})`;
    document.getElementById('detalhes-meta').innerHTML = `
    <span class="meta-nota">★ ${nota}</span>
    <span>${ano}</span>
  `;

    renderAvaliacoes(avaliacoesMock);
    document.getElementById('modal-detalhes').classList.add('ativo');
}

function abrirSelecionarLista() {
    const opcoes = listas.map((lista, i) => `
    <div class="avaliacao-item" style="cursor:pointer;margin-bottom:8px;" onclick="adicionarALista(${i})">
      <div style="display:flex;justify-content:space-between;align-items:center;">
        <span style="font-size:14px;font-weight:500;">${lista.nomeLista}</span>
        <span style="font-size:11px;padding:2px 8px;border-radius:4px;background:${lista.privada?'rgba(239,68,68,0.15)':'rgba(34,197,94,0.15)'};color:${lista.privada?'#fca5a5':'#86efac'};">
          ${lista.privada?'Privada':'Pública'}
        </span>
      </div>
    </div>
  `).join('');

    document.getElementById('modal-selecionar-lista').innerHTML = `
    <div class="modal-detalhes-box" style="max-width:400px;">
      <button class="modal-close" onclick="fecharSelecionarLista()">×</button>
      <div class="detalhes-content">
        <h3 style="margin-bottom:16px;">Adicionar à lista</h3>
        ${opcoes}
      </div>
    </div>
  `;

    document.getElementById('modal-selecionar-lista').classList.add('ativo');
}

function fecharSelecionarLista() {
    document.getElementById('modal-selecionar-lista').classList.remove('ativo');
}

function adicionarALista(index) {
    if (!itemAtual) return;
    listas[index].items.push(itemAtual.titulo);
    fecharSelecionarLista();
    alert(`"${itemAtual.titulo}" adicionado à lista "${listas[index].nomeLista}"!`);
}