function hideAllSections() {
    document.getElementById('manageBeats').style.display = 'none';
    document.getElementById('manageServices').style.display = 'none';
    document.getElementById('manageKits').style.display = 'none';
}
function toggleManageKits() {
    hideAllSections()
    var manageKitsElement = document.getElementById('manageKits');
    if (manageKitsElement.style.display === 'none') {
        loadKits();
        manageKitsElement.style.display = 'block';
    } else {
        manageKitsElement.style.display = 'none';
    }
}
function createNewKit() {
    const kitsContainer = document.getElementById('kitsContainer');
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const newKitHtml = `
        <div class="accordion-item">
            <h2 class="accordion-header" id="headingNewKit">
                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapseNewKit" aria-expanded="false" aria-controls="collapseNewKit">
                    New Kit
                </button>
            </h2>
            <div id="collapseNewKit" class="accordion-collapse collapse" aria-labelledby="headingNewKit" data-bs-parent="#kitsContainer">
                <div class="accordion-body">
                    <form id="kitFormNew">
                        <input type="hidden" name="_csrf" value="${token}">
                        <div class="mb-3">
                            <label for="titleNewKit" class="form-label">Название:</label>
                            <input type="text" class="form-control" id="titleNewKit">
                        </div>
                        <div class="mb-3">
                            <label for="costNewKit" class="form-label">Стоимость(₽):</label>
                            <input type="number" step="0.01" class="form-control" id="costNewKit">
                        </div>
                        <div class="mb-3">
                            <label for="descriptionNewKit" class="form-label">Описание:</label>
                            <textarea class="form-control" id="descriptionNewKit" rows="3"></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="imageInputNewKit" class="form-label">Изображение:</label>
                            <input type="file" id="imageInputNewKit" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label for="archiveInputNewKit" class="form-label">Архив:</label>
                            <input type="file" id="archiveInputNewKit" class="form-control">
                        </div>
                        <button type="button" onclick="submitNewKit()" class="btn btn-primary">Создать</button>
                    </form>
                </div>
            </div>
        </div>
    `;

    const createKitButton = kitsContainer.querySelector('button[onclick="createNewKit()"]');
    createKitButton.insertAdjacentHTML('beforebegin', newKitHtml);
}
function loadKits() {
    fetch('/api/kits')
        .then(response => response.json())
        .then(kits => {
            const kitsContainer = document.getElementById('kitsContainer');
            kitsContainer.innerHTML = ''; // Clear previous content
            kits.forEach((kit, index) => {
                const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                const imageUrl = kit.image ? `data:image/jpeg;base64,${kit.image.data}` : ' ';

                const kitHtml = `
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="heading${index}">
                            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">
                                ${kit.title}
                            </button>
                        </h2>
                        <div id="collapse${index}" class="accordion-collapse collapse" aria-labelledby="heading${index}" data-bs-parent="#kitsContainer">
                            <div class="accordion-body">
                                <form id="kitForm${kit.id}">
                                    <input type="hidden" name="_csrf" value="${token}">
                                    <div class="mb-3">
                                        <label for="title${kit.id}" class="form-label">Название:</label>
                                        <input type="text" class="form-control" id="title${kit.id}" value="${kit.title}">
                                    </div>
                                    <div class="mb-3">
                                        <label for="cost${kit.id}" class="form-label">Стоимость(₽):</label>
                                        <input type="number" step="0.01" class="form-control" id="cost${kit.id}" value="${kit.cost}">
                                    </div>
                                    <div class="mb-3">
                                        <label for="description${kit.id}" class="form-label">Описание:</label>
                                        <textarea class="form-control" id="description${kit.id}" rows="3">${kit.description}</textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="imageInput${kit.id}" class="form-label">Изображение:</label>
                                        <img src="${imageUrl}" alt="Kit image" style="width: 200px; margin-bottom: 10px">
                                        <input type="file" id="imageInput${kit.id}" class="form-control">
                                    </div>
                                    <div class="mb-3">
                                        <label for="archiveInput${kit.id}" class="form-label">Архив:</label>
                                        <input type="file" id="archiveInput${kit.id}" class="form-control">
                                        ${kit.hasArchive ? `<a href="/api/kits/${kit.id}/archive" download>Скачать архив</a>` : ''}
                                    </div>
                                    <button type="button" onclick="updateKitDetails(${kit.id})" class="btn btn-primary">Сохранить изменения</button>
                                    <button type="button" onclick="deleteKit(${kit.id})" class="btn btn-danger">Удалить кит</button>
                                </form>
                            </div>
                        </div>
                    </div>
                `;

                kitsContainer.innerHTML += kitHtml;
            });
            kitsContainer.innerHTML += `
                <button type="button" onclick="createNewKit()" class="btn btn-primary">Создать новый кит</button>
            `;
        })
        .catch(error => console.error('Error loading the kits:', error));
}
function downloadKit(kitId, kitTitle) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(`/api/kits/${kitId}/archive`, {
        method: 'GET',
        headers: {
            [header]: token,
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.blob();
    })
    .then(blob => {
        const url = window.URL.createObjectURL(new Blob([blob]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `${kitTitle}_archive.zip`);
        document.body.appendChild(link);
        link.click();
        link.parentNode.removeChild(link);
    })
    .catch(error => console.error('Error downloading the kit archive:', error));
}
function submitNewKit() {
    if (!validateKitForm('NewKit')) {
        // If the validateKitForm function returns false, prevent form submission
        return;
    }
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const formData = new FormData();
    const title = document.getElementById(`titleNewKit`).value;
    const cost = document.getElementById(`costNewKit`).value;
    const description = document.getElementById(`descriptionNewKit`).value;
    const imageFile = document.getElementById(`imageInputNewKit`).files[0];
    const archiveFile = document.getElementById(`archiveInputNewKit`).files[0];

    formData.append('title', title);
    formData.append('cost', cost);
    formData.append('description', description);
    if (imageFile) formData.append('image', imageFile);
    if (archiveFile) formData.append('archive', archiveFile);

    fetch(`/api/kits/new`, {
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
            console.log('Kit created successfully:', data);
            alert('Кит успешно создан!');

            // Reload the kits
            loadKits();
        })
        .catch(error => {
            console.error('Error creating kit:', error);
            alert('Ошибка при создании кита');
        });
}
function updateKitDetails(kitId) {
    if (!validateKitForm(kitId)) {
        // If the validateKitForm function returns false, prevent form submission
        return;
    }

    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const formData = new FormData();
    const title = document.getElementById(`title${kitId}`).value;
    const cost = document.getElementById(`cost${kitId}`).value;
    const description = document.getElementById(`description${kitId}`).value;
    const imageFile = document.getElementById(`imageInput${kitId}`).files[0];
    const archiveFile = document.getElementById(`archiveInput${kitId}`).files[0];

    formData.append('title', title);
    formData.append('cost', cost);
    formData.append('description', description);
    if (imageFile) formData.append('image', imageFile);
    if (archiveFile) formData.append('archive', archiveFile);

    fetch(`/api/kits/${kitId}`, {
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
            console.log('Kit updated successfully:', data);
            alert('Кит успешно обновлен!');
            loadKits();

        })
        .catch(error => {
            console.error('Error updating kit:', error);
            alert('Ошибка при обновлении кита');
        });
}
function deleteKit(kitId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(`/api/kits/${kitId}`, {
        method: 'DELETE',
        headers: {
            [header]: token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        console.log('Kit deleted successfully');
        alert('Кит успешно удален');
        loadKits(); // Assuming there's a function to reload the kits list
    })
    .catch(error => {
        alert('Ошибка при обновлении кита');
        console.error('Error deleting kit:', error);
    });
}
function validateKitForm(suffix) {
    const title = document.getElementById(`title${suffix}`).value;
    const cost = document.getElementById(`cost${suffix}`).value;
    const description = document.getElementById(`description${suffix}`).value;
    const imageInput = document.getElementById(`imageInput${suffix}`).files[0];
    const archiveInput = document.getElementById(`archiveInput${suffix}`).files[0];

    if (!title.trim()) {
        alert('Название обязательно для заполнения.');
        return false;
    }

    if (!cost.trim()) {
        alert('Стоимость обязательна для заполнения.');
        return false;
    }

    if (isNaN(cost) || cost <= 0) {
        alert('Стоимость должна быть положительным числом.');
        return false;
    }

    if (!description.trim()) {
        alert('Описание обязательно для заполнения.');
        return false;
    }

    // Optionally, you can validate the image and archive inputs if they are required
    // if (!imageInput) {
    //     alert('Изображение обязательно для заполнения.');
    //     return false;
    // }

    // if (!archiveInput) {
    //     alert('Архив обязателен для заполнения.');
    //     return false;
    // }

    return true;
}


function toggleManageBeats() {
    hideAllSections()
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
                const imageUrl = beat.image ? `data:image/jpeg;base64,${beat.image.data}` : ' ';
                let audioHtml = beat.hasAudios ? `
                    <button type="button" onclick="loadAudio(${beat.id})">Загрузить аудио</button>
                    <div id="audioContainer${beat.id}"></div>` : '';
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
                                        <img src="${imageUrl}" alt="Beat image" style="width: 200px; margin-bottom: 10px">
                                        <input type="file" id="imageInput${beat.id}" class="form-control">
                                    </div>
                                    <div class="mb-3">
                                        <label>Аудио:</label><br>
                                        <input type="file" id="audioInput${beat.id}" class="form-control" multiple>
                                        ${audioHtml}
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
function loadAudio(beatId) {
    fetch(`/api/beats/${beatId}/audio`)
        .then(response => response.json())
        .then(audioFiles => {
            const audioContainer = document.getElementById(`audioContainer${beatId}`);
            audioContainer.innerHTML = ''; // Clear previous content
            audioFiles.forEach(audio => {
                audioContainer.innerHTML += `
                    <div class="audio-file">
                        <p>Формат: ${audio.fileFormat}</p>
                        <audio controls>
                            <source src="data:audio/${audio.fileFormat};base64,${audio.data}" type="audio/${audio.fileFormat}">
                            Your browser does not support the audio element.
                        </audio>
                        <button onclick="deleteAudio(${beatId}, ${audio.id})">Удалить</button>
                    </div>`;
            });
        })
        .catch(error => console.error('Error loading the audio files:', error));
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
            alert('Бит успешно обновлен!');

            loadBeats();
        })
        .catch(error => {
            console.error('Error updating beat:', error);
            alert('Ошибка при обновлении бита');
        });
}
function uploadAudioFiles(beatId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    let audioInputElement = document.getElementById(`audioInput${beatId}`);
    if (!audioInputElement) {
        audioInputElement = document.getElementById('audioInputNew');
    }

    const formData = new FormData();
    const audioFiles = audioInputElement.files;
    if (audioFiles.length == 0) {
        return;
    }
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
    .then(response => response.json())
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
    const name = document.getElementById('nameNew').value;
    const bpm = document.getElementById('bpmNew').value;
    const imageFile = document.getElementById('imageInputNew').files[0];

    formData.append('name', name);
    formData.append('bpm', bpm);
    if (imageFile) formData.append('image', imageFile);

    fetch('/api/beats/new', {
        method: 'POST',
        body: formData,
        headers: {
            [header]: token
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.id) {
            console.log('Beat created successfully:', data);
            uploadAudioFiles(data.id); // Upload audio files for the new beat
            alert('Бит создан успешно!');

            // Reload the beats
            loadBeats();
        } else {
            throw new Error('Failed to get new beat ID');
        }
    })
    .catch(error => {
        console.error('Error creating beat:', error);
        alert('Ошибка создания бита');
    });
}
function validateBeatForm(beatId) {
    const name = document.getElementById(`name${beatId}`).value;
    const bpm = document.getElementById(`bpm${beatId}`).value;
    const imageFile = document.getElementById(`imageInput${beatId}`).files[0];
    const audioInputElement = document.getElementById(`audioInput${beatId}`);
    const audioFiles = audioInputElement ? audioInputElement.files : [];

    // Check if name is not empty
    if (!name.trim()) {
        alert('Название нужно ввести обязательно');
        return false;
    }

    // Check if bpm is not empty and is a number
    if (!bpm || isNaN(bpm)) {
        alert('БПМ нужно ввести обязательно');
        return false;
    }

    // Check if image file is not empty and is a jpg or png file
    if (imageFile) {
        const imageFileType = imageFile.type;
        if (imageFileType !== 'image/jpeg' && imageFileType !== 'image/png') {
            alert('Изображение должно быть jpg или png форматов');
            return false;
        }
    }

    // Check if audio files are not empty and are mp3 or wav files
    for (let i = 0; i < audioFiles.length; i++) {
        const audioFileType = audioFiles[i].type;
        if (audioFileType !== 'audio/mpeg' && audioFileType !== 'audio/wav') {
            alert('Аудио файлы должны быть формата mp3 или Wav');
            return false;
        }
    }

//    if (!imageFile) {
//       alert('Изображение обязательно для заполнения.');
//       return false;
//    }
//
//    if (!audioFiles) {
//        alert('Архив обязателен для заполнения.');
//      return false;
//    }

    // If all checks pass, proceed with form submission
    return true;
}


function toggleManageServices() {
    hideAllSections()
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
                    itemsHtml += `
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" id="item${service.id}-${item.id}" value="${item.item}">
                            <button type="button" onclick="deleteServiceItem(${service.id}, ${item.id})">Удалить</button>
                        </div>`;
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
                                        <label for="price${service.id}" class="form-label">Цена(₽):</label>
                                        <input type="number" class="form-control" id="price${service.id}" value="${service.price}">
                                    </div>
                                    Описание:
                                        ${itemsHtml}
                                    <button type="button" onclick="addServiceItem(${service.id})" class="btn btn-secondary">Добавить новый элемент</button>
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
function addServiceItem(serviceId) {
    const newItemHtml = `
        <div class="input-group mb-3">
            <input type="text" class="form-control" id="newItem${serviceId}">
            <button type="button" onclick="deleteNewServiceItem(this)">Удалить</button>
        </div>
    `;
    const serviceForm = document.getElementById(`serviceForm${serviceId}`);
    const addNewItemButton = serviceForm.querySelector('.btn-secondary'); // Select the "Добавить новый элемент" button

    if (addNewItemButton) {
        addNewItemButton.insertAdjacentHTML('beforebegin', newItemHtml); // Insert the new item before the "Добавить новый элемент" button
    } else {
        console.error('Add new item button not found');
    }
}
function deleteNewServiceItem(button) {
    button.parentNode.remove();
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

    const newItemInputs = document.querySelectorAll(`input[id^="newItem${serviceId}"]`);
    newItemInputs.forEach(input => {
        const newItemValue = input.value.trim();
        if (newItemValue !== '') {
            items.push({ serviceId: serviceId, item: newItemValue });
        }
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
            alert('Услуга успешно обновлена');
            loadServices();
        })
        .catch(error => {
            console.error('Error updating service:', error);
            alert('Ошибка при обновлении услуги');
        });
}
function deleteServiceItem(serviceId, itemId) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(`/api/services/${serviceId}/items/${itemId}`, {
        method: 'DELETE',
        headers: {
            [header]: token
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            console.log('Service item deleted successfully');
            loadServices();
        })
        .catch(error => {
            alert('Ошибка при удалении элемента услуг');
            console.error('Error deleting service item:', error);
        });
}
function validateServiceForm(serviceId) {
    const name = document.getElementById(`name${serviceId}`).value.trim();
    const price = document.getElementById(`price${serviceId}`).value.trim();
    const items = document.querySelectorAll(`#serviceForm${serviceId} .form-control`);

    if (!name) {
        alert('Название обязательно для заполнения.');
        return false;
    }

    if (!price) {
        alert('Цена обязательна для заполнения.');
        return false;
    }

    if (isNaN(price) || price <= 0) {
        alert('Цена должна быть положительным числом.');
        return false;
    }

    for (let item of items) {
        if (!item.value.trim()) {
            alert('Все описания обязательны для заполнения.');
            return false;
        }
    }

    return true;
}