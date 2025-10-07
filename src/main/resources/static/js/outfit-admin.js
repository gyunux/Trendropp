document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('outfit-table-body');
    const API_BASE_URL = '/api/admin/outfits';

    let allCelebs = [];
    let allBrands = [];
    const allSourceTypes = [
        { value: 'MAGAZINE', text: '매거진' },
        { value: 'INSTAGRAM', text: '인스타그램' },
        { value: 'AIRPORT_FASHION', text: '공항패션' },
        { value: 'OFFICIAL_EVENT', text: '공식행사' },
        { value: 'BROADCAST', text: '방송 출연' },
        { value: 'ETC', text: '기타' }
    ];

    const editModal = document.getElementById('edit-outfit-modal');
    const editForm = document.getElementById('edit-outfit-form');
    const editCelebSelect = document.getElementById('edit-celebId');
    const editSourceTypeSelect = document.getElementById('edit-sourceType');
    const itemsContainer = document.getElementById('edit-items-container');

    async function loadInitialData() {
        try {
            const [celebsRes, brandsRes] = await Promise.all([
                fetch('/api/admin/celebs'),
                fetch('/api/admin/brands')
            ]);

            if (!celebsRes.ok || !brandsRes.ok) throw new Error('초기 데이터 로딩 실패');

            allCelebs = await celebsRes.json();
            allBrands = await brandsRes.json();

            populateSelectOptions(editCelebSelect, allCelebs, 'id', 'name');
            populateSelectOptions(editSourceTypeSelect, allSourceTypes, 'value', 'text');

        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    }

    function populateSelectOptions(selectElement, data, valueKey, textKey) {
        selectElement.innerHTML = '';
        data.forEach(item => {
            const option = document.createElement('option');
            option.value = item[valueKey];
            option.textContent = item[textKey];
            selectElement.appendChild(option);
        });
    }

    const searchInput = document.getElementById('outfit-search-input');
    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        tableBody.querySelectorAll('tr').forEach(row => {
            const celebName = row.querySelector('.outfit-celeb-name')?.textContent.toLowerCase() || '';
            row.style.display = celebName.includes(searchTerm) ? '' : 'none';
        });
    });

    tableBody.addEventListener('click', (e) => {
        const row = e.target.closest('tr');
        if (!row) return;
        const outfitId = row.dataset.id;
        if (e.target.matches('.edit-btn')) openEditModal(outfitId);
        if (e.target.matches('.delete-btn')) {
            if (confirm(`착장(ID: ${outfitId})을 정말로 삭제하시겠습니까?`)) deleteOutfit(outfitId);
        }
    });

    async function openEditModal(outfitId) {
        try {
            const response = await fetch(`${API_BASE_URL}/${outfitId}`);
            if (!response.ok) throw new Error('착장 정보를 불러오는 데 실패했습니다.');
            const outfitData = await response.json();

            document.getElementById('edit-outfit-id').value = outfitData.id;
            editCelebSelect.value = outfitData.celeb.id;
            editSourceTypeSelect.value = outfitData.sourceType;
            document.getElementById('edit-sourceUrl').value = outfitData.sourceUrl;
            if (outfitData.sourceDate) {
                document.getElementById('edit-sourceDate').value = outfitData.sourceDate.slice(0, 16);
            }
            document.getElementById('edit-mainImageUrl').value = outfitData.originImageUrl;

            itemsContainer.innerHTML = '';
            outfitData.items.forEach(item => addItemForm(item));

            editModal.classList.add('active');
        } catch (error) {
            alert(error.message);
        }
    }

    document.getElementById('edit-add-item-btn').addEventListener('click', () => addItemForm());
    function addItemForm(item = {}) {
        const brandOptionsHtml = allBrands.map(brand =>
            `<option value="${brand.id}" ${item.brand?.id === brand.id ? 'selected' : ''}>${brand.englishName}</option>`
        ).join('');

        const itemFormHtml = `
            <div class="item-form-group">
                <select class="item-brand-id" required>${brandOptionsHtml}</select>
                <input type="text" class="item-name" placeholder="아이템 이름" value="${item.name || ''}" required>
                <input type="url" class="item-product-url" placeholder="구매 링크" value="${item.productUrl || ''}" required>
                <button type="button" class="btn btn-danger btn-sm remove-item-btn">X</button>
            </div>
        `;
        itemsContainer.insertAdjacentHTML('beforeend', itemFormHtml);
    }

    itemsContainer.addEventListener('click', (e) => {
        if (e.target.matches('.remove-item-btn')) e.target.closest('.item-form-group').remove();
    });

    editModal.addEventListener('click', (e) => {
        if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) editModal.classList.remove('active');
    });

    editForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const outfitId = document.getElementById('edit-outfit-id').value;
        const items = [];
        itemsContainer.querySelectorAll('.item-form-group').forEach(group => {
            items.push({
                brandId: group.querySelector('.item-brand-id').value,
                itemName: group.querySelector('.item-name').value,
                productUrl: group.querySelector('.item-product-url').value,
            });
        });

        const formData = {
            celebId: editCelebSelect.value,
            sourceType: editSourceTypeSelect.value,
            sourceUrl: document.getElementById('edit-sourceUrl').value,
            sourceDate: document.getElementById('edit-sourceDate').value,
            mainImageUrl: document.getElementById('edit-mainImageUrl').value,
            items: items
        };

        try {
            const response = await fetch(`${API_BASE_URL}/${outfitId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });
            if (!response.ok) throw new Error('수정 실패');
            alert('성공적으로 수정되었습니다.');
            window.location.reload();
        } catch (error) {
            alert(error.message);
        }
    });

    async function deleteOutfit(outfitId) {
        try {
            const response = await fetch(`${API_BASE_URL}/${outfitId}`, { method: 'DELETE' });
            if (!response.ok) throw new Error('삭제 실패');
            alert('성공적으로 삭제되었습니다.');
            window.location.reload();
        } catch (error) {
            alert(error.message);
        }
    }

    loadInitialData();
});