<script lang="ts">
    import { api } from '$lib/api';

    let username = '';
    let email = '';
    let password = '';
    let error = '';
    let loading = false;
    let registeredUser: { id: number; username: string; email: string } | null = null;

    async function handleSubmit() {
        try {
            loading = true;
            error = '';
            const user = await api.register({ username, email, password });
            registeredUser = user;
        } catch (e) {
            error = 'Registration failed. Please try again.';
        } finally {
            loading = false;
        }
    }
</script>

<div class="container">
    {#if registeredUser}
        <div class="success-container">
            <h1>Registration Successful! 🎉</h1>
            <div class="user-info">
                <p>Welcome to MovieDB, <span class="highlight">{registeredUser.username}</span>!</p>
                <p>Your user ID is: <span class="highlight">{registeredUser.id}</span></p>
                <p>Email: <span class="highlight">{registeredUser.email}</span></p>
            </div>
            <button on:click={() => registeredUser = null} class="secondary-button">
                Register Another User
            </button>
        </div>
    {:else}
        <div class="form-container">
            <h1>Join MovieDB</h1>
            <p class="subtitle">Share your movie journey with the world</p>

            {#if error}
                <div class="error">{error}</div>
            {/if}

            <form on:submit|preventDefault={handleSubmit}>
                <div class="form-group">
                    <label for="username">Username</label>
                    <input
                        type="text"
                        id="username"
                        bind:value={username}
                        required
                        placeholder="Choose a username"
                    />
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input
                        type="email"
                        id="email"
                        bind:value={email}
                        required
                        placeholder="Enter your email"
                    />
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        bind:value={password}
                        required
                        placeholder="Create a password"
                    />
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? 'Creating account...' : 'Create account'}
                </button>
            </form>

            <p class="login-link">
                Already have an account? <a href="/login">Log in</a>
            </p>
        </div>
    {/if}
</div>

<style>
    .container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #14181f;
        color: #fff;
        padding: 1rem;
    }

    .form-container, .success-container {
        background-color: #1a1f2b;
        padding: 2rem;
        border-radius: 8px;
        width: 100%;
        max-width: 400px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    h1 {
        font-size: 2rem;
        margin-bottom: 0.5rem;
        color: #00e054;
    }

    .subtitle {
        color: #9ab;
        margin-bottom: 2rem;
    }

    .form-group {
        margin-bottom: 1.5rem;
    }

    label {
        display: block;
        margin-bottom: 0.5rem;
        color: #9ab;
    }

    input {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid #2c3440;
        border-radius: 4px;
        background-color: #14181f;
        color: #fff;
        font-size: 1rem;
    }

    input:focus {
        outline: none;
        border-color: #00e054;
    }

    button {
        width: 100%;
        padding: 0.75rem;
        background-color: #00e054;
        color: #14181f;
        border: none;
        border-radius: 4px;
        font-size: 1rem;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    button:hover {
        background-color: #00c04b;
    }

    button:disabled {
        background-color: #2c3440;
        cursor: not-allowed;
    }

    .error {
        background-color: rgba(255, 0, 0, 0.1);
        color: #ff4444;
        padding: 0.75rem;
        border-radius: 4px;
        margin-bottom: 1rem;
    }

    .login-link {
        margin-top: 1.5rem;
        text-align: center;
        color: #9ab;
    }

    .login-link a {
        color: #00e054;
        text-decoration: none;
    }

    .login-link a:hover {
        text-decoration: underline;
    }

    .user-info {
        margin: 2rem 0;
        padding: 1.5rem;
        background-color: #14181f;
        border-radius: 4px;
    }

    .user-info p {
        margin: 0.5rem 0;
        color: #9ab;
    }

    .highlight {
        color: #00e054;
        font-weight: bold;
    }

    .secondary-button {
        background-color: #2c3440;
        color: #fff;
    }

    .secondary-button:hover {
        background-color: #3c4450;
    }
</style> 