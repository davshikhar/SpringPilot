// Authentication page logic

document.addEventListener('DOMContentLoaded', () => {
    // Redirect if already authenticated
    if (API.isAuthenticated()) {
        window.location.href = '/dashboard';
        return;
    }

    // Login form handler
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // Signup form handler
    const signupForm = document.getElementById('signup-form');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }
});

async function handleLogin(e) {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('error-message');
    const loginBtn = document.getElementById('login-btn');
    const btnText = loginBtn.querySelector('.btn-text');
    const btnLoader = loginBtn.querySelector('.btn-loader');

    // Hide previous errors
    errorMessage.style.display = 'none';

    // Validate
    if (!username || !password) {
        showError('Please enter both username and password');
        return;
    }

    // Disable button and show loader
    loginBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';

    try {
        // await API.Auth.login(username, password);
        // // Redirect to dashboard
        // window.location.href = '/dashboard';
            const result = await API.Auth.login(username, password);
            console.log('1. Raw result:', result);
            console.log('2. Token in storage:', localStorage.getItem('springpilot-token'));
            console.log('3. isAuthenticated:', API.isAuthenticated());
            window.location.href = '/dashboard';
    } catch (error) {
        showError(error.message || 'Login failed. Please check your credentials.');
        // Re-enable button
        loginBtn.disabled = false;
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
}

async function handleSignup(e) {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const errorMessage = document.getElementById('error-message');
    const successMessage = document.getElementById('success-message');
    const signupBtn = document.getElementById('signup-btn');
    const btnText = signupBtn.querySelector('.btn-text');
    const btnLoader = signupBtn.querySelector('.btn-loader');

    // Hide previous messages
    errorMessage.style.display = 'none';
    successMessage.style.display = 'none';

    // Validate
    if (!username || !password || !confirmPassword) {
        showError('Please fill in all required fields');
        return;
    }

    if (password !== confirmPassword) {
        showError('Passwords do not match');
        return;
    }

    if (password.length < 6) {
        showError('Password must be at least 6 characters long');
        return;
    }

    // Disable button and show loader
    signupBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';

    try {
        await API.Auth.signup(username, password, email);
        
        // Show success message
        successMessage.textContent = 'Account created successfully! Redirecting to login...';
        successMessage.style.display = 'block';

        // Redirect to login after 2 seconds
        setTimeout(() => {
            window.location.href = '/login';
        }, 2000);
    } catch (error) {
        showError(error.message || 'Signup failed. Username might already be taken.');
        // Re-enable button
        signupBtn.disabled = false;
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
}

function showError(message) {
    const errorMessage = document.getElementById('error-message');
    errorMessage.textContent = message;
    errorMessage.style.display = 'block';
}
