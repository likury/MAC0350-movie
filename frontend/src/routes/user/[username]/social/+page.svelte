<script lang="ts">
    import { api } from '$lib/api';
    import { page } from '$app/stores';
    import { onMount } from 'svelte';
    import type { UserDto, FollowDto } from '$lib/api';

    let user: UserDto | null = null;
    let followers: FollowDto[] = [];
    let following: FollowDto[] = [];
    let followerUsers: Map<number, UserDto> = new Map(); // Cache user details
    let followingUsers: Map<number, UserDto> = new Map(); // Cache user details
    let loading = true;
    let error = '';

    // Search and follow state
    let searchQuery = '';
    let searchResults: UserDto[] = [];
    let searchLoading = false;
    let searchError = '';
    let followingUser = false;

    // Tab state
    let activeTab: 'followers' | 'following' | 'search' = 'followers';

    async function loadUserDetails(userId: number): Promise<UserDto | null> {
        try {
            const response = await api.getUserById(userId);
            return response;
        } catch (e) {
            console.error(`Failed to load user details for ID ${userId}:`, e);
            return null;
        }
    }

    async function loadSocialData() {
        if (!user) return;
        
        try {
            const [followersData, followingData] = await Promise.all([
                api.getFollowers(user.id),
                api.getFollowing(user.id)
            ]);
            followers = followersData;
            following = followingData;

            // Load user details for followers
            const followerUserIds = followers.map(f => f.followerId);
            const followerUserDetails = await Promise.all(
                followerUserIds.map(loadUserDetails)
            );
            followerUsers = new Map();
            followerUserDetails.forEach((userDetail, index) => {
                if (userDetail) {
                    followerUsers.set(followerUserIds[index], userDetail);
                }
            });

            // Load user details for following
            const followingUserIds = following.map(f => f.followedId);
            const followingUserDetails = await Promise.all(
                followingUserIds.map(loadUserDetails)
            );
            followingUsers = new Map();
            followingUserDetails.forEach((userDetail, index) => {
                if (userDetail) {
                    followingUsers.set(followingUserIds[index], userDetail);
                }
            });
        } catch (e) {
            console.error('Error loading social data:', e);
            error = 'Failed to load social data';
        }
    }

    async function searchUsers() {
        if (!searchQuery.trim()) {
            searchResults = [];
            return;
        }
        
        try {
            searchLoading = true;
            searchError = '';
            searchResults = await api.searchUsers(searchQuery);
        } catch (e) {
            searchError = 'Failed to search users';
            console.error('Error searching users:', e);
        } finally {
            searchLoading = false;
        }
    }

    async function followUser(targetUserId: number) {
        if (!user || followingUser) return;
        
        try {
            followingUser = true;
            await api.followUser(user.id, targetUserId);
            
            // Refresh the social data
            await loadSocialData();
            
            // Remove the followed user from search results or update UI
            searchResults = searchResults.filter(u => u.id !== targetUserId);
        } catch (e) {
            console.error('Error following user:', e);
            // Handle error (could show a toast or error message)
        } finally {
            followingUser = false;
        }
    }

    function isAlreadyFollowing(userId: number): boolean {
        return following.some(f => f.followedId === userId);
    }

    function isCurrentUser(userId: number): boolean {
        return user?.id === userId;
    }

    onMount(async () => {
        try {
            const username = $page.params.username;
            const userResponse = await api.getUserByUsername(username);
            user = userResponse;
            
            if (user) {
                await loadSocialData();
            }
        } catch (e) {
            error = 'User not found';
        } finally {
            loading = false;
        }
    });
</script>

<div class="container">
    {#if loading}
        <div class="loading">Loading...</div>
    {:else if error}
        <div class="error-container">
            <h1>Oops! 😕</h1>
            <p class="error-message">{error}</p>
            <a href="/register" class="button">Create an Account</a>
        </div>
    {:else if user}
        <div class="profile-container">
            <div class="profile-header">
                <h1>{user.username}'s Social</h1>
                <div class="stats">
                    <div class="stat-item">
                        <span class="stat-number">{followers.length}</span>
                        <span class="stat-label">Followers</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-number">{following.length}</span>
                        <span class="stat-label">Following</span>
                    </div>
                </div>
            </div>

            <div class="profile-navigation">
                <a href="/user/{user.username}" class="nav-link">Profile</a>
                <a href="/user/{user.username}/social" class="nav-link active">Social</a>
                <a href="/user/{user.username}/feed" class="nav-link">Feed</a>
            </div>
            
            <div class="profile-content">
                <div class="tabs">
                    <button 
                        class="tab {activeTab === 'followers' ? 'active' : ''}"
                        on:click={() => activeTab = 'followers'}
                    >
                        Followers ({followers.length})
                    </button>
                    <button 
                        class="tab {activeTab === 'following' ? 'active' : ''}"
                        on:click={() => activeTab = 'following'}
                    >
                        Following ({following.length})
                    </button>
                    <button 
                        class="tab {activeTab === 'search' ? 'active' : ''}"
                        on:click={() => activeTab = 'search'}
                    >
                        Find Users
                    </button>
                </div>

                <div class="tab-content">
                    {#if activeTab === 'followers'}
                        <div class="user-list">
                            {#if followers.length === 0}
                                <p class="no-users">No followers yet</p>
                            {:else}
                                {#each followers as follow}
                                    {@const followerUser = followerUsers.get(follow.followerId)}
                                    {#if followerUser}
                                        <div class="user-card">
                                            <div class="user-info">
                                                <div class="username">{followerUser.username}</div>
                                                <div class="email">{followerUser.email}</div>
                                            </div>
                                            <a href="/user/{followerUser.username}" class="view-profile">
                                                View Profile
                                            </a>
                                        </div>
                                    {/if}
                                {/each}
                            {/if}
                        </div>
                    {:else if activeTab === 'following'}
                        <div class="user-list">
                            {#if following.length === 0}
                                <p class="no-users">Not following anyone yet</p>
                            {:else}
                                {#each following as follow}
                                    {@const followedUser = followingUsers.get(follow.followedId)}
                                    {#if followedUser}
                                        <div class="user-card">
                                            <div class="user-info">
                                                <div class="username">{followedUser.username}</div>
                                                <div class="email">{followedUser.email}</div>
                                            </div>
                                            <a href="/user/{followedUser.username}" class="view-profile">
                                                View Profile
                                            </a>
                                        </div>
                                    {/if}
                                {/each}
                            {/if}
                        </div>
                    {:else if activeTab === 'search'}
                        <div class="search-section">
                            <div class="search-input">
                                <input
                                    type="text"
                                    bind:value={searchQuery}
                                    placeholder="Enter exact username..."
                                    on:keydown={(e) => e.key === 'Enter' && searchUsers()}
                                />
                                <button on:click={searchUsers} disabled={searchLoading}>
                                    {searchLoading ? 'Searching...' : 'Find User'}
                                </button>
                            </div>

                            {#if searchError}
                                <div class="search-error">{searchError}</div>
                            {/if}

                            <div class="user-list">
                                {#if searchResults.length === 0 && searchQuery.trim()}
                                    <p class="no-users">User "{searchQuery}" not found</p>
                                {:else if searchResults.length > 0}
                                    {#each searchResults as searchUser}
                                        <div class="user-card">
                                            <div class="user-info">
                                                <div class="username">{searchUser.username}</div>
                                                <div class="email">{searchUser.email}</div>
                                            </div>
                                            <div class="user-actions">
                                                <a href="/user/{searchUser.username}" class="view-profile">
                                                    View Profile
                                                </a>
                                                {#if !isCurrentUser(searchUser.id) && !isAlreadyFollowing(searchUser.id)}
                                                    <button 
                                                        class="follow-button"
                                                        on:click={() => followUser(searchUser.id)}
                                                        disabled={followingUser}
                                                    >
                                                        {followingUser ? 'Following...' : 'Follow'}
                                                    </button>
                                                {:else if isAlreadyFollowing(searchUser.id)}
                                                    <span class="already-following">Following</span>
                                                {:else if isCurrentUser(searchUser.id)}
                                                    <span class="current-user">You</span>
                                                {/if}
                                            </div>
                                        </div>
                                    {/each}
                                {/if}
                            </div>
                        </div>
                    {/if}
                </div>
            </div>
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

    .loading {
        color: #00e054;
        font-size: 1.2rem;
    }

    .error-container {
        background-color: #1a1f2b;
        padding: 2rem;
        border-radius: 8px;
        text-align: center;
        max-width: 400px;
        width: 100%;
    }

    .error-message {
        color: #ff4444;
        margin: 1rem 0;
    }

    .profile-container {
        background-color: #1a1f2b;
        border-radius: 8px;
        width: 100%;
        max-width: 800px;
        overflow: hidden;
    }

    .profile-header {
        background-color: #00e054;
        padding: 2rem;
        text-align: center;
    }

    .profile-header h1 {
        color: #14181f;
        margin: 0 0 1rem 0;
        font-size: 2rem;
    }

    .stats {
        display: flex;
        justify-content: center;
        gap: 2rem;
    }

    .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .stat-number {
        color: #14181f;
        font-size: 1.5rem;
        font-weight: bold;
    }

    .stat-label {
        color: #14181f;
        font-size: 0.9rem;
        opacity: 0.8;
    }

    .profile-navigation {
        display: flex;
        background-color: #14181f;
        border-bottom: 1px solid #2c3440;
    }

    .nav-link {
        flex: 1;
        padding: 1rem;
        text-align: center;
        color: #9ab;
        text-decoration: none;
        font-weight: 500;
        transition: all 0.2s;
        border-bottom: 2px solid transparent;
    }

    .nav-link:hover {
        background-color: #2c3440;
        color: #fff;
    }

    .nav-link.active {
        color: #00e054;
        border-bottom-color: #00e054;
        background-color: #1a1f2b;
    }

    .profile-content {
        padding: 2rem;
    }

    .tabs {
        display: flex;
        border-bottom: 1px solid #2c3440;
        margin-bottom: 1.5rem;
    }

    .tab {
        background: none;
        border: none;
        color: #9ab;
        padding: 1rem 1.5rem;
        cursor: pointer;
        font-size: 1rem;
        border-bottom: 2px solid transparent;
        transition: all 0.2s;
    }

    .tab:hover {
        color: #fff;
        background-color: #2c3440;
    }

    .tab.active {
        color: #00e054;
        border-bottom-color: #00e054;
    }

    .tab-content {
        min-height: 400px;
    }

    .user-list {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .user-card {
        background-color: #14181f;
        border-radius: 8px;
        padding: 1.5rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .user-info {
        flex: 1;
    }

    .username {
        color: #fff;
        font-size: 1.1rem;
        font-weight: 500;
        margin-bottom: 0.25rem;
    }

    .email {
        color: #9ab;
        font-size: 0.9rem;
    }

    .user-actions {
        display: flex;
        gap: 1rem;
        align-items: center;
    }

    .view-profile {
        background-color: transparent;
        color: #9ab;
        border: 1px solid #2c3440;
        padding: 0.5rem 1rem;
        border-radius: 4px;
        text-decoration: none;
        cursor: pointer;
        transition: all 0.2s;
        font-size: 0.9rem;
    }

    .view-profile:hover {
        background-color: #2c3440;
        color: #fff;
    }

    .follow-button {
        background-color: #00e054;
        color: #14181f;
        border: none;
        padding: 0.5rem 1rem;
        border-radius: 4px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.2s;
        font-size: 0.9rem;
    }

    .follow-button:hover:not(:disabled) {
        background-color: #00c04b;
    }

    .follow-button:disabled {
        background-color: #2c3440;
        cursor: not-allowed;
    }

    .already-following {
        color: #00e054;
        font-size: 0.9rem;
        font-weight: 500;
    }

    .current-user {
        color: #9ab;
        font-size: 0.9rem;
    }

    .no-users {
        color: #9ab;
        text-align: center;
        padding: 2rem;
        background-color: #14181f;
        border-radius: 8px;
    }

    .search-section {
        margin-bottom: 1.5rem;
    }

    .search-input {
        display: flex;
        gap: 1rem;
        margin-bottom: 1rem;
    }

    .search-input input {
        flex: 1;
        padding: 0.75rem;
        border: 1px solid #2c3440;
        border-radius: 4px;
        background-color: #1a1f2b;
        color: #fff;
        font-size: 1rem;
    }

    .search-input input:focus {
        outline: none;
        border-color: #00e054;
    }

    .search-input button {
        background-color: #00e054;
        color: #14181f;
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 4px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .search-input button:hover:not(:disabled) {
        background-color: #00c04b;
    }

    .search-input button:disabled {
        background-color: #2c3440;
        cursor: not-allowed;
    }

    .search-error {
        color: #ff4444;
        margin-bottom: 1rem;
    }

    .button {
        display: inline-block;
        background-color: #00e054;
        color: #14181f;
        padding: 0.75rem 1.5rem;
        border-radius: 4px;
        text-decoration: none;
        font-weight: bold;
        margin-top: 1rem;
        transition: background-color 0.2s;
    }

    .button:hover {
        background-color: #00c04b;
    }
</style> 