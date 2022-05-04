const resetButton = document.querySelector("#reset-button");
const loadButton = document.querySelector("#load-button");
const endButton = document.querySelector("#end-button");
const chessBoard = document.querySelector("table");
const whiteScore = document.querySelector("#white-score");
const blackScore = document.querySelector("#black-score");

resetButton.addEventListener("click", onClickResetButton);
loadButton.addEventListener("click", onClickLoadButton);
endButton.addEventListener("click", onClickEndButton);
chessBoard.addEventListener("click", onClickBoard);

const urls = location.href.split('/');
const gameId = urls[urls.length - 1];

async function onClickEndButton () {
    const response = await fetch(`/endGame/${gameId}`);
    if (response.ok) {
        removeAllPiece();
    }
}

async function onClickResetButton () {
    const response = await fetch(`/reset/${gameId}`);
    const data = await response.json();

    if (response.ok) {
        loadBoard(data);
        return;
    }

    alert(JSON.stringify(data));
}

function loadBoard (data) {
    removeAllPiece();
    Object.entries(data.positionsAndPieces).forEach(([key, value]) => {
        const block = document.getElementById(key.toLowerCase());
        block.appendChild(createPieceImage(value));
    });
    whiteScore.innerText = data.whiteScore.WHITE;
    blackScore.innerText = data.blackScore.BLACK;
    const result = data.result;
    if (result !== "EMPTY") {
        alert(result);
    }
}

function removeAllPiece () {
    chessBoard.querySelectorAll(".piece").forEach(e => e.remove());
}

function createPieceImage ({color, name}) {
    const image = document.createElement("img");
    image.src = `/images/pieces/${color}/${color}-${name}.svg`;
    image.width = 90;
    image.height = 90;
    image.classList.add("piece");
    return image;
}

async function onClickLoadButton () {
    const response = await fetch(`/load/${gameId}`);
    const data = await response.json();

    if (response.ok) {
        loadBoard(data);
        return;
    }

    alert(JSON.stringify(data));
}

function onClickBoard ({target: {classList, id, parentNode}}) {
    if (!hasFirstSelected() && isPiece(id)) {
        classList.toggle("first-selected");
        return;
    }

    if (hasFirstSelected() && !hasSecondSelected()) {
        classList.add("second-selected");
        onClickPiece(id);
        return;
    }
}

function isPiece(id) {
    if (id === "") {
        return true;
    }
    alert("기물을 선택해 주세요.");
    return false;
}

function hasFirstSelected () {
    return chessBoard.querySelector(".first-selected") !== null;
}

function hasSecondSelected () {
    return chessBoard.querySelector(".second-selected") !== null;
}

async function onClickPiece (id) {
    const from = chessBoard.querySelector(".first-selected").parentNode.id;
    const to = getSecondSelectedId(id);

    removeSelected();

    const response = await fetch(`/move/${gameId}`, {
        method: "put",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({from: from, to: to})
    });
    const data = await response.json();
    if (response.ok) {
        movePiece(from, to);
        loadBoard(data);
        return;
    }

    alert(JSON.stringify(data));
}

function getSecondSelectedId (id) {
    if (id === "") {
        return chessBoard.querySelector(".second-selected").parentNode.id;
    }
    return id;
}

function removeSelected () {
    chessBoard.querySelector(".first-selected").classList.remove("first-selected");
    chessBoard.querySelector(".second-selected").classList.remove("second-selected");
}

function movePiece (from, to) {
    const fromPiece = document.getElementById(from).firstElementChild;
    const toPiece = document.getElementById(to).firstElementChild;
    if (toPiece !== null) {
        toPiece.remove();
    }
    fromPiece.remove();
    document.getElementById(to).appendChild(fromPiece);
}
