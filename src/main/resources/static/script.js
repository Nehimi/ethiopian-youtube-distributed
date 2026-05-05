const videoGrid = document.getElementById('videoGrid');
const mainPlayer = document.getElementById('mainPlayer');
const mainLayout = document.querySelector('.main-layout');
const watchLayout = document.getElementById('watchLayout');

let allVideosData = [];

async function fetchVideos() {
    renderSkeletons();
    try {
        const response = await fetch('/api/videos');
        if (!response.ok) throw new Error('Failed to load videos from server');
        allVideosData = await response.json();
        renderVideos(allVideosData);
    } catch (error) {
        console.error('Error fetching videos:', error);
        videoGrid.innerHTML = '<div style="color: #ff4a4a; text-align: center; width: 100%; grid-column: 1 / -1; padding: 40px; font-size: 18px;">Failed to load videos. Please check your connection to the distributed nodes.</div>';
    }
}

function renderSkeletons() {
    videoGrid.innerHTML = '';
    for (let i = 0; i < 8; i++) {
        const card = document.createElement('div');
        card.className = 'video-card';
        card.innerHTML = `
            <div class="thumbnail-container skeleton" style="border-radius: 12px; margin-bottom: 12px;"></div>
            <div class="video-details">
                <div class="skeleton skeleton-avatar"></div>
                <div class="video-info" style="width: 100%;">
                    <div class="skeleton skeleton-text"></div>
                    <div class="skeleton skeleton-text short"></div>
                </div>
            </div>
        `;
        videoGrid.appendChild(card);
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
    document.getElementById('searchInput').value = "";
    fetchVideos();
};

// --- Search Logic ---
const searchInput = document.getElementById('searchInput');
const searchBtn = document.getElementById('searchBtn');

async function handleSearch() {
    const query = searchInput.value.trim();
    if (!query) {
        fetchVideos();
        return;
    }

    renderSkeletons();
    try {
        const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error('Search failed');
        const searchResults = await response.json();
        renderVideos(searchResults);

        if (searchResults.length === 0) {
            videoGrid.innerHTML = `
                <div style="text-align: center; width: 100%; grid-column: 1 / -1; padding: 60px; color: #aaa;">
                    <i class="fas fa-search" style="font-size: 48px; margin-bottom: 20px; display: block; opacity: 0.3;"></i>
                    <h3>No results found for "${query}"</h3>
                    <p>Try different keywords or check your spelling.</p>
                </div>
            `;
        }
    } catch (error) {
        console.error('Search error:', error);
        videoGrid.innerHTML = '<div style="color: #ff4a4a; text-align: center; width: 100%; grid-column: 1 / -1; padding: 40px;">Search failed.</div>';
    }
}

searchBtn.onclick = handleSearch;
searchInput.onkeypress = (e) => {
    if (e.key === 'Enter') handleSearch();
};

// --- Redesigned Upload Logic (YouTube Studio Style) ---
const uploadModal = document.getElementById('uploadModal');
const uploadBtnIcon = document.getElementById('uploadBtnIcon');
const closeUploadBtn = document.querySelector('.close-upload-btn');
const uploadFile = document.getElementById('uploadFile');
const uploadPreview = document.getElementById('uploadPreview');
const filePreviewPlaceholder = document.getElementById('filePreviewPlaceholder');
const fileNameDisplay = document.getElementById('fileNameDisplay');
const uploadTitle = document.getElementById('uploadTitle');
const uploadDesc = document.getElementById('uploadDesc');
const titleCount = document.getElementById('titleCount');
const descCount = document.getElementById('descCount');
const nextBtn = document.getElementById('nextBtn');
const prevBtn = document.getElementById('prevBtn');
const finishBtn = document.getElementById('finishBtn');
const progressBarFill = document.getElementById('progressBarFill');
const progressText = document.getElementById('progressText');

let currentStep = 1;

uploadBtnIcon.onclick = () => {
    uploadModal.style.display = 'flex';
    resetUploadModal();
};

closeUploadBtn.onclick = () => {
    uploadModal.style.display = 'none';
};

// Character counts
uploadTitle.oninput = () => {
    titleCount.innerText = uploadTitle.value.length;
};

uploadDesc.oninput = () => {
    descCount.innerText = uploadDesc.value.length;
};

// File selection and preview
uploadFile.onchange = (e) => {
    const file = e.target.files[0];
    if (file) {
        fileNameDisplay.innerText = file.name;
        uploadTitle.value = file.name.replace(/\.[^/.]+$/, ""); // Auto-fill title with filename
        titleCount.innerText = uploadTitle.value.length;

        filePreviewPlaceholder.style.display = 'none';
        uploadPreview.style.display = 'block';
        uploadPreview.src = URL.createObjectURL(file);

        document.querySelector('.video-link').innerText = "Processing " + file.name + "...";
    }
};

// Stepper Logic
function updateStepper() {
    // Update steps
    document.querySelectorAll('.step').forEach(step => {
        const stepNum = parseInt(step.dataset.step);
        step.classList.remove('active', 'completed');
        if (stepNum === currentStep) {
            step.classList.add('active');
        } else if (stepNum < currentStep) {
            step.classList.add('completed');
        }
    });

    // Update content
    document.querySelectorAll('.step-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(`step${currentStep}`).classList.add('active');

    // Update buttons
    prevBtn.disabled = currentStep === 1;
    if (currentStep === 4) {
        nextBtn.style.display = 'none';
        finishBtn.style.display = 'block';
    } else {
        nextBtn.style.display = 'block';
        finishBtn.style.display = 'none';
    }
}

nextBtn.onclick = () => {
    if (currentStep < 4) {
        currentStep++;
        updateStepper();
    }
};

prevBtn.onclick = () => {
    if (currentStep > 1) {
        currentStep--;
        updateStepper();
    }
};

// Final Upload Submission
finishBtn.onclick = async () => {
    const file = uploadFile.files[0];
    if (!file) {
        alert("Please select a video file first.");
        currentStep = 1;
        updateStepper();
        return;
    }

    const title = uploadTitle.value;
    const desc = uploadDesc.value;

    finishBtn.disabled = true;
    finishBtn.innerText = "UPLOADING...";

    const formData = new FormData();
    formData.append("title", title);
    formData.append("description", desc);
    formData.append("file", file);
    if (thumbFile.files[0]) {
        formData.append("thumbnail", thumbFile.files[0]);
    }

    progressText.innerText = "Uploading to distributed nodes...";
    progressBarFill.style.width = "30%";

    try {
        const response = await fetch('/api/upload', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            progressBarFill.style.width = "100%";
            progressText.innerText = "Upload complete! Processing...";
            progressText.style.color = "lightgreen";

            setTimeout(() => {
                uploadModal.style.display = 'none';
                fetchVideos();
            }, 2000);
        } else {
            const errText = await response.text();
            progressText.innerText = "Failed: " + errText;
            progressText.style.color = "#ff4a4a";
            progressBarFill.style.backgroundColor = "#ff4a4a";
        }
    } catch (err) {
        progressText.innerText = "Error: " + err.message;
        progressText.style.color = "#ff4a4a";
        progressBarFill.style.backgroundColor = "#ff4a4a";
    } finally {
        finishBtn.disabled = false;
        finishBtn.innerText = "UPLOAD";
    }
};

const thumbFile = document.getElementById('thumbFile');
const thumbPreview = document.getElementById('thumbPreview');
const thumbSelector = document.getElementById('thumbSelector');

// Thumbnail selection and preview
thumbFile.onchange = (e) => {
    const file = e.target.files[0];
    if (file) {
        thumbPreview.src = URL.createObjectURL(file);
        thumbPreview.style.display = 'block';
        thumbSelector.querySelector('i').style.display = 'none';
        thumbSelector.querySelector('span').style.display = 'none';
    }
};

function resetUploadModal() {
    currentStep = 1;
    uploadTitle.value = "";
    uploadDesc.value = "";
    uploadFile.value = "";
    thumbFile.value = "";
    uploadPreview.src = "";
    uploadPreview.style.display = 'none';
    thumbPreview.src = "";
    thumbPreview.style.display = 'none';
    thumbSelector.querySelector('i').style.display = 'block';
    thumbSelector.querySelector('span').style.display = 'block';
    filePreviewPlaceholder.style.display = 'flex';
    fileNameDisplay.innerText = "No file selected";
    titleCount.innerText = "0";
    descCount.innerText = "0";
    progressBarFill.style.width = "0%";
    progressBarFill.style.backgroundColor = "#3ea6ff";
    progressText.innerText = "Ready to upload";
    progressText.style.color = "#aaa";
    updateStepper();
}

// Start
fetchVideos();
