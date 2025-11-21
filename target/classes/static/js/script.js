const API = "/api/utenti";
let authHeader = null;
let isAdmin = false;
let currentUsername = ""; // mostra chi è loggato
let lastUtenti = [];      // ultimi utenti caricati (per filtri e ordinamento)
let sortMode = "default"; // "default" | "date" | "surname" | "firstname"
let currentPage = 1;
const pageSize = 9;       // numero di utenti per pagina

// Sblocca UI dopo login corretto
function unlockUI() {
    const loginOverlay = document.getElementById("loginOverlay");
    const mainContent = document.getElementById("mainContent");

    if (loginOverlay) {
        loginOverlay.style.display = "none";
    }
    if (mainContent) {
        mainContent.classList.add("active");
    }
}

// scelta layout in base al ruolo
function applyRoleUi() {
    const thId = document.getElementById("thId");
    const thAzioni = document.getElementById("thAzioni");
    const createBtn = document.getElementById("openCreateModalBtn");
    const userInfo = document.getElementById("userInfo");

    if (thId) thId.style.display = isAdmin ? "" : "none";
    if (thAzioni) thAzioni.style.display = isAdmin ? "" : "none";
    if (createBtn) createBtn.style.display = isAdmin ? "" : "none";

    if (userInfo) {
        if (currentUsername) {
            userInfo.textContent = `Loggato come: ${currentUsername} (ruolo: ${isAdmin ? "ADMIN" : "USER"})`;
        } else {
            userInfo.textContent = "";
        }
    }
}

// Logout
function logout() {
    authHeader = null;
    isAdmin = false;
    currentUsername = "";
    lastUtenti = [];
    sortMode = "default";
    currentPage = 1;

    const mainContent = document.getElementById("mainContent");
    if (mainContent) {
        mainContent.classList.remove("active");
    }

    const loginOverlay = document.getElementById("loginOverlay");
    if (loginOverlay) {
        loginOverlay.style.display = "flex";
    }

    document.getElementById("loginStatus").textContent = "";
    document.getElementById("password").value = "";

    const tbody = document.querySelector("#utentiTable tbody");
    if (tbody) {
        tbody.innerHTML = "";
    }
    const tableStatus = document.getElementById("tableStatus");
    if (tableStatus) {
        tableStatus.textContent = "";
    }

    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.value = "";
    }

    const filterFrom = document.getElementById("filterFrom");
    const filterTo = document.getElementById("filterTo");
    if (filterFrom) filterFrom.value = "";
    if (filterTo) filterTo.value = "";

    const paginationInfo = document.getElementById("paginationInfo");
    if (paginationInfo) {
        paginationInfo.textContent = "";
    }

    const createOverlay = document.getElementById("createOverlay");
    if (createOverlay) {
        createOverlay.style.display = "none";
    }

    const editOverlay = document.getElementById("editOverlay");
    if (editOverlay) {
        editOverlay.style.display = "none";
    }

    applyRoleUi();
}

// Carica utenti (ricerca classica)
async function loadUtenti(query) {
    if (!authHeader) {
        document.getElementById("tableStatus").textContent = "Fai prima il login.";
        return;
    }

    let url = API;
    if (query && query.trim() !== "") {
        url = API + "/search?q=" + encodeURIComponent(query.trim());
    }

    const res = await fetch(url, {
        headers: { Authorization: authHeader }
    });

    if (!res.ok) {
        const table = document.querySelector("#utentiTable tbody");
        if (table) table.innerHTML = "";
        document.getElementById("tableStatus").textContent = "Errore: " + res.status;
        return;
    }

    const utenti = await res.json();
    lastUtenti = utenti; // salviamo l'ultimo risultato dal backend
    currentPage = 1;     // ogni nuova ricerca riparte dalla prima pagina
    renderUtenti();      // disegna la tabella applicando filtri + ordinamento + paginazione
}

// Renderizza la tabella applicando filtri (data), ordinamento e paginazione
function renderUtenti() {
    const table = document.querySelector("#utentiTable tbody");
    if (!table) return;

    table.innerHTML = "";

    const from = document.getElementById("filterFrom")?.value;
    const to = document.getElementById("filterTo")?.value;

    // Filtro per date
    let filtered = lastUtenti.filter(u => {
        if (!from && !to) return true;

        if (!u.dataDiNascita) return false;

        const d = u.dataDiNascita; // formato "yyyy-MM-dd"

        if (from && d < from) return false;
        if (to && d > to) return false;

        return true;
    });

    // Ordinamento
    const sorted = [...filtered]; // copia per non modificare lastUtenti

    if (sortMode === "date") {
        sorted.sort((a, b) => {
            const ad = a.dataDiNascita || "";
            const bd = b.dataDiNascita || "";
            return ad.localeCompare(bd); // cronologico (vecchi prima)
        });
    } else if (sortMode === "surname") {
        // alfabetico per cognome, poi nome
        sorted.sort((a, b) => {
            const ac = (a.cognome || "").toLowerCase();
            const bc = (b.cognome || "").toLowerCase();
            if (ac !== bc) return ac.localeCompare(bc);
            const an = (a.nome || "").toLowerCase();
            const bn = (b.nome || "").toLowerCase();
            return an.localeCompare(bn);
        });
    } else if (sortMode === "firstname") {
        // alfabetico per nome, poi cognome
        sorted.sort((a, b) => {
            const an = (a.nome || "").toLowerCase();
            const bn = (b.nome || "").toLowerCase();
            if (an !== bn) return an.localeCompare(bn);
            const ac = (a.cognome || "").toLowerCase();
            const bc = (b.cognome || "").toLowerCase();
            return ac.localeCompare(bc);
        });
    }
    // sortMode "default" → nessun sort, mantiene l'ordine del backend

    // --- PAGINAZIONE ---
    const totalItems = sorted.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));

    // wrap: se vado oltre i limiti, rimbalzo
    if (currentPage > totalPages) {
        currentPage = 1;
    }
    if (currentPage < 1) {
        currentPage = totalPages;
    }

    const startIndex = (currentPage - 1) * pageSize;
    const pageItems = sorted.slice(startIndex, startIndex + pageSize);

    // disegna solo gli elementi della pagina corrente
    pageItems.forEach(u => {
        const tr = document.createElement("tr");

        tr.dataset.id = u.id;
        tr.dataset.nome = u.nome;
        tr.dataset.cognome = u.cognome;
        tr.dataset.cf = u.codiceFiscale;
        tr.dataset.nascita = u.dataDiNascita ?? "";

        let rowHtml = "";

        if (isAdmin) {
            rowHtml += `<td>${u.id}</td>`;
        }

        rowHtml += `
            <td>${u.nome}</td>
            <td>${u.cognome}</td>
            <td>${u.codiceFiscale}</td>
            <td>${u.dataDiNascita ?? ""}</td>
        `;

        if (isAdmin) {
            rowHtml += `
                <td>
                    <button onclick="openEdit(this)">Modifica</button>
                    <button class="delete-btn" onclick="deleteUtente(${u.id})">Elimina</button>
                </td>
            `;
        }

        tr.innerHTML = rowHtml;
        table.appendChild(tr);
    });

    // info paginazione
    const info = document.getElementById("paginationInfo");
    if (info) {
        info.textContent = `Pagina ${currentPage} di ${totalPages} (totale ${totalItems} utenti)`;
    }

    document.getElementById("tableStatus").textContent =
        totalItems + " utenti caricati.";
}

// Elimina utente
async function deleteUtente(id) {
    if (!authHeader) {
        alert("Fai prima il login.");
        return;
    }

    const res = await fetch(API + "/" + id, {
        method: "DELETE",
        headers: { Authorization: authHeader }
    });

    if (res.ok) {
        loadUtenti();
    } else {
        alert("Errore: " + res.status);
    }
}

// Apertura popup edit
function openEdit(button) {
    if (!authHeader || !isAdmin) {
        alert("Solo l'amministratore può modificare utenti.");
        return;
    }

    const tr = button.closest("tr");
    if (!tr) return;

    const id = tr.dataset.id;
    const nome = tr.dataset.nome;
    const cognome = tr.dataset.cognome;
    const cf = tr.dataset.cf;
    const nascita = tr.dataset.nascita;

    document.getElementById("editId").value = id;
    document.getElementById("editNome").value = nome;
    document.getElementById("editCognome").value = cognome;
    document.getElementById("editCodiceFiscale").value = cf;
    document.getElementById("editDataDiNascita").value = nascita || "";

    document.getElementById("editStatus").textContent = "";
    document.getElementById("editOverlay").style.display = "flex";
}

// Debounce
function debounce(func, delay) {
    let timer;
    return function (...args) {
        clearTimeout(timer);
        timer = setTimeout(() => func.apply(this, args), delay);
    };
}

// Inizializzazione event listener
document.addEventListener("DOMContentLoaded", () => {

    // login
    document.getElementById("loginBtn").addEventListener("click", async () => {
        const u = document.getElementById("username").value;
        const p = document.getElementById("password").value;

        authHeader = "Basic " + btoa(u + ":" + p);

        const res = await fetch(API, { headers: { Authorization: authHeader } });

        if (res.status === 200) {
            const username = u.trim().toLowerCase();
            currentUsername = username;
            isAdmin = (username === "admin");

            document.getElementById("loginStatus").textContent = "Login OK!";
            document.getElementById("loginStatus").style.color = "green";

            unlockUI();
            applyRoleUi();
            loadUtenti();
        } else {
            document.getElementById("loginStatus").textContent = "Credenziali errate";
            document.getElementById("loginStatus").style.color = "red";
        }
    });

    // logout
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", logout);
    }

    // nuovo utente
    const openCreateModalBtn = document.getElementById("openCreateModalBtn");
    const closeCreateBtn = document.getElementById("closeCreateBtn");
    const createOverlay = document.getElementById("createOverlay");

    if (openCreateModalBtn && createOverlay) {
        openCreateModalBtn.addEventListener("click", () => {
            if (!authHeader || !isAdmin) {
                document.getElementById("createStatus").textContent = "Solo l'amministratore può creare utenti.";
                document.getElementById("createStatus").style.color = "red";
                return;
            }
            document.getElementById("createStatus").textContent = "";
            document.getElementById("createForm").reset();
            createOverlay.style.display = "flex";
        });
    }

    if (closeCreateBtn && createOverlay) {
        closeCreateBtn.addEventListener("click", () => {
            createOverlay.style.display = "none";
        });
    }

    if (createOverlay) {
        createOverlay.addEventListener("click", (e) => {
            if (e.target === createOverlay) {
                createOverlay.style.display = "none";
            }
        });
    }

    // edit overlay
    const editOverlay = document.getElementById("editOverlay");
    const closeEditBtn = document.getElementById("closeEditBtn");

    if (closeEditBtn && editOverlay) {
        closeEditBtn.addEventListener("click", () => {
            editOverlay.style.display = "none";
        });
    }

    if (editOverlay) {
        editOverlay.addEventListener("click", (e) => {
            if (e.target === editOverlay) {
                editOverlay.style.display = "none";
            }
        });
    }

    // Codice Fiscale sempre uppercase (crea)
    const cfInput = document.getElementById("codiceFiscale");
    if (cfInput) {
        cfInput.addEventListener("input", () => {
            cfInput.value = cfInput.value.toUpperCase();
        });
    }

    // Codice Fiscale sempre uppercase (edit)
    const cfEditInput = document.getElementById("editCodiceFiscale");
    if (cfEditInput) {
        cfEditInput.addEventListener("input", () => {
            cfEditInput.value = cfEditInput.value.toUpperCase();
        });
    }

    // crea utente
    document.getElementById("createForm").addEventListener("submit", async (e) => {
        e.preventDefault();

        if (!authHeader || !isAdmin) {
            document.getElementById("createStatus").textContent = "Solo l'amministratore può creare utenti.";
            document.getElementById("createStatus").style.color = "red";
            return;
        }

        const dto = {
            nome: document.getElementById("nome").value,
            cognome: document.getElementById("cognome").value,
            codiceFiscale: document.getElementById("codiceFiscale").value.toUpperCase(),
            dataDiNascita: document.getElementById("dataDiNascita").value
        };

        const res = await fetch(API, {
            method: "POST",
            headers: {
                Authorization: authHeader,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dto)
        });

        const msg = document.getElementById("createStatus");

        if (res.ok) {
            msg.textContent = "Utente creato!";
            msg.style.color = "green";
            loadUtenti();
            document.getElementById("createForm").reset();
        } else if (res.status === 403) {
            msg.textContent = "Non hai i permessi per creare utenti (serve ADMIN).";
            msg.style.color = "red";
        } else {
            let errorText = "Errore: " + res.status;

            try {
                const errorJson = await res.json();
                if (errorJson.message) {
                    errorText = errorJson.message;
                }
            } catch (e) {
                console.warn("Impossibile leggere il JSON di errore", e);
            }

            msg.textContent = errorText;
            msg.style.color = "red";
        }
    });

    // edit utente
    document.getElementById("editForm").addEventListener("submit", async (e) => {
        e.preventDefault();

        const msg = document.getElementById("editStatus");

        if (!authHeader || !isAdmin) {
            msg.textContent = "Solo l'amministratore può modificare utenti.";
            msg.style.color = "red";
            return;
        }

        const id = document.getElementById("editId").value;

        const dto = {
            nome: document.getElementById("editNome").value,
            cognome: document.getElementById("editCognome").value,
            codiceFiscale: document.getElementById("editCodiceFiscale").value.toUpperCase(),
            dataDiNascita: document.getElementById("editDataDiNascita").value
        };

        const res = await fetch(`${API}/${id}`, {
            method: "PUT",
            headers: {
                Authorization: authHeader,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dto)
        });

        if (res.ok) {
            msg.textContent = "Utente aggiornato correttamente.";
            msg.style.color = "green";
            loadUtenti();
        } else {
            let errorText = "Errore: " + res.status;

            try {
                const errorJson = await res.json();
                if (errorJson.message) {
                    errorText = errorJson.message;
                }
            } catch (e) {
                console.warn("Impossibile leggere il JSON di errore", e);
            }

            msg.textContent = errorText;
            msg.style.color = "red";
        }
    });

    // ricerca utenti live
    const searchInput = document.getElementById("searchInput");

    if (searchInput) {
        searchInput.addEventListener("input", debounce(() => {
            if (!authHeader) {
                document.getElementById("tableStatus").textContent = "Fai prima il login.";
                return;
            }

            const q = searchInput.value.trim();
            loadUtenti(q);  // se vuoto carica tutti
        }, 0));
    }

    // filtri per data
    const filterFrom = document.getElementById("filterFrom");
    const filterTo = document.getElementById("filterTo");

    if (filterFrom) filterFrom.addEventListener("change", () => {
        currentPage = 1;
        renderUtenti();
    });
    if (filterTo) filterTo.addEventListener("change", () => {
        currentPage = 1;
        renderUtenti();
    });

    // ordinamento
    const sortDefaultBtn = document.getElementById("sortDefaultBtn");
    const sortDateBtn = document.getElementById("sortDateBtn");
    const sortNameBtn = document.getElementById("sortNameBtn");
    const sortFirstNameBtn = document.getElementById("sortFirstNameBtn");
    const resetFiltersBtn = document.getElementById("resetFiltersBtn");
    const nextPageBtn = document.getElementById("nextPageBtn");
    const prevPageBtn = document.getElementById("prevPageBtn");

    if (sortDefaultBtn) {
        sortDefaultBtn.addEventListener("click", () => {
            sortMode = "default";
            currentPage = 1;
            renderUtenti();
        });
    }

    if (sortDateBtn) {
        sortDateBtn.addEventListener("click", () => {
            sortMode = "date";
            currentPage = 1;
            renderUtenti();
        });
    }

    if (sortNameBtn) {
        sortNameBtn.addEventListener("click", () => {
            sortMode = "surname";
            currentPage = 1;
            renderUtenti();
        });
    }

    if (sortFirstNameBtn) {
        sortFirstNameBtn.addEventListener("click", () => {
            sortMode = "firstname";
            currentPage = 1;
            renderUtenti();
        });
    }

    // reset filtri (data + ricerca + ordine)
    if (resetFiltersBtn) {
        resetFiltersBtn.addEventListener("click", () => {
            const searchInput = document.getElementById("searchInput");
            const filterFrom = document.getElementById("filterFrom");
            const filterTo = document.getElementById("filterTo");

            if (searchInput) searchInput.value = "";
            if (filterFrom) filterFrom.value = "";
            if (filterTo) filterTo.value = "";

            sortMode = "default";
            currentPage = 1;
            loadUtenti(); // ricarica tutto senza query né filtri
        });
    }

    // paginazione - pagina successiva
    if (nextPageBtn) {
        nextPageBtn.addEventListener("click", () => {
            if (!lastUtenti.length) return;
            currentPage += 1;
            renderUtenti();
        });
    }

    // paginazione - pagina precedente
    if (prevPageBtn) {
        prevPageBtn.addEventListener("click", () => {
            if (!lastUtenti.length) return;
            currentPage -= 1;
            renderUtenti();
        });
    }
});