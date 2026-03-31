// Theme management
const THEME_KEY = 'springpilot-theme';

// Get the saved theme from localStorage or default to 'light'
function getSavedTheme() {
    return localStorage.getItem(THEME_KEY) || 'light';
}

// Save theme to localStorage
function saveTheme(theme) {
    localStorage.setItem(THEME_KEY, theme);
}

// Apply theme to the document
function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
}

// Toggle between light and dark themes
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    
    applyTheme(newTheme);
    saveTheme(newTheme);
    
    // Add a small animation effect
    document.body.style.transition = 'background-color 0.3s ease';
}

// Initialize theme on page load
function initTheme() {
    const savedTheme = getSavedTheme();
    applyTheme(savedTheme);
    
    // Add event listener to theme toggle button
    const themeToggle = document.getElementById('theme-toggle');
    if (themeToggle) {
        themeToggle.addEventListener('click', toggleTheme);
        
        // Add keyboard support for accessibility
        themeToggle.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                toggleTheme();
            }
        });
    }
}

// Auto-detect system theme preference if no saved preference exists
function detectSystemTheme() {
    if (!localStorage.getItem(THEME_KEY)) {
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            applyTheme('dark');
            saveTheme('dark');
        }
    }
}

// Listen for system theme changes
function watchSystemTheme() {
    if (window.matchMedia) {
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
            // Only auto-switch if user hasn't manually set a preference
            if (!localStorage.getItem(THEME_KEY)) {
                const newTheme = e.matches ? 'dark' : 'light';
                applyTheme(newTheme);
            }
        });
    }
}

// Initialize everything when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        detectSystemTheme();
        initTheme();
        watchSystemTheme();
    });
} else {
    detectSystemTheme();
    initTheme();
    watchSystemTheme();
}
