const videoGrid = document.getElementById('videoGrid');
const mainPlayer = document.getElementById('mainPlayer');
const mainLayout = document.querySelector('.main-layout');
const watchLayout = document.getElementById('watchLayout');

let allVideosData = [];

async function fetchVideos() {
    try {
        const response = await fetch('/api/videos');
        allVideosData = await response.json();
        renderVideos(allVideosData);
    } catch (error) {
        console.error('Error fetching videos:', error);
    }
}

function renderVideos(videos) {
    videoGrid.innerHTML = '';

    videos.forEach(video => {
        const card = document.createElement('div');
        card.className = 'video-card';

        const thumb = `https://picsum.photos/seed/${video.id}/400/225`;
        const avatar = `https://api.dicebear.com/7.x/avataaars/svg?seed=${video.nodeId}`;

        card.innerHTML = `
            <div class="thumbnail-container">
                <img src="${thumb}" alt="thumbnail">
                <span class="duration">12:30</span>
            </div>
            <div class="video-details">
                <img src="${avatar}" class="channel-avatar">
                <div class="video-info">
                    <h3 class="video-title">${video.title}</h3>
                    <div class="video-meta">
                        <div>${video.nodeId} Node <i class="fas fa-check-circle" style="font-size: 10px;"></i></div>
                        <div>125K views • 2 days ago</div>
                    </div>
                </div>
                <i class="fas fa-ellipsis-v" style="font-size: 14px; color: #aaa; margin-top: 5px;"></i>
            </div>
        `;

        card.onclick = () => openPlayer(video);
        videoGrid.appendChild(card);
    });
}

function openPlayer(video) {
    mainLayout.style.display = 'none';
    watchLayout.style.display = 'flex';

    document.getElementById('watchTitle').innerText = video.title;
    document.getElementById('watchDesc').innerText = video.description || "Streaming from " + video.nodeId;
    document.getElementById('watchChannelName').innerText = video.nodeId + " Node";
    document.getElementById('watchAvatar').src = `https://api.dicebear.com/7.x/avataaars/svg?seed=${video.nodeId}`;
    
    mainPlayer.src = `/api/stream/${video.id}`;
    mainPlayer.play();

    renderRelatedVideos(allVideosData.filter(v => v.id !== video.id));
    window.scrollTo(0, 0);
}

function renderRelatedVideos(videos) {
    const relatedList = document.getElementById('relatedVideos');
    relatedList.innerHTML = '';
    videos.slice(0, 10).forEach(video => {
        const card = document.createElement('div');
        card.className = 'related-video-card';
        card.innerHTML = `
            <img src="https://picsum.photos/seed/${video.id}/168/94" class="related-thumb">
            <div class="related-info">
                <h4>${video.title}</h4>
                <div class="related-meta">
                    <div>${video.nodeId} Node <i class="fas fa-check-circle" style="font-size: 10px;"></i></div>
                    <div>12K views • 1 day ago</div>
                </div>
            </div>
        `;
        card.onclick = () => {
            openPlayer(video);
        };
        relatedList.appendChild(card);
    });
}

// Click logo to go back home
document.querySelector('.logo').onclick = () => {
    mainPlayer.pause();
    mainPlayer.src = "";
    watchLayout.style.display = 'none';
    mainLayout.style.display = 'flex';
};

// Start
fetchVideos();
