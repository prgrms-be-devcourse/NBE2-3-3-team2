document.addEventListener('DOMContentLoaded', () => {
    const searchIcon = document.getElementById('search-icon'); // 돋보기 아이콘
    const searchPopup = document.getElementById('search-popup'); // 검색 팝업
    const closeSearch = document.getElementById('close-search'); // 팝업 닫기 버튼
    const searchInput = document.getElementById('search-input'); // 검색 입력 필드
    const searchForm = document.getElementById('search-form'); // 검색 폼
    const suggestionsList = document.getElementById('search-suggestions'); // 검색 제안 목록

    // 돋보기 아이콘 클릭 시 검색창 팝업 열기
    searchIcon.addEventListener('click', () => {
        searchPopup.classList.remove('d-none'); // 팝업 표시
        searchPopup.style.display = 'flex';
        searchInput.focus(); // 검색 입력창에 포커스
    });

    // 닫기 버튼 클릭 시 검색창 팝업 닫기
    closeSearch.addEventListener('click', () => {
        searchPopup.classList.add('d-none'); // 팝업 숨김
        searchPopup.style.display = 'none';
    });

    // 검색 입력 이벤트 (자동완성 기능)
    searchInput.addEventListener('input', async () => {
        const query = searchInput.value.trim();
        if (query.length > 0) {
            try {
                const response = await fetch(`/api/search/suggestions?query=${query}`);
                const suggestions = await response.json();
                suggestionsList.innerHTML = suggestions.map(
                    (suggestion) =>
                        `<li class="list-group-item" onclick="location.href='/movie/${suggestion.id}'">${suggestion.name}</li>`
                ).join('');
            } catch (error) {
                suggestionsList.innerHTML = '<li class="list-group-item text-danger">검색어를 불러오지 못했습니다.</li>';
            }
        } else {
            suggestionsList.innerHTML = ''; // 입력이 비어있으면 목록 비우기
        }
    });

    // 검색 폼 버튼 이벤트
    searchForm.addEventListener('submit', (event) => {
        if (!searchInput.value.trim()) {
            event.preventDefault(); // 입력이 비어 있으면 폼 제출 막기
            alert('검색어를 입력해주세요.');
        }
    });
});

// document.addEventListener('DOMContentLoaded', () => {
//     const searchIcon = document.getElementById('search-icon'); // 돋보기 아이콘
//     const searchPopup = document.getElementById('search-popup'); // 검색 팝업
//     const closeSearch = document.getElementById('close-search'); // 팝업 닫기 버튼
//     const searchInput = document.getElementById('search-input'); // 검색 입력 필드
//     const suggestionsList = document.getElementById('search-suggestions'); // 검색 제안 목록
//
//     // 돋보기 아이콘 클릭 시 검색창 팝업 열기
//     searchIcon.addEventListener('click', () => {
//         console.log('Search icon clicked');
//         searchPopup.classList.remove('d-none'); // 숨김 클래스 제거
//         searchPopup.style.display = 'flex'; // 팝업 표시
//         searchInput.focus(); // 검색 입력창에 포커스
//     });
//
//     // 닫기 버튼 클릭 시 검색창 팝업 닫기
//     closeSearch.addEventListener('click', () => {
//         console.log('Close button clicked');
//         searchPopup.classList.add('d-none'); // 숨김 클래스 추가
//         searchPopup.style.display = 'none'; // 팝업 숨김
//     });
//
//     // 검색 입력 이벤트
//     searchInput.addEventListener('input', async () => {
//         const query = searchInput.value.trim(); // 입력 값
//         if (query.length > 0) {
//             try {
//                 // AJAX 요청으로 연관 검색어 가져오기
//                 const response = await fetch(`/api/search/suggestions?query=${query}`);
//                 const suggestions = await response.json();
//
//                 // 검색 결과 표시
//                 suggestionsList.innerHTML = suggestions.map(
//                     (suggestion) =>
//                         `<li class="list-group-item" onclick="location.href='/movie/${suggestion.id}'">${suggestion.name}</li>`
//                 ).join('');
//             } catch (error) {
//                 console.error('Error fetching suggestions:', error);
//                 suggestionsList.innerHTML = '<li class="list-group-item text-danger">검색어를 불러오지 못했습니다.</li>';
//             }
//         } else {
//             suggestionsList.innerHTML = ''; // 검색어가 없으면 목록 비우기
//         }
//     });
// });