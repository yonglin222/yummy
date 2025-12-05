// 간단히 userId를 상수로 두고 시작
const USER_ID = 1;
const STORAGE_KEY = `yammi_fridge_${USER_ID}`;

let fridgeState = {
    freezer: [],
    fridge: [],
    room: []
};

document.addEventListener("DOMContentLoaded", () => {
    loadFromStorage();
    renderAll();

    // 재료 자동 추가
    document.querySelectorAll(".add-ingredient-btn").forEach(btn => {
        btn.addEventListener("click", () => autoAddItem(btn.dataset.category));
    });

    // 선택 삭제
    document.getElementById("deleteSelectedBtn")
        .addEventListener("click", handleDeleteSelected);

    // 레시피 불러오기
    document.getElementById("recipeBtn")
        .addEventListener("click", handleRecipeButton);
});

/* ===== 로컬스토리지 ===== */

function saveToStorage() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(fridgeState));
}

function loadFromStorage() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (!saved) return;
    try {
        fridgeState = { ...fridgeState, ...JSON.parse(saved) };
    } catch (e) {
        console.error("저장된 값을 불러오는 중 오류", e);
    }
}

/* ===== 자동 추가 기능 ===== */

function autoAddItem(categoryKey) {
    const newItem = {
        id: Date.now(),
        name: "새 재료",
        expireDate: null
    };

    fridgeState[categoryKey].push(newItem);
    saveToStorage();
    renderAll();
}

/* ===== 렌더링 ===== */

function renderAll() {
    renderCategory("freezer", document.getElementById("freezerList"));
    renderCategory("fridge", document.getElementById("fridgeList"));
    renderCategory("room", document.getElementById("roomList"));
}

function renderCategory(key, container) {
    container.innerHTML = "";
    const list = fridgeState[key];

    list.forEach(item => {
        const wrapper = document.createElement("div");
        wrapper.className = "ingredient-row";
        wrapper.dataset.id = item.id;
        wrapper.dataset.category = key;

        const ddayInfo = getDDayInfo(item.expireDate);

        wrapper.innerHTML = `
            <label class="check-wrap">
                <input type="checkbox" class="ingredient-check" />
            </label>

            <div class="ingredient-info">
                <div class="top-line">
                    <span class="ingredient-name" data-id="${item.id}">${item.name}</span>
                    <span class="ingredient-date" data-id="${item.id}">
                        ${item.expireDate || "유통기한 등록"}
                    </span>
                    ${
                        item.expireDate
                        ? `<span class="ingredient-dday ${ddayInfo.className}">${ddayInfo.label}</span>`
                        : ""
                    }
                </div>
                <div class="underline"></div>
            </div>
        `;

        container.appendChild(wrapper);
    });
}

/* ===== D-day 계산 ===== */

function getDDayInfo(dateStr) {
    if (!dateStr) return { label: "", className: "", status: "none" };

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const target = new Date(dateStr);
    const diffDays = Math.round((target - today) / (1000 * 60 * 60 * 24));

    if (diffDays < 0) {
        return { label: "유통기한 지남", className: "expired", status: "expired" };
    }
    if (diffDays === 0) {
        return { label: "D-day", className: "warning", status: "warning" };
    }
    if (diffDays <= 3) {
        return { label: `D-${diffDays}`, className: "warning", status: "warning" };
    }

    return { label: `D-${diffDays}`, className: "", status: "normal" };
}

/* ===== 인라인 수정 ===== */

document.addEventListener("click", (e) => {
    if (e.target.classList.contains("ingredient-name")) {
        enterNameEditMode(e.target);
    }
    if (e.target.classList.contains("ingredient-date")) {
        enterDateEditMode(e.target);
    }
});

// 이름 수정
function enterNameEditMode(nameEl) {
    const id = Number(nameEl.dataset.id);
    const category = nameEl.closest(".ingredient-row").dataset.category;
    const original = nameEl.textContent;

    const input = document.createElement("input");
    input.type = "text";
    input.value = original;
    input.className = "edit-inline";
    nameEl.replaceWith(input);
    input.focus();

    function save() {
        const newName = input.value.trim() || original;
        fridgeState[category].find(i => i.id === id).name = newName;
        saveToStorage();
        renderAll();
    }

    input.addEventListener("blur", save);
    input.addEventListener("keypress", e => {
        if (e.key === "Enter") save();
    });
}

// 날짜 수정
function enterDateEditMode(dateEl) {
    const id = Number(dateEl.dataset.id);
    const category = dateEl.closest(".ingredient-row").dataset.category;

    const original = dateEl.textContent === "유통기한 등록" ? "" : dateEl.textContent;

    const input = document.createElement("input");
    input.type = "date";
    input.value = original;
    input.className = "edit-inline";
    dateEl.replaceWith(input);
    input.focus();

    function save() {
        const newDate = input.value || null;
        fridgeState[category].find(i => i.id === id).expireDate = newDate;
        saveToStorage();
        renderAll();
    }

    input.addEventListener("blur", save);
    input.addEventListener("change", save);
}

// 삭제 //
async function handleDeleteSelected() {
  const checkedNodes = document.querySelectorAll(
    ".ingredient-row .ingredient-check:checked"
  );

  if (!checkedNodes.length) {
    showAutoModal("삭제할 재료를 선택해주세요.");
    return;
  }

  const ok = await showConfirmModal("선택한 재료를 삭제하시겠습니까?");
  if (!ok) return;

  checkedNodes.forEach((chk) => {
    const itemEl = chk.closest(".ingredient-row");
    const id = Number(itemEl.dataset.id);
    const categoryKey = itemEl.dataset.category;

    fridgeState[categoryKey] = fridgeState[categoryKey].filter(
      (i) => i.id !== id
    );
  });

  saveToStorage();
  renderAll();
}




/* 레시피 페이지 이동 */
function handleRecipeButton() {
    const allNames = [
        ...fridgeState.freezer,
        ...fridgeState.fridge,
        ...fridgeState.room
    ].map(i => i.name);

    if (!allNames.length) {
        showAutoModal("냉장고에 등록된 재료가 없습니다");
        return;
    }

    // 연결 //

}

