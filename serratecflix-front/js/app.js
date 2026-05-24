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

document.getElementById('modal-auth').addEventListener('click', function(e) {
    if (e.target === this) fecharModal();
});