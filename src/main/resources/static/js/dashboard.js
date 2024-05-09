function loadBeatsAndToggleDisplay() {
    fetch('/api/beats')
        .then(response => response.json())
        .then(beats => {
            const beatsContainer = document.getElementById('beatsContainer');
            beatsContainer.innerHTML = ''; // Clear previous content
            beats.forEach((beat, index) => {
                const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                let imageUrl;
                if (beat.image && beat.image.data) {
                    imageUrl = `data:image/jpeg;base64,${beat.image.data}`;
                }
                beatsContainer.innerHTML += `
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="heading${index}">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
                                ${beat.name}
                            </button>
                        </h2>
                        <div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#beatsContainer">
                            <div class="accordion-body">
                                <form id="beatForm${beat.id}">
                                    <input type="hidden" name="_csrf" value="${token}">
                                    <div class="mb-3">
                                        <label for="name${beat.id}" class="form-label">Название:</label>
                                        <input type="text" class="form-control" id="name${beat.id}" value="${beat.name}">
                                    </div>
                                    <div class="mb-3">
                                        <label for="bpm${beat.id}" class="form-label">БПМ:</label>
                                        <input type="number" class="form-control" id="bpm${beat.id}" value="${beat.bpm}">
                                    </div>
                                    <div class="mb-3">
                                        <label>Изображение:</label>
                                        <img src="${imageUrl}" alt="Beat image" style="width: 400px; margin-bottom: 10px">
                                        <input type="file" id="imageInput${beat.id}" class="form-control">
                                    </div>
                                    <div class="mb-3">
                                        <label>Аудио:</label>
                                        <audio controls>
                                            <source src="${beat.audioPath}" type="audio/mpeg">
                                            Your browser does not support the audio element.
                                        </audio>
                                        <input type="file" id="audioInput${beat.id}" class="form-control">
                                    </div>
                                    <button type="button" onclick="updateBeatDetails(${beat.id})" class="btn btn-primary">Сохранить изменения</button>
                                </form>
                            </div>
                        </div>
                    </div>
                `;
            });
            toggleManageBeats();
        })
        .catch(error => console.error('Error loading the beats:', error));
}

function toggleManageBeats() {
    var manageBeatsElement = document.getElementById('manageBeats');
    manageBeatsElement.style.display = manageBeatsElement.style.display === 'none' ? 'block' : 'none';
}

function updateBeatDetails(beatId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Create FormData object to allow file uploading alongside data
    const formData = new FormData();
    const name = document.getElementById(`name${beatId}`).value;
    const bpm = document.getElementById(`bpm${beatId}`).value;
    const imageFile = document.getElementById(`imageInput${beatId}`).files[0];
    const audioFile = document.getElementById(`audioInput${beatId}`).files[0];

    // Append fields and files to FormData
    formData.append('name', name);
    formData.append('bpm', bpm);
    if (imageFile) formData.append('image', imageFile);
    if (audioFile) formData.append('audio', audioFile);

    fetch(`/api/beats/${beatId}`, {
        method: 'POST',
        body: formData,
        headers: {
            [header]: token
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Success:', data);
            alert('Beat updated successfully!');
            // Optionally refresh part of your website or re-fetch beat data to update the UI
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function createNewBeat() {
    // Optionally add form or modal dialog to capture new beat details
}

function deleteBeat(beatId) {
    // Optionally call an API to delete a beat
}

function toggleManageServices() {
    var manageServicesElement = document.getElementById('manageServices');
    if (manageServicesElement.style.display === 'none') {
        manageServicesElement.style.display = 'block';
    } else {
        manageServicesElement.style.display = 'none';
    }
}