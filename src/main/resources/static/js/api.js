// API Client for SpringPilot Backend

const API_BASE_URL = '';  // Empty string means same origin

// Token management
const TOKEN_KEY = 'springpilot-token';
const USERNAME_KEY = 'springpilot-username';

function saveToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function removeToken() {
    localStorage.removeItem(TOKEN_KEY);
}

function saveUsername(username) {
    localStorage.setItem(USERNAME_KEY, username);
}

function getUsername() {
    return localStorage.getItem(USERNAME_KEY);
}

function removeUsername() {
    localStorage.removeItem(USERNAME_KEY);
}

function isAuthenticated() {
    return !!getToken();
}

function logout() {
    removeToken();
    removeUsername();
    window.location.href = '/login';
}

// HTTP request helper
async function request(url, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers,
    };

    try {
        const response = await fetch(API_BASE_URL + url, config);
        
        // Handle 401 Unauthorized
        if (response.status === 401) {
            logout();
            throw new Error('Session expired. Please login again.');
        }

        // Handle no content responses
        if (response.status === 204) {
            return null;
        }

        // Try to parse JSON response
        const contentType = response.headers.get('content-type');
        let data;
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {
            throw new Error(data.message || data || 'Request failed');
        }

        return data;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

// Authentication API
const AuthAPI = {
    async signup(username, password, email) {
        const payload = { username, password };
        if (email) {
            payload.email = email;
        }
        return await request('/public/signup', {
            method: 'POST',
            body: JSON.stringify(payload),
        });
    },

    async login(username, password) {
        const data = await request('/public/login', {
            method: 'POST',
            body: JSON.stringify({ username, password }),
        });
        const token = data.token;
        saveToken(token);
        saveUsername(username);
        return token;
    },

    logout() {
        logout();
    }
};

// Journal API
const JournalAPI = {
    async getAll() {
        return await request('/journal');
    },

    async getById(id) {
        return await request(`/journal/id/${id}`);
    },

    async create(title, content) {
        return await request('/journal', {
            method: 'POST',
            body: JSON.stringify({ title, content }),
        });
    },

    async update(id, title, content) {
        return await request(`/journal/id/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ title, content }),
        });
    },

    async delete(id) {
        return await request(`/journal/id/${id}`, {
            method: 'DELETE',
        });
    }
};

// User API
const UserAPI = {
    async getGreeting() {
        return await request('/user');
    },

    async update(username, password) {
        return await request('/user', {
            method: 'PUT',
            body: JSON.stringify({ username, password }),
        });
    },

    async delete() {
        return await request('/user', {
            method: 'DELETE',
        });
    }
};

// Admin API
const AdminAPI = {
    async getAllUsers() {
        return await request('/admin/all-users');
    },

    async createAdmin(username, password) {
        return await request('/admin/create-admin', {
            method: 'POST',
            body: JSON.stringify({ username, password }),
        });
    },

    async clearCache() {
        return await request('/admin/clear-app-cache');
    }
};

// Export for use in other files
window.API = {
    Auth: AuthAPI,
    Journal: JournalAPI,
    User: UserAPI,
    Admin: AdminAPI,
    isAuthenticated,
    getUsername,
    logout,
};
