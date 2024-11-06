// article.js

// 임시저장 버튼과 임시저장된 게시글 수 버튼
const saveTempButton = document.getElementById('save-temp-btn');
const temporarySaveCountBtn = document.getElementById('temporary-save-count-btn');
const temporarySaveCount = document.getElementById('temporary-save-count');
const temporaryArticlesList = document.getElementById('temporary-articles-list');

// 임시저장된 게시글 목록 버튼 클릭 시 모달 열기
if (temporarySaveCountBtn) {
    temporarySaveCountBtn.addEventListener('click', () => {
        fetchTemporaryArticles();
        $('#temporaryArticlesModal').modal('show');
    });
}

// 임시저장된 게시글 목록 조회
function fetchTemporaryArticles() {
    fetch('/api/article/temporary', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('access_token')
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to fetch temporary articles');
        })
        .then(data => {
            // 목록 초기화
            temporaryArticlesList.innerHTML = '';

            if (data.length === 0) {
                temporaryArticlesList.innerHTML = '<li class="list-group-item">임시저장된 게시글이 없습니다.</li>';
                return;
            }

            // 임시저장된 게시글 목록 추가
            data.forEach(article => {
                const listItem = document.createElement('li');
                listItem.className = 'list-group-item d-flex justify-content-between align-items-center';

                const titleLink = document.createElement('a');
                titleLink.href = `/new-article?id=${article.id}`; // 임시게시글 수정 페이지로 이동
                titleLink.textContent = article.title || '제목 없음';

                const deleteIcon = document.createElement('i');
                deleteIcon.className = 'fas fa-trash text-danger';
                deleteIcon.title = '삭제';
                deleteIcon.style.cursor = 'pointer';
                deleteIcon.addEventListener('click', () => {
                    deleteTemporaryArticle(article.id, listItem);
                });

                listItem.appendChild(titleLink);
                listItem.appendChild(deleteIcon);
                temporaryArticlesList.appendChild(listItem);
            });
        })
        .catch(error => {
            console.error('Error fetching temporary articles:', error);
            temporaryArticlesList.innerHTML = '<li class="list-group-item text-danger">임시저장된 게시글을 불러오는 중 오류가 발생했습니다.</li>';
        });
}

// 임시저장된 게시글 삭제
function deleteTemporaryArticle(articleId, listItem) {
    const userConfirmed = confirm('임시저장된 게시글을 삭제하시겠습니까?');
    if (!userConfirmed) return;

    fetch(`/api/article/temporary/${articleId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('access_token')
        }
    })
        .then(response => {
            if (response.ok) {
                alert('임시저장된 게시글이 삭제되었습니다.');
                // 리스트에서 삭제된 항목 제거
                listItem.remove();
                // 임시저장된 게시글 수 갱신
                fetchTemporarySaveCount();
            } else {
                return response.json().then(data => { throw new Error(data.message || 'Failed to delete') });
            }
        })
        .catch(error => {
            console.error('Error deleting temporary article:', error);
            alert('임시저장된 게시글 삭제에 실패했습니다.');
        });
}

// 임시저장된 게시글 수 조회 및 표시
function fetchTemporarySaveCount() {
    fetch('/api/article/temporary', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('access_token')
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to fetch temporary articles count');
        })
        .then(data => {
            temporarySaveCount.textContent = data.length;
        })
        .catch(error => {
            console.error('Error fetching temporary articles count:', error);
            temporarySaveCount.textContent = '0';
        });
}

// 초기 로드 시 임시저장된 게시글 수 갱신
document.addEventListener('DOMContentLoaded', () => {
    fetchTemporarySaveCount();
});

// 임시저장 버튼 클릭 시 임시저장 API 호출
if (saveTempButton) {
    saveTempButton.addEventListener('click', () => {
        const title = document.getElementById('title').value;
        const content = editorInstance.getData();

        if (!title || !content) {
            alert('제목과 내용을 모두 입력해주세요.');
            return;
        }

        const formData = new FormData();
        formData.append('request', JSON.stringify({ title, content }));

        // 파일 업로드는 CKEditor를 통해 처리되므로 별도의 파일 입력 필드가 필요 없습니다.

        fetch('/api/article/temporary', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('access_token')
            },
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                return response.json().then(data => { throw new Error(data.message || 'Failed to save temporary article') });
            })
            .then(data => {
                alert('임시저장되었습니다.');
                fetchTemporarySaveCount();
            })
            .catch(error => {
                console.error('Error saving temporary article:', error);
                alert('임시저장에 실패했습니다.');
            });
    });
}
// 삭제 기능
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        const userConfirmed = confirm('삭제하시겠습니까?');
        let id = document.getElementById('article-id').value;

        function success() {
            alert('삭제가 완료되었습니다.');
            location.replace('/articles');
        }

        // function fail(response) {
        //     response.text().then(text => {
        //         alert('삭제에 실패했습니다');
        //         location.replace('/articles');
        //     });
        // }
        function fail(response) {
            console.log("Fail function called");  // fail 함수 호출 확인
            response.json().then(data => {  // JSON 응답 처리
                console.log("Response JSON:", data);
                if (response.status === 500) {
                    alert('서버에서 문제가 발생했습니다: ' + data.message);
                } else {
                    alert('삭제에 실패했습니다: ' + data.message);
                }
                location.replace('/articles');
            }).catch(error => {
                console.log("JSON 처리 실패:", error);
                alert('삭제 과정에서 문제가 발생했습니다.');
            });
        }

        if (userConfirmed) {
            httpRequest('DELETE', `/api/article/${id}`, null, success, fail);
        } else {
            event.preventDefault();
        }

    });
}
// 취소 버튼 기능 수정: 새 글 작성 시 임시저장된 글 삭제, 글 수정 시 저장되지 않음
const cancelButton = document.getElementById('cancel-btn');

if (cancelButton) {
    cancelButton.addEventListener('click', event => {
        const userConfirmed = confirm('글 작성을 취소하시겠습니까? 작성 중인 글이 저장되지 않습니다.');
        let articleId = document.getElementById('article-id').value;

        if (userConfirmed) {
            if (articleId) {  // 글 수정 중 취소
                fetch(`/api/article/${articleId}/cancel-edit`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('access_token')
                    }
                })
                    .then(response => {
                        if (response.ok) {
                            alert('글 수정이 취소되었습니다.');
                            location.replace(`/articles/${articleId}`);
                        } else {
                            alert('취소 작업 중 문제가 발생했습니다.');
                        }
                    })
                    .catch(error => {
                        console.error('취소 작업 오류:', error);
                        alert('취소 작업 중 오류가 발생했습니다.');
                    });
            } else {  // 새 글 작성 취소
                // 현재 작성 중인 글을 임시저장하지 않고 취소하므로 별도의 삭제 API 호출은 필요 없습니다.
                alert('새 글 작성이 취소되었습니다.');
                location.replace('/articles');
            }
        }
    });
}

// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        const userConfirmed = confirm('수정하시겠습니까?'); // 확인창 띄우기
        if (!userConfirmed) {
            event.preventDefault(); // 취소 시 아무 동작도 하지 않음
            return;
        }

        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        const body = new FormData();
        body.append('request', JSON.stringify({
            title: document.getElementById('title').value,
            content: editorInstance.getData()  // CKEditor의 내용을 가져옴
        }));

        // 파일 업로드는 CKEditor를 통해 처리되므로 별도의 파일 입력 필드가 필요 없습니다.

        // 첫 번째 요청 (수정 API)
        function updateSuccess() {
            // 수정 완료 후 finalize-edit API 호출
            finalizeEdit(id);
        }

        function updateFail(response) {
            response.text().then(text => {
                alert('수정 실패했습니다.');
                location.replace(`/articles/${id}`);
            });
        }

        httpRequest('PUT', `/api/article/${id}`, body, updateSuccess, updateFail);
    });

    // finalize-edit API 호출 함수
    function finalizeEdit(id) {
        function success() {
            alert('수정이 완료되었습니다.');
            location.replace(`/articles/${id}`);
        }

        function fail(response) {
            response.text().then(text => {
                alert('수정 완료 처리에 실패했습니다.');
                location.replace(`/articles/${id}`);
            });
        }

        httpRequest('PUT', `/api/article/${id}/finalize-edit`, null, success, fail);
    }
}

// 등록 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {
        const body = new FormData();
        // CKEditor 5에서 데이터 가져오기 (index.js에서 초기화된 전역 변수 사용)

        body.append('request', JSON.stringify({
            title: document.getElementById('title').value,
            content: editorInstance.getData()   // CKEditor5에서 데이터 가져오기
        }));

        // 파일 업로드는 CKEditor를 통해 처리되므로 별도의 파일 입력 필드가 필요 없습니다.

        fetch('/api/article', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('access_token')
            },
            body: body
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                return response.json().then(data => { throw new Error(data.message || 'Failed to create article') });
            })
            .then(data => {
                alert('등록 완료되었습니다.');
                location.replace('/articles');
            })
            .catch(error => {
                console.error('Error creating article:', error);
                alert('등록 실패했습니다. 로그인 후 이용해주세요!');
                location.replace('/articles');
            });
    });
}

// 등록 버튼 (임시저장된 글을 실제 게시글로 전환)
// 등록 버튼 (새 글 작성 또는 임시저장 글 수정 시 표시)
// const createButton = document.getElementById('create-btn');
//
// if (createButton) {
//     createButton.addEventListener('click', event => {
//         const body = new FormData();
//         body.append('request', JSON.stringify({
//             title: document.getElementById('title').value,
//             content: editorInstance.getData()
//         }));
//
//         fetch('/api/article', {
//             method: 'POST',
//             headers: {
//                 'Authorization': 'Bearer ' + localStorage.getItem('access_token')
//             },
//             body: body
//         })
//             .then(response => {
//                 if (response.ok) {
//                     return response.json();
//                 }
//                 return response.json().then(data => { throw new Error(data.message || 'Failed to create article') });
//             })
//             .then(data => {
//                 alert('등록 완료되었습니다.');
//                 location.replace('/articles');
//             })
//             .catch(error => {
//                 console.error('Error creating article:', error);
//                 alert('등록 실패했습니다. 로그인 후 이용해주세요!');
//                 location.replace('/articles');
//             });
//     });
// }



// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.trim();
        var dic = item.split('=');
        if (key === dic[0]) {
            result = decodeURIComponent(dic[1]);
            return true;
        }
    });
    return result;
}

// HTTP 요청을 보내는 함수
let retryCount = 0;  // 재시도 횟수 제한

function httpRequest(method, url, body, success, fail) {
    const headers = {
        Authorization: 'Bearer ' + localStorage.getItem('access_token'),
        // Content-Type 설정하지 않음, FormData 사용 시 브라우저가 자동으로 설정함
    };

    fetch(url, {
        method: method,
        headers: headers,  // Content-Type 설정하지 않음
        body: body, // FormData 객체 전달
        // credentials: 'include'
    }).then(response => {
        if (response.ok) {
            return success();
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token && retryCount < 3) {
            retryCount++;
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
                // credentials: 'include'
            })
                .then(res => res.ok ? res.json() : Promise.reject())
                .then(result => {
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequest(method, url, body, success, fail); // 토큰 갱신 후 다시 요청
                })
                .catch(() => fail(response));
        } else {
            return fail(response);
        }
    }).catch(error => fail(error));
}

// 알림
document.addEventListener('DOMContentLoaded', () => {
    const notificationCount = document.getElementById('notification-count');

    // 읽지 않은 알림 수를 가져오는 함수 정의
    function fetchUnreadNotificationCount() {
        fetch('/api/notifications/unread-count', {
            method: 'GET',
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error fetching unread count: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Fetched unread count in article.js:", data);
                const unreadCount = data.unreadCount || 0;
                notificationCount.textContent = unreadCount;
                notificationCount.classList.toggle('hidden', unreadCount === 0);
            })
            .catch(error => {
                console.error('Error fetching unread notification count in article.js:', error);
                notificationCount.textContent = '0';
                notificationCount.classList.add('hidden');
            });
    }

    // 페이지 로드 시마다 알림 수 갱신
    fetchUnreadNotificationCount();
});
