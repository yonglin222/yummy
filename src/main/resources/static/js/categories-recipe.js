// ========================================================
//  UI 테스트용 더미 데이터 (⭐ 지금만 사용 ⭐)
//  ⚠ 나중에 백엔드 연동 시 이 배열은 삭제됨!
// ========================================================
const dummyRecipes = [
  {
    id: 1,
    name: "계란볶음밥",
    type: "rice",
    method: "fry",
    cookTime: "10분",
    serving: "1인분",
    ingredients: ["밥 1공기", "계란 2개", "파"],
    seasoning: ["소금", "참기름", "후추"],
    cooking: [
      "계란을 풀고 볶는다.",
      "밥을 넣고 함께 볶는다.",
      "간을 맞춘 후 파를 넣는다.",
    ],
  },
  {
    id: 2,
    name: "시금치나물",
    type: "side",
    method: "blanch",
    cookTime: "8분",
    serving: "2인분",
    ingredients: ["시금치", "파"],
    seasoning: ["소금", "참기름", "깨"],
    cooking: ["시금치를 데친다.", "물기를 짠다.", "양념과 함께 무친다."],
  },
  {
    id: 3,
    name: "부대찌개",
    type: "soup",
    method: "boil",
    cookTime: "20분",
    serving: "2인분",
    ingredients: ["햄", "두부", "양파", "김치"],
    seasoning: ["고추장", "고춧가루", "간장"],
    cooking: [
      "재료를 냄비에 넣는다.",
      "육수를 붓고 끓인다.",
      "양념을 넣고 끓인다.",
    ],
  },
  {
    id: 4,
    name: "닭갈비",
    type: "main",
    method: "fry",
    cookTime: "25분",
    serving: "2인분",
    ingredients: ["닭", "양배추", "고구마"],
    seasoning: ["고추장", "간장", "설탕"],
    cooking: [
      "양념에 닭을 재운다.",
      "채소와 함께 볶는다.",
      "익을 때까지 익힌다.",
    ],
  },
  {
    id: 5,
    name: "토마토 파스타",
    type: "western",
    method: "boil",
    cookTime: "15분",
    serving: "1인분",
    ingredients: ["파스타면", "토마토소스"],
    seasoning: ["소금", "후추"],
    cooking: ["면을 삶는다.", "토마토소스를 데운다.", "면과 섞어 마무리한다."],
  },
  {
    id: 6,
    name: "두부조림",
    type: "main",
    method: "boil",
    cookTime: "15분",
    serving: "2인분",
    ingredients: ["두부", "양파"],
    seasoning: ["고춧가루", "간장", "설탕"],
    cooking: [
      "두부를 굽는다.",
      "양념장을 넣어 졸인다.",
      "국물이 졸면 완성한다.",
    ],
  },
  {
    id: 7,
    name: "김치찌개",
    type: "jjigae",
    method: "boil",
    cookTime: "25분",
    serving: "2인분",
    ingredients: ["김치", "돼지고기", "두부"],
    seasoning: ["고춧가루", "간장"],
    cooking: ["돼지고기를 볶는다.", "김치를 넣고 볶는다.", "물 넣고 끓인다."],
  },
  {
    id: 8,
    name: "고구마 샐러드",
    type: "salad",
    method: "mix",
    cookTime: "10분",
    serving: "1인분",
    ingredients: ["고구마", "마요네즈", "옥수수"],
    seasoning: ["소금", "후추"],
    cooking: ["고구마를 으깬다.", "재료를 넣고 섞는다.", "간을 맞춘다."],
  },
  {
    id: 9,
    name: "감자조림",
    type: "side",
    method: "boil",
    cookTime: "20분",
    serving: "2인분",
    ingredients: ["감자", "당근"],
    seasoning: ["간장", "설탕"],
    cooking: ["감자를 볶는다.", "양념을 넣고 졸인다.", "윤기가 나면 완성."],
  },
  {
    id: 10,
    name: "프렌치 토스트",
    type: "bread",
    method: "pan",
    cookTime: "10분",
    serving: "1인분",
    ingredients: ["식빵", "계란", "우유"],
    seasoning: ["설탕", "시나몬"],
    cooking: ["계란물을 만든다.", "식빵을 적신다.", "팬에 굽는다."],
  },

  {
    id: 11,
    name: "라면",
    type: "noodle",
    method: "boil",
    cookTime: "5분",
    serving: "1인분",
    ingredients: ["라면", "스프"],
    seasoning: ["없음"],
    cooking: ["물을 끓인다.", "면과 스프를 넣는다.", "익으면 완성."],
  },
  {
    id: 12,
    name: "불고기",
    type: "main",
    method: "grill",
    cookTime: "15분",
    serving: "2인분",
    ingredients: ["소고기", "양파"],
    seasoning: ["간장", "설탕", "마늘"],
    cooking: ["고기를 양념에 잰다.", "센 불로 구운다.", "양파와 마무리."],
  },
  {
    id: 13,
    name: "닭칼국수",
    type: "noodle",
    method: "boil",
    cookTime: "20분",
    serving: "2인분",
    ingredients: ["칼국수면", "닭고기"],
    seasoning: ["소금", "후추"],
    cooking: ["닭을 끓인다.", "면을 넣는다.", "간을 맞춘다."],
  },
  {
    id: 14,
    name: "오믈렛",
    type: "western",
    method: "pan",
    cookTime: "10분",
    serving: "1인분",
    ingredients: ["계란", "우유", "버터"],
    seasoning: ["소금", "후추"],
    cooking: ["계란물을 만든다.", "팬에 굽는다.", "접어서 완성."],
  },
  {
    id: 15,
    name: "고등어구이",
    type: "main",
    method: "grill",
    cookTime: "15분",
    serving: "1인분",
    ingredients: ["고등어"],
    seasoning: ["소금"],
    cooking: ["고등어에 소금 뿌린다.", "굽는다.", "완성."],
  },
  {
    id: 16,
    name: "샐러드 파스타",
    type: "salad",
    method: "mix",
    cookTime: "10분",
    serving: "1인분",
    ingredients: ["파스타면", "야채"],
    seasoning: ["올리브유", "레몬즙"],
    cooking: ["면을 삶는다.", "야채와 섞는다.", "올리브유 뿌린다."],
  },
  {
    id: 17,
    name: "꿀호떡",
    type: "dessert",
    method: "pan",
    cookTime: "7분",
    serving: "1인분",
    ingredients: ["호떡믹스", "꿀"],
    seasoning: ["없음"],
    cooking: ["반죽을 만든다.", "꿀을 넣고 싸준다.", "팬에 굽는다."],
  },
  {
    id: 18,
    name: "쿠키",
    type: "dessert",
    method: "bake",
    cookTime: "12분",
    serving: "2인분",
    ingredients: ["밀가루", "버터", "설탕"],
    seasoning: ["바닐라"],
    cooking: ["반죽을 만든다.", "모양을 만든다.", "오븐에 굽는다."],
  },
  {
    id: 19,
    name: "감바스",
    type: "western",
    method: "pan",
    cookTime: "15분",
    serving: "1인분",
    ingredients: ["새우", "올리브유", "마늘"],
    seasoning: ["소금", "후추"],
    cooking: ["마늘을 볶는다.", "새우를 넣는다.", "올리브유와 완성."],
  },
  {
    id: 20,
    name: "참치주먹밥",
    type: "rice",
    method: "mix",
    cookTime: "7분",
    serving: "1인분",
    ingredients: ["밥", "참치", "김"],
    seasoning: ["마요네즈", "참기름"],
    cooking: ["밥에 재료를 넣고 섞는다.", "모양을 만든다.", "김에 감싼다."],
  },
];

const typeToKorean = {
  side: "밑반찬",
  main: "메인반찬",
  soup: "국/탕",
  jjigae: "찌개",
  noodle: "면/만두",
  rice: "밥/떡",
  western: "양식",
  salad: "샐러드",
  bread: "빵",
  dessert: "간식/디저트",
};
const methodToKorean = {
  fry: "볶음",
  pan: "부침",
  boil: "끓이기",
  mix: "무침",
  bibim: "비빔",
  steam: "찜",
  deep: "튀김",
  cook: "삶기",
  grill: "굽기",
  blanch: "데치기",
  etc: "기타",
};

let favorites = JSON.parse(localStorage.getItem("favorites")) || [];
let currentRecipe = null;

let currentPage = 1;
let pageSize = 20;

function renderRecipes(data) {
  const recipeList = document.getElementById("recipeList");
  recipeList.innerHTML = "";

  data.forEach((recipe) => {
    const card = document.createElement("div");
    card.className = "recipe-card";
    card.dataset.type = recipe.type;
    card.dataset.method = recipe.method;
    card.innerHTML = `<h3>${recipe.name}</h3>`;
    card.addEventListener("click", () => showDetailPage(recipe));
    recipeList.appendChild(card);
  });
}

function showDetailPage(recipe) {
  currentRecipe = recipe;

  document.getElementById("recipeListPage").style.display = "none";
  document.getElementById("recipeDetailPage").style.display = "block";
  document.getElementById("pagination").style.display = "none";

  document.getElementById("detailTitle").textContent = recipe.name;
  document.getElementById("detailCookTime").textContent = recipe.cookTime;
  document.getElementById("detailServing").textContent = recipe.serving;

  document.getElementById("detailIngredients").innerHTML = recipe.ingredients
    .map((i) => `<li>${i}</li>`)
    .join("");

  document.getElementById("detailSeasoning").innerHTML = recipe.seasoning
    .map((s) => `<li>${s}</li>`)
    .join("");

  document.getElementById("detailCooking").innerHTML = recipe.cooking
    .map((c) => `<li>${c}</li>`)
    .join("");

  updateFavoriteButton(recipe);
}

function updateFavoriteButton(recipe) {
  const favBtn = document.getElementById("favoriteBtn");
  const textSpan = favBtn.querySelector(".text");
  const icon = favBtn.querySelector("i");

  if (favorites.includes(recipe.id)) {
    favBtn.classList.add("active");
    textSpan.textContent = "즐겨찾기 추가됨";
    icon.className = "fa-solid fa-star";
    favBtn.dataset.hoverText = "즐겨찾기 삭제하기";
  } else {
    favBtn.classList.remove("active");
    textSpan.textContent = "즐겨찾기";
    icon.className = "fa-regular fa-star";
    favBtn.dataset.hoverText = "즐겨찾기 추가하기";
  }
}

function saveFavorites() {
  localStorage.setItem("favorites", JSON.stringify(favorites));
}

document.getElementById("favoriteBtn").onclick = () => {
  if (!currentRecipe) return;

  if (favorites.includes(currentRecipe.id)) {
    favorites = favorites.filter((id) => id !== currentRecipe.id);
  } else {
    favorites.push(currentRecipe.id);
  }
  saveFavorites();
  updateFavoriteButton(currentRecipe);
};

document.getElementById("backToList").addEventListener("click", () => {
  document.getElementById("recipeDetailPage").style.display = "none";
  document.getElementById("recipeListPage").style.display = "block";

  const pagination = document.getElementById("pagination");
  if (pagination) {
    pagination.style.display = "flex";
  }

  
  renderPagedList();
});

document.addEventListener("DOMContentLoaded", () => {
  renderPagedList();

  const urlParams = new URLSearchParams(window.location.search);
  const keyword = urlParams.get("search");

  if (keyword) {
    searchKeyword = keyword;
    document.getElementById("searchInput").value = keyword;
    applyFilters();
  }
});

function renderPagedList() {
  const start = (currentPage - 1) * pageSize;
  const end = start + pageSize;

  const sliced = filteredData.slice(start, end);

  renderRecipes(sliced);

  createPagination({
    containerId: "pagination",
    totalItems: filteredData.length,
    currentPage: currentPage,
    pageSize: pageSize,
    onPageChange: (selectedPage) => {
      currentPage = selectedPage;
      renderPagedList();
    },
  });
}

/*
 // ⭐ 나중에 DB(Spring) 연결할 때 이 부분으로 교체하면 됨

 document.addEventListener("DOMContentLoaded", () => {
   fetch("/api/recipes")
     .then(res => res.json())
     .then(data => {
       recipesData = data;
       renderRecipes(recipesData);
     })
     .catch(err => {
       console.error("레시피 불러오기 실패:", err);
       // 실패 시 더미데이터 사용
       recipesData = dummyRecipes;
       renderRecipes(recipesData);
     });
 });
*/
