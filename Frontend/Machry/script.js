lucide.createIcons();
  const sidebar = document.getElementById('sidebar');
  const menuToggle = document.getElementById('menuToggle');
  const homeView = document.getElementById('homeView');
  const detailView = document.getElementById('detailView');
  const ordersPanel = document.getElementById('ordersPanel');
  const originalParent = ordersPanel.parentElement;
  const originalNext = ordersPanel.nextSibling;
  const sections = {orders:'ordersDetail',recipes:'recipesDetail',stock:'stockDetail',purchases:'purchasesDetail',sales:'salesDetail',finance:'financeDetail',reports:'reportsDetail',team:'teamDetail',api:'apiDetail'};
  const titles = {orders:'Pedidos',recipes:'Receitas',stock:'Estoque',purchases:'Compras',sales:'Vendas',finance:'Financeiro',reports:'Relatórios',team:'Equipe',api:'Cadastros API'};

  function navigate(view) {
    const isHome = view === 'home';
    homeView.hidden = !isHome;
    detailView.hidden = isHome;
    document.querySelectorAll('.nav-item').forEach(button => {
      const active = button.dataset.view === view;
      button.classList.toggle('active', active);
      if (active) button.setAttribute('aria-current','page');
      else button.removeAttribute('aria-current');
    });
    Object.entries(sections).forEach(([key,id]) => document.getElementById(id).hidden = key !== view);
    if (view === 'orders') document.getElementById('ordersDetail').appendChild(ordersPanel);
    else originalParent.insertBefore(ordersPanel, originalNext);
    if (!isHome) document.getElementById('detailTitle').textContent = titles[view];
    sidebar.classList.remove('open');
    menuToggle.setAttribute('aria-expanded','false');
    window.scrollTo({top:0,behavior:'smooth'});
  }
  document.querySelectorAll('[data-view]').forEach(button => button.addEventListener('click', () => navigate(button.dataset.view)));
  menuToggle.addEventListener('click', () => {
    const open = sidebar.classList.toggle('open');
    menuToggle.setAttribute('aria-expanded',String(open));
  });
  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') {
      sidebar.classList.remove('open');
      menuToggle.setAttribute('aria-expanded','false');
    }
  });

  const orderList = document.getElementById('orderList');
  function refreshOrders() {
    const counts = {waiting:0,preparing:0,ready:0};
    orderList.querySelectorAll('.order-card').forEach(card => {
      const stage = card.dataset.stage;
      counts[stage]++;
      card.querySelectorAll('[data-status]').forEach(label => label.hidden = label.dataset.status !== stage);
      card.querySelectorAll('[data-action]').forEach(label => label.hidden = label.dataset.action !== stage);
      card.querySelector('.advance').hidden = stage === 'ready';
    });
    document.getElementById('waitingCount').textContent = counts.waiting;
    document.getElementById('preparingCount').textContent = counts.preparing;
    document.getElementById('readyCount').textContent = counts.ready;
  }
  orderList.addEventListener('click', event => {
    const button = event.target.closest('.advance');
    if (!button) return;
    const card = button.closest('.order-card');
    if (card.dataset.stage === 'waiting') card.dataset.stage = 'preparing';
    else if (card.dataset.stage === 'preparing') card.dataset.stage = 'ready';
    refreshOrders();
  });
  document.getElementById('orderSearch').addEventListener('input', event => {
    const term = event.target.value.trim().toLocaleLowerCase('pt-BR');
    if (term) navigate('orders');
    let visible = 0;
    orderList.querySelectorAll('.order-card').forEach(card => {
      card.hidden = !card.dataset.search.includes(term);
      if (!card.hidden) visible++;
    });
    document.getElementById('searchEmpty').hidden = visible !== 0;
  });
  refreshOrders();

  const brl = value => new Intl.NumberFormat('pt-BR',{style:'currency',currency:'BRL'}).format(value);
  document.getElementById('recipeForm').addEventListener('submit', event => {
    event.preventDefault();
    const input = document.getElementById('portions');
    const quantity = Number(input.value);
    const output = document.getElementById('recipeResult');
    if (!Number.isInteger(quantity) || quantity < 1 || quantity > 999) {
      output.textContent = 'Informe uma quantidade inteira entre 1 e 999.';
      input.focus();
      return;
    }
    const cost = Number(document.getElementById('recipeSelect').value);
    output.textContent = `Custo estimado para ${quantity} ${quantity === 1 ? 'porção' : 'porções'}: ${brl(quantity * cost)}`;
  });
  const purchaseChecks = [...document.querySelectorAll('#purchasesDetail input[type="checkbox"]')];
  function updatePurchaseProgress() {
    const checked = purchaseChecks.filter(input => input.checked).length;
    document.getElementById('purchaseProgress').textContent = `${checked} de ${purchaseChecks.length} itens conferidos nesta sessão`;
  }
  purchaseChecks.forEach(input => input.addEventListener('change',updatePurchaseProgress));
  updatePurchaseProgress();

  const sales = [708,441,553];
  const revenue = sales.reduce((total,value) => total + value,0);
  document.getElementById('salesTotal').textContent = `Total demonstrativo: ${brl(revenue)}`;
  document.getElementById('financeRevenue').textContent = brl(revenue);
  document.getElementById('financeBalance').textContent = `Saldo demonstrativo: ${brl(revenue - 620 - 340)}`;
  const reportData = {day:{revenue,orders:28},week:{revenue:11914,orders:196}};
  function updateReport() {
    const data = reportData[document.getElementById('reportPeriod').value];
    document.getElementById('reportRevenue').textContent = brl(data.revenue);
    document.getElementById('reportOrders').textContent = data.orders;
    document.getElementById('reportTicket').textContent = brl(data.revenue / data.orders);
  }
  document.getElementById('reportPeriod').addEventListener('change',updateReport);
  updateReport();
(() => {
  const API_BASE = 'http://localhost:8080';
  const resourceSelect = document.getElementById('apiResource');
  if (!resourceSelect) return;

  const $ = id => document.getElementById(id);
  const samplePayloads = {
    items: { nome: 'Farinha', tipo: 'INSUMO', unidadeMedida: 'G' },
    lotes: { itemId: 1, quantidade: 25.5, dataProducao: '2026-10-15', dataEntrada: '2026-10-16', dataValidade: '2026-11-15' },
    'fichas-tecnicas': {
      produtoId: 4, rendimento: 1000, unidadeRendimento: 'G',
      ingredientes: [{ itemId: 1, quantidade: 500 }],
      etapas: [{ ordem: 1, nome: 'Misturar ingredientes', tipoMetodo: 'PREPARO', descricao: 'Misturar até ficar homogêneo', tempoPrevistoMinutos: 10 }]
    },
    processamentos: {
      produtoId: 4, fichaTecnicaId: 1, quantidadeProduzida: 980, dataProcessamento: '2026-10-16',
      itensUtilizados: [{ itemId: 1, quantidadeUtilizada: 523 }],
      etapasRealizadas: [{ etapaFichaTecnicaId: 1, tempoRealizadoMinutos: 12, observacao: 'Etapa concluída' }]
    }
  };

  const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, char => ({
    '&':'&amp;', '<':'&lt;', '>':'&gt;', '"':'&quot;', "'":'&#39;'
  }[char]));
  const endpoint = () => `${API_BASE}/${resourceSelect.value}`;
  const setMessage = (message, error = false) => {
    $('apiMessage').textContent = message;
    $('apiMessage').classList.toggle('error', error);
  };
  const getId = () => $('apiId').value.trim();

  async function request(path = '', options = {}) {
    let response;
    try {
      response = await fetch(`${endpoint()}${path}`, {
        ...options,
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
      });
    } catch {
      throw new Error('Não foi possível conectar à API. Confirme se o backend está rodando em http://localhost:8080 e se o CORS está habilitado.');
    }
    const text = await response.text();
    let data = null;
    if (text) {
      try { data = JSON.parse(text); } catch { data = text; }
    }
    if (!response.ok) {
      throw new Error(data?.message || data?.error || `Erro HTTP ${response.status}`);
    }
    return data;
  }

  function showRecords(records) {
    const root = $('apiResults');
    if (!Array.isArray(records) || records.length === 0) {
      root.innerHTML = '<p class="intro">Nenhum registro encontrado.</p>';
      return;
    }
    root.innerHTML = records.map(record => {
      const id = record.id;
      const label = record.nome || record.itemNome || record.produtoNome || record.id || 'Registro';
      return `<article class="api-record">
        <div><strong>#${escapeHtml(id ?? '—')} · ${escapeHtml(label)}</strong>
        <pre>${escapeHtml(JSON.stringify(record, null, 2))}</pre></div>
        <div class="api-record-actions">
          <button type="button" class="api-secondary" data-api-edit="${escapeHtml(id)}">Ver JSON</button>
          <button type="button" class="api-secondary" data-api-select="${escapeHtml(id)}">Usar ID</button>
        </div>
      </article>`;
    }).join('');
  }

  async function listRecords() {
    try {
      const records = await request();
      showRecords(records);
      setMessage(`${records.length} registro(s) carregado(s) de ${resourceSelect.value}.`);
    } catch (error) {
      setMessage(error.message, true);
    }
  }

  function payload() {
    try {
      return JSON.parse($('apiPayload').value);
    } catch {
      throw new Error('O corpo da requisição não é um JSON válido.');
    }
  }

  $('apiRefresh').addEventListener('click', listRecords);
  $('apiNew').addEventListener('click', () => {
    $('apiId').value = '';
    $('apiPayload').value = JSON.stringify(samplePayloads[resourceSelect.value], null, 2);
    setMessage('Formulário limpo. Confira os IDs relacionados antes de cadastrar.');
  });
  resourceSelect.addEventListener('change', () => {
    $('apiId').value = '';
    $('apiPayload').value = JSON.stringify(samplePayloads[resourceSelect.value], null, 2);
    $('apiResults').innerHTML = '';
    setMessage(`Cadastro selecionado: ${resourceSelect.value}. Clique em “Listar registros”.`);
  });

  $('apiCreate').addEventListener('click', async () => {
    try {
      const result = await request('', { method: 'POST', body: JSON.stringify(payload()) });
      if (result?.id) $('apiId').value = result.id;
      setMessage('Registro cadastrado com sucesso.');
      await listRecords();
    } catch (error) { setMessage(error.message, true); }
  });

  $('apiUpdate').addEventListener('click', async () => {
    const id = getId();
    if (!id) return setMessage('Informe o ID do registro que deseja atualizar.', true);
    try {
      await request(`/${encodeURIComponent(id)}`, { method: 'PUT', body: JSON.stringify(payload()) });
      setMessage(`Registro #${id} atualizado com sucesso.`);
      await listRecords();
    } catch (error) { setMessage(error.message, true); }
  });

  $('apiGet').addEventListener('click', async () => {
    const id = getId();
    if (!id) return setMessage('Informe um ID para consultar.', true);
    try {
      const result = await request(`/${encodeURIComponent(id)}`);
      $('apiPayload').value = JSON.stringify(result, null, 2);
      setMessage(`Registro #${id} consultado. Atenção: respostas podem conter campos somente de leitura; ajuste o JSON antes de atualizar.`);
    } catch (error) { setMessage(error.message, true); }
  });

  $('apiDelete').addEventListener('click', async () => {
    const id = getId();
    if (!id) return setMessage('Informe um ID para excluir.', true);
    if (!window.confirm(`Deseja realmente excluir o registro #${id} de ${resourceSelect.value}?`)) return;
    try {
      await request(`/${encodeURIComponent(id)}`, { method: 'DELETE' });
      $('apiId').value = '';
      setMessage(`Registro #${id} excluído com sucesso.`);
      await listRecords();
    } catch (error) { setMessage(error.message, true); }
  });

  $('apiResults').addEventListener('click', async event => {
    const editButton = event.target.closest('[data-api-edit]');
    const selectButton = event.target.closest('[data-api-select]');
    if (selectButton) $('apiId').value = selectButton.dataset.apiSelect;
    if (editButton) {
      $('apiId').value = editButton.dataset.apiEdit;
      try {
        const record = await request(`/${encodeURIComponent(editButton.dataset.apiEdit)}`);
        $('apiPayload').value = JSON.stringify(record, null, 2);
        setMessage('JSON carregado. Alguns campos podem ser somente de leitura; ajuste para o formato de requisição antes de salvar.');
      } catch (error) { setMessage(error.message, true); }
    }
  });

  $('apiPayload').value = JSON.stringify(samplePayloads.items, null, 2);
})();
