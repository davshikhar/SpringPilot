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
    loadCityStatus();

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
    document.getElementById('save-city-prompt-btn').addEventListener('click', handleSaveCityFromPrompt);
    document.getElementById('edit-city-btn').addEventListener('click', openCityModal);
    document.getElementById('close-city-modal').addEventListener('click', closeCityModal);
    document.getElementById('cancel-city-btn').addEventListener('click', closeCityModal);
    document.getElementById('save-city-modal-btn').addEventListener('click', handleSaveCityFromModal);
    document.getElementById('city-modal').addEventListener('click', (e) => {
        if (e.target.id === 'city-modal') closeCityModal();
    });

    // Weather panel listeners
    document.getElementById('weather-btn').addEventListener('click', toggleWeatherPanel);
    document.getElementById('close-weather-panel').addEventListener('click', closeWeatherPanel);
    document.getElementById('weather-add-city-btn').addEventListener('click', handleAddWatchedCity);
    document.getElementById('weather-city-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleAddWatchedCity();
    });
    document.getElementById('current-city-toggle').addEventListener('click', () => {
        toggleCityCard(document.getElementById('weather-current-city'));
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
            document.getElementById('user-greeting').textContent = greeting + ' 👋';
        }
    } catch (error) {
        const username = API.getUsername();
        if (username) {
            document.getElementById('user-greeting').textContent = `Hi, ${username} 👋`;
        }
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
    const entryId = entry.id;

    console.log('entry.id raw:', JSON.stringify(entry.id));
    console.log('entryId resolved:', entryId);

    card.innerHTML = `
        <div class="entry-header">
            <h3 class="entry-title">${escapeHtml(entry.title)}</h3>
            <div class="entry-actions">
                <button class="icon-btn edit" onclick="editEntry('${entryId}')" title="Edit">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                </button>
                <button class="icon-btn delete" onclick="confirmDeleteEntry('${entryId}')" title="Delete">
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
        // document.getElementById('entry-id').value = entry.id;
        document.getElementById('entry-id').value = entry.id;
        document.getElementById('entry-title').value = entry.title;
        document.getElementById('entry-content').value = entry.content || '';
        // editingEntryId = entry.id;
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
    console.log('Deleting entry with ID:', entryId);
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

async function loadCityStatus() {
    try {
        const data = await API.City.getCity();
        if (data && data.city && data.city!=='') {
            document.getElementById('city-prompt').style.display = 'none';
            document.getElementById('edit-city-btn').style.display = 'flex';
        } else {
            document.getElementById('city-prompt').style.display = 'flex';
            document.getElementById('edit-city-btn').style.display = 'none';
        }
    } catch (error) {
        document.getElementById('city-prompt').style.display = 'flex';
    }
}

async function handleSaveCityFromPrompt() {
    const city = document.getElementById('city-input-prompt').value.trim();
    if (!city) return;
    const btn = document.getElementById('save-city-prompt-btn');
    btn.disabled = true;
    btn.textContent = 'Saving...';
    try {
        await API.City.updateCity(city);
        document.getElementById('city-prompt').style.display = 'none';
        document.getElementById('edit-city-btn').style.display = 'flex';
        await loadUserGreeting();
    } catch (error) {
        alert('Failed to save city: ' + error.message);
    } finally {
        btn.disabled = false;
        btn.textContent = 'Save City';
    }
}

function openCityModal() {
    document.getElementById('city-modal').style.display = 'flex';
}

function closeCityModal() {
    document.getElementById('city-modal').style.display = 'none';
    document.getElementById('city-input-modal').value = '';
}

async function handleSaveCityFromModal() {
    const city = document.getElementById('city-input-modal').value.trim();
    if (!city) return;
    const btn = document.getElementById('save-city-modal-btn');
    btn.disabled = true;
    btn.textContent = 'Updating...';
    try {
        await API.City.updateCity(city);
        closeCityModal();
        await loadUserGreeting();
    } catch (error) {
        alert('Failed to update city: ' + error.message);
    } finally {
        btn.disabled = false;
        btn.textContent = 'Update City';
    }
}

let weatherPanelOpen = false;

async function toggleWeatherPanel() {
    const panel = document.getElementById('weather-panel');
    if (weatherPanelOpen) {
        closeWeatherPanel();
    } else {
        panel.style.display = 'block';
        weatherPanelOpen = true;
        await loadWeatherPanel();
    }
}

function closeWeatherPanel() {
    document.getElementById('weather-panel').style.display = 'none';
    weatherPanelOpen = false;
}

async function loadWeatherPanel() {
    await loadCurrentCityWeather();
    await loadListCities();
}

async function loadCurrentCityWeather() {
    const detailsEl = document.getElementById('current-city-details');
    const nameEl = document.getElementById('current-city-name');
    detailsEl.innerHTML = '<div class="weather-loading">Loading...</div>';

    try {
        const weather = await API.User.getMyWeather();
        nameEl.textContent = weather.city;
        detailsEl.innerHTML = renderWeatherDetails(weather);
    } catch (error) {
        detailsEl.innerHTML = '<div class="weather-error">Could not load weather</div>';
    }
}

async function loadListCities() {
    const listEl = document.getElementById('watched-cities-list');
    listEl.innerHTML = '';

    try {
        const data = await API.ListCities.getAll();
        const cities = data.cities || [];

        for (const city of cities) {
            const card = await createWatchedCityCard(city);
            listEl.appendChild(card);
        }
    } catch (error) {
        console.error('Failed to load watched cities:', error);
    }
}

async function createWatchedCityCard(city) {
    const card = document.createElement('div');
    card.className = 'weather-city-card expanded';
    card.id = `city-card-${city}`;

    card.innerHTML = `
        <div class="weather-city-header" onclick="toggleCityCard(this.parentElement)">
            <div class="weather-city-name">
                <span class="weather-city-label">🌍 City</span>
                <span class="weather-city-subtitle">${city}</span>
            </div>
            <span class="weather-chevron">▾</span>
        </div>
        <div class="weather-city-details">
            <div class="weather-loading">Loading...</div>
            <button class="weather-city-remove" onclick="removeWatchedCity('${city}')">
                ✕ Remove
            </button>
        </div>
    `;

    // Fetch weather for this city
    try {
        const weather = await API.Weather.getWeather(city);
        const detailsEl = card.querySelector('.weather-city-details');
        detailsEl.innerHTML = renderWeatherDetails(weather) + `
            <button class="weather-city-remove" onclick="removeWatchedCity('${city}')">
                ✕ Remove
            </button>
        `;
    } catch (error) {
        card.querySelector('.weather-city-details').innerHTML = `
            <div class="weather-error">Could not load weather</div>
            <button class="weather-city-remove" onclick="removeWatchedCity('${city}')">✕ Remove</button>
        `;
    }

    return card;
}

function renderWeatherDetails(weather) {
    if (!weather) return '<div class="weather-error">No data available</div>';
    return `
        <div class="weather-detail-row">
            <span class="weather-detail-label">🌡️ Temperature</span>
            <span class="weather-detail-value">${weather.temperature}°C</span>
        </div>
        <div class="weather-detail-row">
            <span class="weather-detail-label">🤔 Feels Like</span>
            <span class="weather-detail-value">${weather.feelsLike}°C</span>
        </div>
        <div class="weather-detail-row">
            <span class="weather-detail-label">💧 Humidity</span>
            <span class="weather-detail-value">${weather.humidity}%</span>
        </div>
        <div class="weather-detail-row">
            <span class="weather-detail-label">☁️ Condition</span>
            <span class="weather-detail-value">${weather.description}</span>
        </div>
    `;
}

function toggleCityCard(card) {
    card.classList.toggle('expanded');
}

async function handleAddWatchedCity() {
    const input = document.getElementById('weather-city-input');
    const city = input.value.trim();
    if (!city) return;

    const btn = document.getElementById('weather-add-city-btn');
    btn.disabled = true;
    btn.textContent = 'Adding...';

    try {
        await API.ListCities.add(city);
        input.value = '';
        await loadListCities();
    } catch (error) {
        alert('Failed to add city: ' + error.message);
    } finally {
        btn.disabled = false;
        btn.textContent = 'Add';
    }
}

async function removeWatchedCity(city) {
    try {
        await API.ListCities.remove(city);
        const card = document.getElementById(`city-card-${city}`);
        if (card) card.remove();
    } catch (error) {
        alert('Failed to remove city: ' + error.message);
    }
}

// Make functions globally accessible
window.editEntry = editEntry;
window.confirmDeleteEntry = confirmDeleteEntry;
window.toggleCityCard = toggleCityCard;
window.removeWatchedCity = removeWatchedCity;
