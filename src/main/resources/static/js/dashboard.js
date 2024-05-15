function toggleManageBeats() {
    var manageBeatsElement = document.getElementById('manageBeats');
    if (manageBeatsElement.style.display === 'none') {
        loadBeats();
        manageBeatsElement.style.display = 'block';
    } else {
        manageBeatsElement.style.display = 'none';
    }
}

function loadBeats() {
    fetch('/api/beats')
        .then(response => response.json())
        .then(beats => {
            const beatsContainer = document.getElementById('beatsContainer');
            beatsContainer.innerHTML = ''; // Clear previous content
            beats.forEach((beat, index) => {
                const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                const imageUrl = beat.image ? `data:image/jpeg;base64,${beat.image.data}` : 'default_image.png';
                let audioHtml = '';
                beat.audioFiles.forEach(audio => {
                    audioHtml += `
                        <audio controls>
                            <source src="data:audio/${audio.fileFormat};base64,${audio.data}" type="audio/${audio.fileFormat}">
                            Your browser does not support the audio element.
                        </audio>
                        <button onclick="deleteAudio(${beat.id}, ${audio.id})">Удалить</button>`;
                });
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
                                        <label>Аудио:</label><br>
                                        ${audioHtml}
                                        <input type="file" id="audioInput${beat.id}" class="form-control" multiple>
                                    </div>
                                    <button type="button" onclick="updateBeatDetails(${beat.id})" class="btn btn-primary">Сохранить изменения</button>
                                    <button type="button" onclick="deleteBeat(${beat.id})" class="btn btn-danger">Удалить бит</button>
                                </form>
                            </div>
                        </div>
                    </div>
                `;
            });
            beatsContainer.innerHTML += `
                <button type="button" onclick="createNewBeat()" class="btn btn-primary">Создать новый бит</button>
            `;
        })
        .catch(error => console.error('Error loading the beats:', error));
}
function deleteBeat(beatId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(`/api/beats/${beatId}`, {
        method: 'DELETE',
        headers: {
            [header]: token
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            console.log('Beat deleted successfully');
            loadBeats();
        })
        .catch(error => {
            console.error('Error deleting beat:', error);
        });
}
function deleteAudio(beatId, audioId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(`/api/beats/${beatId}/audio/${audioId}`, {
        method: 'DELETE',
        headers: {
            [header]: token
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            console.log('Audio file deleted successfully');
            loadBeats();
        })
        .catch(error => {
            console.error('Error deleting audio file:', error);
        });
}


function updateBeatDetails(beatId) {
    if (!validateBeatForm(beatId)) {
        // If the validateBeatForm function returns false, prevent form submission
        return;
    }

    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const formData = new FormData();
    const name = document.getElementById(`name${beatId}`).value;
    const bpm = document.getElementById(`bpm${beatId}`).value;
    const imageFile = document.getElementById(`imageInput${beatId}`).files[0];

    formData.append('name', name);
    formData.append('bpm', bpm);
    if (imageFile) formData.append('image', imageFile);

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
            else {
                uploadAudioFiles(beatId);
            }
            return response.json();
        })
        .then(data => {
            console.log('Beat updated successfully:', data);
            alert('Beat updated successfully!');

            // Optionally reload only the expanded beat section
            loadBeats();

            // Alternatively, reload the entire page
            // window.location.reload();

        })
        .catch(error => {
            console.error('Error updating beat:', error);
            alert('Failed to update beat');
        });
}


function uploadAudioFiles(beatId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const formData = new FormData();
    const audioFiles = document.getElementById(`audioInput${beatId}`).files;

    Array.from(audioFiles).forEach(file => {
        formData.append('audio', file);
    });

    fetch(`/api/beats/${beatId}/audio`, {
        method: 'POST',
        body: formData,
        headers: {
            [header]: token
        }
    })
        .then(response => response.ok ? response.json() : Promise.reject('Failed to upload audio files'))
        .then(data => {
            console.log('Audio files uploaded successfully:', data);
        })
        .catch(error => {
            console.error('Error uploading audio files:', error);
        });
}
function createNewBeat() {
    const beatsContainer = document.getElementById('beatsContainer');
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const newBeatHtml = `
        <div class="accordion-item">
            <h2 class="accordion-header" id="headingNew">
                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseNew" aria-expanded="false" aria-controls="collapseNew">
                    New Beat
                </button>
            </h2>
            <div id="collapseNew" class="accordion-collapse collapse" aria-labelledby="headingNew" data-bs-parent="#beatsContainer">
                <div class="accordion-body">
                    <form id="beatFormNew">
                        <input type="hidden" name="_csrf" value="${token}">
                        <div class="mb-3">
                            <label for="nameNew" class="form-label">Название:</label>
                            <input type="text" class="form-control" id="nameNew">
                        </div>
                        <div class="mb-3">
                            <label for="bpmNew" class="form-label">БПМ:</label>
                            <input type="number" class="form-control" id="bpmNew">
                        </div>
                        <div class="mb-3">
                            <label>Изображение:</label>
                            <input type="file" id="imageInputNew" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label>Аудио:</label><br>
                            <input type="file" id="audioInputNew" class="form-control" multiple>
                        </div>
                        <button type="button" onclick="submitNewBeat()" class="btn btn-primary">Создать</button>
                    </form>
                </div>
            </div>
        </div>
    `;

    const createBeatButton = beatsContainer.querySelector('button[onclick="createNewBeat()"]');
    createBeatButton.insertAdjacentHTML('beforebegin', newBeatHtml);
}
function submitNewBeat() {
    if (!validateBeatForm('New')) {
        // If the validateBeatForm function returns false, prevent form submission
        return;
    }
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const formData = new FormData();
    const name = document.getElementById(`nameNew`).value;
    const bpm = document.getElementById(`bpmNew`).value;
    const imageFile = document.getElementById(`imageInputNew`).files[0];

    formData.append('name', name);
    formData.append('bpm', bpm);
    if (imageFile) formData.append('image', imageFile);

    fetch(`/api/beats/new`, {
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
            else {
                uploadAudioFiles(response.id);
            }
            return response.json();
        })
        .then(data => {
            console.log('Beat created successfully:', data);
            alert('Beat created successfully!');

            // Reload the beats
            loadBeats();
        })
        .catch(error => {
            console.error('Error creating beat:', error);
            alert('Failed to create beat');
        });
}
function validateBeatForm(beatId) {
    const name = document.getElementById(`name${beatId}`).value;
    const bpm = document.getElementById(`bpm${beatId}`).value;
    const imageFile = document.getElementById(`imageInput${beatId}`).files[0];
    const audioFiles = document.getElementById(`audioInput${beatId}`).files;

    // Check if name is not empty
    if (!name.trim()) {
        alert('Name is required');
        return false;
    }

    // Check if bpm is not empty and is a number
    if (!bpm || isNaN(bpm)) {
        alert('BPM is required and should be a number');
        return false;
    }

    // Check if image file is not empty and is a jpg or png file
    if (imageFile) {
        const imageFileType = imageFile.type;
        if (imageFileType !== 'image/jpeg' && imageFileType !== 'image/png') {
            alert('Image file should be a jpg or png file');
            return false;
        }
    }

    // Check if audio files are not empty and are mp3 or wav files
    for (let i = 0; i < audioFiles.length; i++) {
        const audioFileType = audioFiles[i].type;
        if (audioFileType !== 'audio/mpeg' && audioFileType !== 'audio/wav') {
            alert('Audio files should be mp3 or wav files');
            return false;
        }
    }

    // If all checks pass, proceed with form submission
    return true;
}

function toggleManageServices() {
    var manageServicesElement = document.getElementById('manageServices');
    if (manageServicesElement.style.display === 'none') {
        loadServices();
        manageServicesElement.style.display = 'block';
    } else {
        manageServicesElement.style.display = 'none';
    }
}
function loadServices() {
    fetch('/api/services')
        .then(response => response.json())
        .then(services => {
            const servicesContainer = document.getElementById('servicesContainer');
            servicesContainer.innerHTML = ''; // Clear previous content
            services.forEach((service, index) => {
                let itemsHtml = '';
                service.items.forEach((item) => {
                    itemsHtml += `<input type="text" class="form-control" id="item${service.id}-${item.id}" value="${item.item}" style="margin-bottom: 10px">`;
                });
                servicesContainer.innerHTML += `
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="headingService${index}">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseService${index}" aria-expanded="false" aria-controls="collapseService${index}">
                                ${service.name}
                            </button>
                        </h2>
                        <div id="collapseService${index}" class="accordion-collapse collapse" aria-labelledby="headingService${index}" data-bs-parent="#servicesContainer">
                            <div class="accordion-body">
                                <form id="serviceForm${service.id}">
                                    <div class="mb-3">
                                        <label for="name${service.id}" class="form-label">Название:</label>
                                        <input type="text" class="form-control" id="name${service.id}" value="${service.name}">
                                    </div>
                                    <div class="mb-3">
                                        <label for="price${service.id}" class="form-label">Цена:</label>
                                        <input type="number" class="form-control" id="price${service.id}" value="${service.price}">
                                    </div>
                                    Описание:
                                        ${itemsHtml}
                                    <button type="button" onclick="updateServiceDetails(${service.id})" class="btn btn-primary">Сохранить изменения</button>
                                </form>
                            </div>
                        </div>
                    </div>
                `;

            });
            servicesContainer.innerHTML += `
                    <button type="button" onclick="createNewService()" class="btn btn-primary">Создать новую услугу</button>
                `;
        })
        .catch(error => console.error('Error loading the services:', error));
}
function updateServiceDetails(serviceId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const name = document.getElementById(`name${serviceId}`).value;
    const price = document.getElementById(`price${serviceId}`).value;
    const items = Array.from(document.querySelectorAll(`input[id^="item${serviceId}-"]`)).map(input => {
        const itemId = input.id.split('-')[1];
        return { id: itemId, serviceId: serviceId, item: input.value };
    });
    const updatedService = {
        id: serviceId,
        name: name,
        price: price,
        items: items
    };

    fetch(`/api/services/${serviceId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify(updatedService)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            console.log('Service updated successfully');
            loadServices();
        })
        .catch(error => {
            console.error('Error updating service:', error);
        });
}