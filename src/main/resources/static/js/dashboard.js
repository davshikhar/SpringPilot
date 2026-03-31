// Dashboard page logic

let currentEntries = [];
let editingEntryId = null;

document.addEventListener('DOMContentLoaded', () => {

    console.log('4. Token on dashboard load:', localStorage.getItem('springpilot-token'));
    console.log('5. isAuthenticated:', API.isAuthenticated());
    // Check authentication
    if (!API.isAuthenticated()) {
        window.location.href = '/login';
        return;
    }

    // Initialize dashboard
    initDashboard();
    loadUserGreeting();
    loadEntries();

    // Event listeners
    document.getElementById('logout-btn').addEventListener('click', handleLogout);
    document.getElementById('new-entry-btn').addEventListener('click', () => openEntryModal());
    document.getElementById('close-modal').addEventListener('click', closeEntryModal);
    document.getElementById('cancel-btn').addEventListener('click', closeEntryModal);
    document.getElementById('entry-form').addEventListener('submit', handleSaveEntry);
    document.getElementById('close-delete-modal').addEventListener('click', closeDeleteModal);
    document.getElementById('cancel-delete-btn').addEventListener('click', closeDeleteModal);

    // Close modals when clicking outside
    document.getElementById('entry-modal').addEventListener('click', (e) => {
        if (e.target.id === 'entry-modal') {
            closeEntryModal();
        }
    });

    document.getElementById('delete-modal').addEventListener('click', (e) => {
        if (e.target.id === 'delete-modal') {
            closeDeleteModal();
        }
    });
});

function initDashboard() {
    const username = API.getUsername();
    if (username) {
        document.getElementById('user-greeting').textContent = `Welcome, ${username}!`;
    }
}

async function loadUserGreeting() {
    try {
        const greeting = await API.User.getGreeting();
        if (greeting) {
            document.getElementById('user-greeting').textContent = greeting;
        }
    } catch (error) {
        console.error('Failed to load greeting:', error);
    }
}

async function loadEntries() {
    const loading = document.getElementById('loading');
    const emptyState = document.getElementById('empty-state');
    const entriesGrid = document.getElementById('entries-grid');

    // Show loading
    loading.style.display = 'block';
    emptyState.style.display = 'none';
    entriesGrid.innerHTML = '';

    try {
        const entries = await API.Journal.getAll();
        currentEntries = entries || [];

        loading.style.display = 'none';

        if (currentEntries.length === 0) {
            emptyState.style.display = 'block';
        } else {
            renderEntries();
        }
    } catch (error) {
        console.error('Failed to load entries:', error);
        loading.style.display = 'none';
        emptyState.style.display = 'block';
    }
}

function renderEntries() {
    const entriesGrid = document.getElementById('entries-grid');
    entriesGrid.innerHTML = '';

    currentEntries.forEach(entry => {
        const card = createEntryCard(entry);
        entriesGrid.appendChild(card);
    });
}

function createEntryCard(entry) {
    const card = document.createElement('div');
    card.className = 'entry-card';
    
    // Format date
    const date = entry.date ? new Date(entry.date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    }) : 'No date';

    // Sentiment badge
    const sentimentBadge = entry.sentiment 
        ? `<span class="entry-sentiment sentiment-${entry.sentiment}">${entry.sentiment}</span>`
        : '';

    card.innerHTML = `
        <div class="entry-header">
            <h3 class="entry-title">${escapeHtml(entry.title)}</h3>
            <div class="entry-actions">
                <button class="icon-btn edit" onclick="editEntry('${entry.id}')" title="Edit">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                </button>
                <button class="icon-btn delete" onclick="confirmDeleteEntry('${entry.id}')" title="Delete">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    </svg>
                </button>
            </div>
        </div>
        <div class="entry-content">${escapeHtml(entry.content || 'No content')}</div>
        <div class="entry-footer">
            <span class="entry-date">${date}</span>
            ${sentimentBadge}
        </div>
    `;

    return card;
}

function openEntryModal(entry = null) {
    const modal = document.getElementById('entry-modal');
    const modalTitle = document.getElementById('modal-title');
    const entryForm = document.getElementById('entry-form');
    
    if (entry) {
        // Edit mode
        modalTitle.textContent = 'Edit Journal Entry';
        document.getElementById('entry-id').value = entry.id;
        document.getElementById('entry-title').value = entry.title;
        document.getElementById('entry-content').value = entry.content || '';
        editingEntryId = entry.id;
    } else {
        // Create mode
        modalTitle.textContent = 'New Journal Entry';
        entryForm.reset();
        document.getElementById('entry-id').value = '';
        editingEntryId = null;
    }

    modal.style.display = 'flex';
}

function closeEntryModal() {
    const modal = document.getElementById('entry-modal');
    modal.style.display = 'none';
    document.getElementById('entry-form').reset();
    editingEntryId = null;
}

async function handleSaveEntry(e) {
    e.preventDefault();

    const title = document.getElementById('entry-title').value.trim();
    const content = document.getElementById('entry-content').value.trim();
    const saveBtn = document.getElementById('save-btn');
    const btnText = saveBtn.querySelector('.btn-text');
    const btnLoader = saveBtn.querySelector('.btn-loader');

    if (!title) {
        alert('Please enter a title');
        return;
    }

    // Disable button and show loader
    saveBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';

    try {
        if (editingEntryId) {
            // Update existing entry
            await API.Journal.update(editingEntryId, title, content);
        } else {
            // Create new entry
            await API.Journal.create(title, content);
        }

        // Close modal and reload entries
        closeEntryModal();
        await loadEntries();
    } catch (error) {
        console.error('Failed to save entry:', error);
        alert('Failed to save entry: ' + error.message);
    } finally {
        // Re-enable button
        saveBtn.disabled = false;
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
}

function editEntry(entryId) {
    const entry = currentEntries.find(e => e.id === entryId);
    if (entry) {
        openEntryModal(entry);
    }
}

let deleteEntryId = null;

function confirmDeleteEntry(entryId) {
    deleteEntryId = entryId;
    document.getElementById('delete-modal').style.display = 'flex';
    
    // Set up confirm button handler
    document.getElementById('confirm-delete-btn').onclick = async () => {
        await handleDeleteEntry(deleteEntryId);
    };
}

function closeDeleteModal() {
    document.getElementById('delete-modal').style.display = 'none';
    deleteEntryId = null;
}

async function handleDeleteEntry(entryId) {
    try {
        await API.Journal.delete(entryId);
        closeDeleteModal();
        await loadEntries();
    } catch (error) {
        console.error('Failed to delete entry:', error);
        alert('Failed to delete entry: ' + error.message);
    }
}

function handleLogout() {
    if (confirm('Are you sure you want to logout?')) {
        API.logout();
    }
}

// Utility function to escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Make functions globally accessible
window.editEntry = editEntry;
window.confirmDeleteEntry = confirmDeleteEntry;
