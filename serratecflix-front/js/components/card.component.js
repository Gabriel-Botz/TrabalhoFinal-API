export const cardComponent = {
    criar(midia) {
        if (!midia) return document.createElement('div');

        // Log de inspeção: veja no console se o ID aparece aqui!
        console.log("Criando card para mídia:", midia);

        const id = midia.tmdbId;
        const titulo = midia.titulo || midia.nome || "Título Desconhecido";
        const poster = midia.poster;
        const nota = (typeof midia.notaMedia === 'number') ? midia.notaMedia : 0;

        // Ajuste: verificamos o 'midia.tipo' se ele existir, senão inferimos
        const ehSerie = midia.tipo === 'serie' || midia.tipo === 'series' || midia.temporadas !== undefined;
        const tipoLabel = ehSerie ? 'Série' : 'Filme';
        const classeBadge = ehSerie ? 'serie' : '';
        const imagemFinal = (poster && poster.startsWith('http')) ? poster : './assets/img/sem-imagem.png';

        const card = document.createElement('div');
        card.className = 'card';

        // Garante que o ID existe antes de criar o evento
        if (id) {
            card.addEventListener('click', () => {
                window.abrirDetalhesMidia(midia, ehSerie ? 'serie' : 'filme');
            });
        } else {
            console.error("Card criado sem ID:", midia);
        }

        card.innerHTML = `
            <div class="card-image-container">
                <img src="${imagemFinal}" alt="${titulo}" class="card-image">
                <span class="card-nota">★ ${nota.toFixed(1)}</span>
                <span class="card-badge ${classeBadge}">${tipoLabel}</span>
            </div>
            <div class="card-content">
                <h3 class="card-title">${titulo}</h3>
                <div class="card-info">
                    <span class="card-year">Mídia Digital</span>
                </div>
            </div>
        `;

        return card;
    }
};