<script>
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';

    onMount(() => {
        if (window.location.pathname === '/') {
            goto('/register', { replaceState: true });
        }
    });
</script>

<div class="loading">Loading...</div>

<style>
    .loading {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        color: #00e054;
        font-size: 1.2rem;
    }
</style>
