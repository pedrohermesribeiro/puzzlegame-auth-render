let currentImage = null;
let pieces = [];
let piecePositions = [];
let availablePieces = [];
let corretas = 0;
let total = 0;
let selectedPieceIndex = null;
let selectedPieceIndexAnt = null;
let imageAspectRatio = 1;
let startTime = null;
let endTime = null;
let tempoTotalSegundos = 0;
let percentualAcertos = 0;
let usuarioLogado = null;


let token = localStorage.getItem('jwt'); // Armazenar token JWT

document.getElementById("corretas").textContent = "Certas: " + corretas;
document.getElementById("total").textContent = "Tentativas: " + total;
percentualAcertos = total == 0 ? 0 : Math.round((corretas / total) * 100);
document.getElementById("percentual").textContent = "Percentual: " + percentualAcertos + "%";
document.getElementById("tempototal").textContent = "Tempo total: 0 minutos";


// Verificar autenticação ao carregar
/*window.onload = () => {
    token = localStorage.getItem('jwt');
    if (token) {
        showPuzzle();
        loadImageLibrary();
    } else {
        showLogin();
    }
};*/

console.log("token antes do login: ", token)

// Mostrar tela de login
function showLogin() {
    document.getElementById('auth-section').style.display = 'block';
    document.getElementById('login-form').style.display = 'block';
    document.getElementById('register-form').style.display = 'none';
    document.getElementById('puzzle').style.display = 'none';
}

// Mostrar tela de cadastro
function showRegister() {
    document.getElementById('login-form').style.display = 'none';
    document.getElementById('register-form').style.display = 'block';
}

// Mostrar jogo
function showPuzzle() {
    document.getElementById('auth-section').style.display = 'none';
    document.getElementById('puzzle').style.display = 'block';
}

// Cadastro
async function register() {
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const consent = document.getElementById('consent').checked;
    console.log("Consent: ",consent);

    if (!consent) {
        alert('Você deve concordar com a coleta de dados.');
        return;
    }

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password,consent})
        });
        const result = await response.text();
        if (response.ok) {
        token = result; // usa variável global
            if (token) {
                document.getElementById('login-form').style.display = 'block';
                document.getElementById('register-form').style.display = 'none';
                localStorage.setItem('jwt', token); // token = response do back
                token = localStorage.getItem('jwt'); // Atualiza variável global após login
                console.log("token após login: ", token)
            } else {
                showLogin();
            }
        }
    } catch (error) {
        console.error('Erro ao cadastrar:', error);
        alert('Falha ao cadastrar. Tente novamente.');
    }
}

// Login
async function login() {
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value.trim();

    if (!email || !password) {
        alert("Preencha o e-mail e a senha.");
        return;
    }

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            usuarioLogado = await response.json();
            console.log("Usuário logado:", usuarioLogado);
            token = usuarioLogado.token; // usa variável global
            if (token) {
                showPuzzle();
                localStorage.setItem('jwt', token);
                //token = localStorage.getItem('jwt');
                loadImageLibrary();
                document.getElementById('auth-section').style.display = 'none';
                document.getElementById('puzzle').style.display = 'block';
                console.log("token após login: ", token)
            } else {
                showLogin();
            }
        } else {
            console.warn("Falha ao obter dados do usuário logado.");
        }
        if (response.ok) {


        } else {
            //alert(result);
        }
    } catch (error) {
        console.error('Erro ao logar:', error);
        alert('Falha ao logar. Tente novamente.');
    }
}


// Excluir conta
async function deleteAccount() {
    if (!confirm('Tem certeza que deseja excluir sua conta? Isso não pode ser desfeito.')) return;

    try {
        const response = await fetch('/api/auth/delete', {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const result = await response.text();
        if (response.ok) {
            localStorage.removeItem('token');
            token = null;
            alert(result);
            showLogin();
        } else {
            alert(result);
        }
    } catch (error) {
        console.error('Erro ao excluir conta:', error);
        alert('Falha ao excluir conta. Tente novamente.');
    }
}

// Load images from backend
async function loadImageLibrary() {

     //token = localStorage.getItem('jwt');
    try {
        const response = await fetch('/api/puzzle/images', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                //'Authorization': 'Bearer ' + localStorage.getItem('jwt')
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const images = await response.json();
        console.log('Images received:', images);
        const select = document.getElementById('image-select');
        select.innerHTML = '<option value="">Selecione uma imagem</option>';
        images.forEach(image => {
            const option = document.createElement('option');
            option.value = image.path;
            option.textContent = image.name;
            select.appendChild(option);
            console.log('Added image:', image.name);
        });
    } catch (error) {
        console.error('Error loading image library:', error);
        alert('Falha ao carregar a biblioteca de imagens. Tente novamente.');
    }
}

// Process image (library or uploaded)
function processImage(img, cuts) {
    const canvas = document.getElementById('puzzle-canvas');
    const ctx = canvas.getContext('2d');
    const gridSize = Math.sqrt(cuts);
    
    imageAspectRatio = img.width / img.height;
    
    canvas.width = img.width;
    canvas.height = img.height;
    ctx.drawImage(img, 0, 0);
    
    pieces = [];
    for (let i = 0; i < gridSize; i++) {
        for (let j = 0; j < gridSize; j++) {
            const pieceCanvas = document.createElement('canvas');
            pieceCanvas.width = img.width / gridSize;
            pieceCanvas.height = img.height / gridSize;
            const pieceCtx = pieceCanvas.getContext('2d');
            pieceCtx.drawImage(
                img,
                j * (img.width / gridSize),
                i * (img.height / gridSize),
                img.width / gridSize,
                img.height / gridSize,
                0,
                0,
                img.width / gridSize,
                img.height / gridSize
            );
            pieces.push({ dataUrl: pieceCanvas.toDataURL(), correctIndex: i * gridSize + j });
        }
    }
    initializePuzzle(cuts);
}

// Initialize puzzle
function initializePuzzle(cuts) {
    piecePositions = Array(cuts).fill(null);
    availablePieces = Array.from({ length: cuts }, (_, i) => i);
    selectedPieceIndex = null;
    shuffle(availablePieces);
    
    const boardContainer = document.getElementById('puzzle-board-container');
    boardContainer.style.aspectRatio = imageAspectRatio;
    
    if (imageAspectRatio >= 1) {
        boardContainer.style.width = '400px';
        boardContainer.style.height = `${400 / imageAspectRatio}px`;
    } else {
        boardContainer.style.width = `${400 * imageAspectRatio}px`;
        boardContainer.style.height = '400px';
    }
    
    renderBoard(cuts);
    renderPieceColumn(cuts);
}

// Render puzzle board
function renderBoard(cuts) {
    const board = document.getElementById('puzzle-board');
    board.innerHTML = '';
    board.style.display = 'grid';
    board.style.gridTemplateColumns = `repeat(${Math.sqrt(cuts)}, 1fr)`;
    board.style.gridTemplateRows = `repeat(${Math.sqrt(cuts)}, 1fr)`;

    for (let i = 0; i < cuts; i++) {
        const cell = document.createElement('div');
        cell.className = 'puzzle-cell';
        cell.dataset.index = i;
        cell.addEventListener('dragover', dragOver);
        cell.addEventListener('drop', (e) => dropOnBoard(e, i));
        cell.addEventListener('click', () => handleBoardCellClick(i));
        if (piecePositions[i] !== null) {
            cell.style.backgroundImage = `url(${pieces[piecePositions[i]].dataUrl})`;
            cell.draggable = true;
            cell.addEventListener('dragstart', (e) => dragStart(e, i));
            if (piecePositions[i] === i) {
                cell.classList.add('glow-effect');
                setTimeout(() => {
                    cell.classList.remove('glow-effect');
                }, 2000);
            } else {
                cell.classList.add('glow-effect1');
                setTimeout(() => {
                    cell.classList.remove('glow-effect1');
                }, 2000);
            }
        }
        board.appendChild(cell);
    }

    if (availablePieces.length === cuts) {
        total = 0;
    } else {
        total = total + 1;
    }
    corretas = 0;
    renderProgress(corretas, total);
}

// Handle click on board cell to place selected piece
function handleBoardCellClick(cellIndex) {
    if (selectedPieceIndex === null && piecePositions[cellIndex] !== null) {
        selectedPieceIndexAnt = cellIndex;
        const cell = document.getElementById('puzzle-board');
        const cellDiv = cell.children[cellIndex];
        cellDiv.classList.add('selected');
        return;
    } 

    if (selectedPieceIndex === null && piecePositions[cellIndex] === null && selectedPieceIndexAnt !== null) {
        piecePositions[cellIndex] = piecePositions[selectedPieceIndexAnt];
        piecePositions[selectedPieceIndexAnt] = null;
        selectedPieceIndexAnt = null;
    } else if (piecePositions[cellIndex] === null) {
        piecePositions[cellIndex] = selectedPieceIndex;
        availablePieces = availablePieces.filter(p => p !== selectedPieceIndex);
    } else {
        availablePieces.push(piecePositions[cellIndex]);
        piecePositions[cellIndex] = selectedPieceIndex;
        availablePieces = availablePieces.filter(p => p !== selectedPieceIndex);
    }

    selectedPieceIndex = null;
    const cuts = parseInt(document.getElementById('cuts').value);
    renderBoard(cuts);
    renderPieceColumn(cuts);
    verifyPuzzle(cuts);
}

// Render piece column
function renderPieceColumn(cuts) {
    const column = document.getElementById('piece-column');
    column.innerHTML = '';
    const gridSize = Math.sqrt(cuts);
    const pieceSize = 400 / gridSize;
    availablePieces.forEach((pieceIndex) => {
        const pieceElement = document.createElement('div');
        pieceElement.className = 'puzzle-piece';
        pieceElement.style.width = `${pieceSize}px`;
        pieceElement.style.minHeight = `${pieceSize}px`;
        pieceElement.style.margin = '1% 0';
        pieceElement.style.backgroundImage = `url(${pieces[pieceIndex].dataUrl})`;
        pieceElement.draggable = true;
        pieceElement.dataset.pieceIndex = pieceIndex;
        pieceElement.addEventListener('dragstart', (e) => dragStart(e, pieceIndex, true));
        pieceElement.addEventListener('click', () => handlePieceClick(pieceIndex));
        if (pieceIndex === selectedPieceIndex) {
            pieceElement.classList.add('selected');
        }
        column.appendChild(pieceElement);
    });
}

// Handle piece click to select/deselect
function handlePieceClick(pieceIndex) {
    if (selectedPieceIndex === pieceIndex) {
        selectedPieceIndex = null;
    } else {
        selectedPieceIndex = pieceIndex;
    }
    const cuts = parseInt(document.getElementById('cuts').value);
    renderPieceColumn(cuts);
}

// Drag-and-drop functions
function dragStart(e, index, fromColumn = false) {
    e.dataTransfer.setData('text', JSON.stringify({ index, fromColumn }));
    selectedPieceIndex = null;
    const cuts = parseInt(document.getElementById('cuts').value);
    renderPieceColumn(cuts);
}

function dragOver(e) {
    e.preventDefault();
}

async function dropOnBoard(e, cellIndex) {
    e.preventDefault();
    const data = JSON.parse(e.dataTransfer.getData('text'));
    const { index, fromColumn } = data;

    if (fromColumn) {
        if (piecePositions[cellIndex] === null) {
            piecePositions[cellIndex] = index;
            availablePieces = availablePieces.filter(p => p !== index);
        } else {
            availablePieces.push(piecePositions[cellIndex]);
            piecePositions[cellIndex] = index;
            availablePieces = availablePieces.filter(p => p !== index);
        }
    } else {
        if (piecePositions[cellIndex] === null) {
            piecePositions[cellIndex] = piecePositions[index];
            piecePositions[index] = null;
        } else {
            [piecePositions[cellIndex], piecePositions[index]] = [piecePositions[index], piecePositions[cellIndex]];
        }
    }

    const cuts = parseInt(document.getElementById('cuts').value);
    renderBoard(cuts);
    renderPieceColumn(cuts);
    verifyPuzzle(cuts);
}

// Verify puzzle
async function verifyPuzzle(cuts) {
    try {
        const response = await fetch('/api/puzzle/verify', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ piecePositions: piecePositions.map(p => p !== null ? p : -1), cuts })
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const result = await response.text();
        if (result === 'Correto!') {
            endTime = Date.now(); 
            tempoTotalSegundos = Math.floor((endTime - startTime) / 1000);

            renderProgress(0,total);
            let tempo = tempoTotalSegundos / 60;
            let minutos = Math.round(tempo);
            let cortes = parseInt(document.getElementById('cuts').value);

            document.getElementById("tempototal").textContent = "Tempo: " + minutos + " minutos";
            let imageSelect = document.getElementById('image-select').value ? document.getElementById('image-select').value : null;
            if(imageSelect !== null){
                imageSelect = imageSelect.substring(15,imageSelect.length - 4);
            }else{
                 imageSelect = "Imagem capturada";
            }
  
            // Salva estatísticas no back-end
            try {
                await fetch('/api/auth/save-game-stats', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        percentualAcertos: percentualAcertos,
                        tempoTotalJogado: minutos,
                        totalCortes: cortes,
                        image: imageSelect
                    })
                });

                console.log("Usuário logado: ",usuarioLogado);
                if (!(usuarioLogado && usuarioLogado.premium)) {
                    alert("Seu progresso não foi salvo. Apenas usuários premium têm acesso ao histórico.");
                }else{
                    alert(
                        `Parabéns! Você montou o puzzle!\n` +
                        `Tempo total: ${minutos} minutos\n` + 
                        `Percentual de acertos: ${percentualAcertos}%\n` +
                        `Tentativas: ${total}`
                    );
                }

            } catch (error) {
                console.error("Erro ao salvar estatísticas:", error);
            }
        }


    } catch (error) {
        console.error('Error verifying puzzle:', error);
    }
}

// Render progress
function renderProgress(corretas, total) {
    corretas = 0;
    for (let i = 0; i < piecePositions.length; i++) {
        if (piecePositions[i] === i) corretas += 1;
    }
    document.getElementById("corretas").textContent = "Certas: " + corretas;
    document.getElementById("total").textContent = "Tentativas: " + total;
    percentualAcertos = total == 0 ? 0 : Math.round((corretas / total) * 100);
    document.getElementById("percentual").textContent = "Percentual: " + percentualAcertos + "%";
}

// Shuffle array
function shuffle(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
}

// Start puzzle
function startPuzzle() {
    const cuts = parseInt(document.getElementById('cuts').value);
    const imageSelect = document.getElementById('image-select').value;
    const imageUpload = document.getElementById('image-upload').files[0];
    const previewImage = document.getElementById('preview-image');
    document.getElementById("tempototal").textContent = "Tempo total: 0 minutos";

    if (imageUpload) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const img = new Image();
            img.src = e.target.result;
            img.onload = () => {
                 currentImage = img;
                previewImage.src = img.src;
                previewImage.style.display = 'block';
                processImage(img, cuts);
            };
        };
        reader.readAsDataURL(imageUpload);
    } else if (imageSelect) {
        const img = new Image();
        img.src = imageSelect;
        img.onload = () => {
            currentImage = img;
            previewImage.src = img.src;
            previewImage.style.display = 'block';
            processImage(img, cuts);
        };
        img.onerror = () => alert('Falha ao carregar a imagem da biblioteca.');
    } else {
        alert('Selecione ou carregue uma imagem!');
    }
    startTime = Date.now(); // marca o início
}

// Event listeners
document.getElementById('image-select').addEventListener('change', () => {
    if (document.getElementById('image-select').value) {
        document.getElementById('image-upload').value = '';
    }
});
document.getElementById('image-upload').addEventListener('click',() =>{
    if(!usuarioLogado.premium){
        alert('O upload de arquivo é um recurso premium');
    }
    
})
document.getElementById('btn-image-upload').addEventListener('change', () => {
    if (document.getElementById('image-upload').files[0]) {
        document.getElementById('image-select').value = '';
    }
});
document.getElementById('cuts').addEventListener('change', () => {
    if (currentImage) {
        startPuzzle();
    }
}); 
document.getElementById('login-button').addEventListener('click', login);
document.getElementById('showRegister').addEventListener('click', showRegister);
document.getElementById('register-button').addEventListener('click', register);
document.getElementById('showLogin').addEventListener('click', showLogin);
document.getElementById('delete-account-button').addEventListener('click', deleteAccount);
document.getElementById('start-puzzle-button').addEventListener('click', startPuzzle);
document.getElementById('delete-conta-button').addEventListener('click', startPuzzle);




