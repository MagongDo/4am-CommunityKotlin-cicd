document.addEventListener('DOMContentLoaded', () => {
    const notificationIcon = document.getElementById('notification-icon');
    const customAlarmIcon = document.getElementById('custom-alarm-icon');
    const notificationListPopup = document.getElementById('notification-list-popup');
    const customAlarmListPopup = document.getElementById('custom-alarm-list-popup');
    const customAlarmPopup = document.getElementById('custom-alarm-popup');
    const editCustomAlarmPopup = document.getElementById('edit-custom-alarm-popup'); // 새로운 수정 팝업
    const closeNotificationBtn = document.getElementById('close-notification-btn');
    const closeCustomAlarmListBtn = document.getElementById('close-custom-alarm-list-btn');
    const closeAlarmBtn = document.getElementById('close-alarm-btn');
    const closeEditAlarmBtn = document.getElementById('close-edit-alarm-btn'); // 수정 팝업 닫기 버튼
    const customAlarmList = document.getElementById('custom-alarm-list');
    const customAlarmLists = document.getElementById('custom-alarm-lists');
    const commentLikeList = document.getElementById('comment-like-list');
    const customAlarmSection = document.getElementById('custom-alarm-section');
    const commentLikeSection = document.getElementById('comment-like-section');
    const notificationCount = document.getElementById('notification-count');
    const setAlarmBtn = document.getElementById('set-alarm-btn');
    const saveEditAlarmBtn = document.getElementById('save-edit-alarm-btn'); // 수정 저장 버튼
    const addCustomAlarmBtn = document.getElementById('add-custom-alarm-btn');
    const body = document.querySelector('body');
    const currentUserId = body.getAttribute('data-user-id');
    const friendIcon = document.getElementById('friend-icon');
    const friendListPopup = document.getElementById('friend-list-popup');
    const friendList = document.getElementById('friend-list');
    const friendRequestCount = document.getElementById('friend-request-count');
    const closeFriendListBtn = document.getElementById('close-friend-list-btn');
    const searchFriendBtn = document.getElementById('search-friend-btn');
    const friendSearchPopup = document.getElementById('friend-search-popup');
    const closeFriendSearchBtn = document.getElementById('close-friend-search-btn');
    const friendSearchInput = document.getElementById('friend-search-input');
    const friendSearchResult = document.getElementById('friend-search-result');
    const searchFriendSubmit = document.getElementById('search-friend-submit');


    // WebSocket 연결
    const socket = new WebSocket('wss://localhost:8443/ws/notifications');
    socket.onmessage = function (event) {
        const data = JSON.parse(event.data);
        console.log("Received WebSocket data:", data);
        if (data.type === 'NEW_NOTIFICATION') {
            fetchUnreadNotificationCount();  // 새 알림이 올 때마다 읽지 않은 알림 수 갱신
        }
    };
    friendIcon.addEventListener('click', () => {
        friendListPopup.classList.toggle('d-none');
        loadFriendList();
    });

    socket.onopen = function () {
        console.log('WebSocket 연결이 열렸습니다.');
    };

    socket.onclose = function (event) {
        console.log('WebSocket 연결이 종료되었습니다.', event);
    };

    socket.onerror = function (error) {
        console.error('WebSocket 오류:', error);
    };

    // 일반 알림 리스트 토글
    notificationIcon.addEventListener('click', () => {
        notificationListPopup.classList.toggle('d-none');
        customAlarmListPopup.classList.add('d-none');
        customAlarmPopup.classList.add('d-none');
        editCustomAlarmPopup.classList.add('d-none'); // 수정 팝업도 닫기
        loadGeneralNotifications(); // 일반 알림 리스트 로드
    });

    // 커스텀 알람 리스트 토글
    customAlarmIcon.addEventListener('click', () => {
        customAlarmListPopup.classList.toggle('d-none');
        notificationListPopup.classList.add('d-none');
        customAlarmPopup.classList.add('d-none');
        editCustomAlarmPopup.classList.add('d-none'); // 수정 팝업도 닫기
        loadCustomAlarms(); // 커스텀 알람 리스트 로드
    });

    // 일반 알림 리스트 팝업 닫기
    closeNotificationBtn.addEventListener('click', () => {
        notificationListPopup.classList.add('d-none');
    });

    // 커스텀 알람 리스트 팝업 닫기
    closeCustomAlarmListBtn.addEventListener('click', () => {
        customAlarmListPopup.classList.add('d-none');
    });

    // 커스텀 알람 설정 팝업 닫기
    closeAlarmBtn.addEventListener('click', () => {
        customAlarmPopup.classList.add('d-none');
    });

    // 커스텀 알람 수정 팝업 닫기
    closeEditAlarmBtn.addEventListener('click', () => {
        editCustomAlarmPopup.classList.add('d-none');
    });

    // 알림 추가 버튼 클릭 시 커스텀 알람 설정 팝업 열기
    addCustomAlarmBtn.addEventListener('click', () => {
        customAlarmPopup.classList.remove('d-none');
        customAlarmListPopup.classList.add('d-none');
        editCustomAlarmPopup.classList.add('d-none'); // 다른 팝업 닫기
        setAlarmBtn.textContent = '알람 설정';
        setAlarmBtn.onclick = createCustomAlarm; // 알람 설정 모드로 변경
    });
    friendIcon.addEventListener('click', () => {
        friendListPopup.classList.toggle('d-none');
        loadFriendList();
    });

    // 친구 목록 닫기 버튼
    closeFriendListBtn.addEventListener('click', () => {
        friendListPopup.classList.add('d-none');
    });

    // 돋보기 아이콘 클릭 시 친구 찾기 팝업 열기
    searchFriendBtn.addEventListener('click', () => {
        friendSearchPopup.classList.toggle('d-none');
    });

    // 친구 찾기 팝업 닫기 버튼
    closeFriendSearchBtn.addEventListener('click', () => {
        friendSearchPopup.classList.add('d-none');
    });

    function fetchUnreadNotificationCount() {
        if (!currentUserId) {
            console.error('User ID is null or undefined.');
            return;
        }

        console.log("Fetching unread notification count...");
        fetch('/api/notifications/unread-count', {
            method: 'GET',
            credentials: 'include' // 인증 정보를 포함하여 요청
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error fetching unread count: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Fetched unread count data:", data);
                const unreadCount = data.unreadCount || 0;
                notificationCount.textContent = unreadCount;
                notificationCount.classList.toggle('hidden', unreadCount === 0);
            })
            .catch(error => {
                console.error('Error fetching unread notification count:', error);
                notificationCount.textContent = '0'; // 오류 발생 시 0으로 설정
                notificationCount.classList.add('hidden'); // 알림 배지 숨김
            });
    }

    // 페이지 로드 시마다 알림 수 갱신
    fetchUnreadNotificationCount();
    // 알람 설정 버튼 클릭 핸들러
    function createCustomAlarm() {
        const message = document.getElementById('message').value;
        const selectedDays = Array.from(document.querySelectorAll('input[name="days"]:checked')).map(checkbox => checkbox.value.toUpperCase());
        const selectedTime = document.getElementById('alarm-time').value;
        const isActive = document.getElementById('alarm-active').checked;

        if (!selectedTime || selectedDays.length === 0 || !message) {
            alert("모든 필드를 채워주세요!");
            return;
        }

        const customAlarmNotification = {
            message: message,
            notificationDays: selectedDays,
            reserveAt: selectedTime,
            status: isActive,
            alarmType: "COUSTOM"
        };

        if (socket.readyState === WebSocket.OPEN) {
            socket.send(JSON.stringify(customAlarmNotification));
            alert('알람이 설정되었습니다!');
            customAlarmPopup.classList.add('d-none');
            loadCustomAlarms(); // 커스텀 알람 리스트 다시 로드
        } else {
            alert('WebSocket 연결이 열려 있지 않습니다.');
        }
    }

    // 수정 팝업 열기
    function openEditPopup(alarm) {
        editCustomAlarmPopup.classList.remove('d-none'); // 수정 팝업 열기
        document.getElementById('edit-message').value = alarm.message;
        document.getElementById('edit-alarm-time').value = alarm.reserveAt;
        document.getElementById('edit-alarm-active').checked = alarm.status;

        // 기존 요일 체크박스 선택
        const days = Array.isArray(alarm.notificationDays) ? alarm.notificationDays : JSON.parse(alarm.notificationDays);
        document.querySelectorAll('input[name="edit-days"]').forEach(checkbox => {
            checkbox.checked = days.includes(checkbox.value.toUpperCase());
        });

        // 수정 저장 버튼 이벤트 리스너 연결
        saveEditAlarmBtn.removeEventListener('click', saveEditAlarm); // 기존 이벤트 리스너 제거
        saveEditAlarmBtn.addEventListener('click', () => saveEditAlarm(alarm.id)); // 새 이벤트 리스너 추가
    }

    function saveEditAlarm(alarmId) {
        console.log("Save button clicked for Alarm ID:", alarmId); // 버튼 클릭 확인

        const message = document.getElementById('edit-message').value;
        const selectedDays = Array.from(document.querySelectorAll('input[name="edit-days"]:checked')).map(checkbox => checkbox.value.toUpperCase());
        const selectedTime = document.getElementById('edit-alarm-time').value;
        const isActive = document.getElementById('edit-alarm-active').checked;

        // 각 입력값 확인
        console.log("Message:", message);
        console.log("Selected Days:", selectedDays);
        console.log("Selected Time:", selectedTime);
        console.log("Is Active:", isActive);

        if (!selectedTime || selectedDays.length === 0 || !message) {
            alert('모든 필드를 채워주세요!');
            return;
        }

        const updatedAlarm = {
            id: alarmId,
            message: message,
            notificationDays: selectedDays,
            reserveAt: selectedTime,
            status: isActive,
            alarmType: "COUSTOM"
        };

        console.log("Updated Alarm Data:", updatedAlarm); // 전송할 데이터 확인

        // 서버로 PUT 요청 전송
        fetch(`/api/notifications/custom/${alarmId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedAlarm)
        })
            .then(response => {
                if (response.ok) {
                    console.log('Alarm updated successfully');
                    alert('알람이 수정되었습니다!');
                    editCustomAlarmPopup.classList.add('d-none'); // 수정 팝업 닫기
                    loadCustomAlarms(); // 커스텀 알람 리스트 새로고침
                } else {
                    console.error('Failed to update alarm:', response.status);
                    alert('알람 수정에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error updating alarm:', error);
                alert('알람 수정 중 오류가 발생했습니다.');
            });
    }

    // 일반 알림 리스트 가져오기
    function loadGeneralNotifications() {
        if (!currentUserId) {
            console.error('User ID is null or undefined.');
            return;
        }

        fetch(`/api/notifications`)
            .then(response => response.json())
            .then(data => {
                console.log('Fetched notifications:', data); // 서버에서 가져온 데이터 출력
                renderGeneralNotifications(data);
            })
            .catch(error => console.error('Error loading notifications:', error));
    }

    // 커스텀 알람 리스트 가져오기
    function loadCustomAlarms() {
        if (!currentUserId) {
            console.error('User ID is null or undefined.');
            return;
        }

        fetch(`/api/notifications/custom`)
            .then(response => response.json())
            .then(customData => {
                console.log('Fetched custom alarms:', customData);
                renderCustomAlarms(customData);
            })
            .catch(error => console.error('Error loading custom alarms:', error));
    }

    // 일반 알림 목록 렌더링
    function renderGeneralNotifications(notifications) {
        customAlarmLists.innerHTML = ''; // 사용자 지정 알람 초기화
        commentLikeList.innerHTML = ''; // 댓글 및 좋아요 알림 초기화

        const customAlarms = notifications.filter(n => n.alarmType === 'COUSTOM');
        const otherAlarms = notifications.filter(n => n.alarmType !== 'COUSTOM');

        // 사용자 지정 알람 표시
        if (customAlarms.length > 0) {
            customAlarmSection.classList.remove('d-none'); // 섹션 표시
            customAlarms.forEach(notification => {
                const li = document.createElement('li');
                li.textContent = notification.message || '내용 없음';
                li.dataset.id = notification.id;

                if (!notification.isRead) li.style.fontWeight = 'bold';

                li.addEventListener('click', () => markAsRead(notification.id, li));
                customAlarmLists.appendChild(li);
            });
        } else {
            customAlarmSection.classList.add('d-none'); // 섹션 숨기기
        }

        // 댓글 및 좋아요 알람 표시
        if (otherAlarms.length > 0) {
            commentLikeSection.classList.remove('d-none'); // 섹션 표시
            otherAlarms.forEach(notification => {
                const li = document.createElement('li');
                li.textContent = notification.message || '내용 없음';
                li.dataset.id = notification.id;
                if (!notification.isRead) li.style.fontWeight = 'bold';
                li.addEventListener('click', () => markAsRead(notification.id, li));
                li.addEventListener('click', () => {
                    const targetId = notification.targetId; // targetId가 게시물의 ID라고 가정
                    if (targetId) {
                        window.location.href = `/articles/${targetId}`; // 게시물 페이지로 이동
                    } else {
                        console.warn('targetId가 없습니다.');
                    }
                });
                commentLikeList.appendChild(li);
            });
        } else {
            commentLikeSection.classList.add('d-none'); // 섹션 숨기기
        }

        // 읽지 않은 알림 수 업데이트
        const unreadCount = notifications.filter(n => !n.isRead).length;
        notificationCount.textContent = unreadCount;
        notificationCount.classList.toggle('hidden', unreadCount === 0);
    }

    // 커스텀 알람 목록 렌더링
    function renderCustomAlarms(customAlarms) {
        customAlarmList.innerHTML = ''; // 사용자 지정 알람 초기화

        if (customAlarms.length > 0) {
            customAlarmListPopup.classList.remove('d-none'); // 팝업 표시

            customAlarms.forEach(alarm => {
                const li = document.createElement('li');
                li.classList.add('d-flex', 'justify-content-between', 'align-items-center');
                li.dataset.id = alarm.id;
                li.dataset.time = alarm.reserveAt;
                li.dataset.message = alarm.message;
                li.dataset.days = JSON.stringify(alarm.notificationDays);

                // 알람 메시지
                const messageSpan = document.createElement('span');
                messageSpan.textContent = `${alarm.message} (${alarm.reserveAt})`;
                messageSpan.classList.add('flex-grow-1');

                // 수정 버튼 생성
                const editBtn = document.createElement('button');
                editBtn.textContent = '수정';
                editBtn.classList.add('btn', 'btn-warning', 'btn-sm', 'edit-alarm-btn', 'ml-2');
                editBtn.addEventListener('click', () => openEditPopup(alarm));

                // 삭제 버튼 생성
                const deleteBtn = document.createElement('button');
                deleteBtn.textContent = '삭제';
                deleteBtn.classList.add('btn', 'btn-danger', 'btn-sm', 'delete-alarm-btn', 'ml-2');
                deleteBtn.addEventListener('click', () => {
                    if (confirm('정말 이 알람을 삭제하시겠습니까?')) {
                        deleteCustomAlarm(alarm.id);
                    }
                });

                li.appendChild(messageSpan);
                li.appendChild(editBtn);
                li.appendChild(deleteBtn);

                if (!alarm.isRead) li.style.fontWeight = 'bold';

                customAlarmList.appendChild(li);
            });
        } else {
            const li = document.createElement('li');
            li.textContent = '설정된 커스텀 알람이 없습니다.';
            customAlarmList.appendChild(li);
        }
    }

    // 알람 삭제 함수
    function deleteCustomAlarm(alarmId) {
        fetch(`/api/notifications/custom/${alarmId}`, {
            method: 'DELETE',
        })
            .then(response => {
                if (response.ok) {
                    alert('알람이 삭제되었습니다!');
                    loadCustomAlarms(); // 리스트 새로고침
                } else {
                    alert('알람 삭제에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error deleting alarm:', error);
                alert('알람 삭제 중 오류가 발생했습니다.');
            });
    }

    // 알람 상태 토글 함수
    function toggleAlarmStatus(alarmId, newStatus, buttonElement) {
        fetch(`/api/notifications/custom/${alarmId}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newStatus)
        })
            .then(response => {
                if (response.ok) {
                    buttonElement.textContent = newStatus ? '활성화' : '비활성화';
                    buttonElement.classList.toggle('btn-success', newStatus);
                    buttonElement.classList.toggle('btn-secondary', !newStatus);
                    alert('알람 상태가 업데이트되었습니다.');
                } else {
                    alert('알람 상태 업데이트에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error updating alarm status:', error);
                alert('알람 상태 업데이트 중 오류가 발생했습니다.');
            });
    }

    // 알림을 읽음으로 표시하는 함수
    function markAsRead(notificationId, liElement) {
        fetch(`/api/notifications/read/${notificationId}`, {
            method: 'PUT',
            credentials: 'include',
        })
            .then(response => {
                if (response.ok) {
                    liElement.style.fontWeight = 'normal';
                    let currentCount = parseInt(notificationCount.textContent) || 0;
                    currentCount = currentCount > 0 ? currentCount - 1 : 0;
                    notificationCount.textContent = currentCount;
                    if (currentCount === 0) {
                        notificationCount.classList.add('hidden');
                    }
                }
            })
            .catch(error => console.error('Error marking notification as read:', error));

    }

    // 웹소켓으로부터 알림 수신
    socket.onmessage = function (event) {
        const data = JSON.parse(event.data); // 수신한 JSON 데이터를 파싱
        console.log('새로운 알림:', data);

        let currentCount = parseInt(notificationCount.textContent) || 0;
        currentCount += 1;
        notificationCount.textContent = currentCount;
        notificationCount.classList.remove('hidden');
        switch (data.dataType) {
            case 'Notification':
                handleNotification(data);
                break;
            case 'CoustomAlarm':
                handleCustomAlarm(data);
                break;
            default:
                console.warn(`알 수 없는 알림 타입입니다: ${data.dataType}`);
        }
        fetchUnreadNotificationCount();
    };

    // 일반 알림 처리
    function handleNotification(notification) {
        if (!notificationListPopup.classList.contains('d-none')) {
            const li = document.createElement('li');
            li.textContent = `댓글 및 좋아요 알람: ${notification.message}`;
            commentLikeList.appendChild(li);
            li.style.fontWeight = 'bold';
        }
    }

    // 사용자 지정 알람 처리
    function handleCustomAlarm(alarm) {
        if (!customAlarmListPopup.classList.contains('d-none')) {
            const li = document.createElement('li');
            li.textContent = `사용자 지정 알람: ${alarm.message}`;
            li.dataset.id = alarm.id;

            const toggleBtn = document.createElement('button');
            toggleBtn.classList.add('btn', 'btn-sm', alarm.status ? 'btn-success' : 'btn-secondary', 'ml-2');
            toggleBtn.textContent = alarm.status ? '활성화' : '비활성화';
            toggleBtn.addEventListener('click', (event) => {
                event.stopPropagation();
                toggleAlarmStatus(alarm.id, !alarm.status, toggleBtn);
            });

            li.appendChild(toggleBtn);

            if (!alarm.isRead) li.style.fontWeight = 'bold';
            customAlarmList.appendChild(li);
        }
    }
    function loadFriendList() {
        fetch('/api/friends/list')
            .then(response => response.json())
            .then(data => {
                friendList.innerHTML = ''; // 기존 목록 초기화
                data.forEach(friend => {
                    const li = document.createElement('li');
                    li.classList.add('friend-item');
                    li.textContent = `${friend.name} - ${friend.status === 'ONLINE' ? '🟢' : '🔴'}`;
                    friendList.appendChild(li);
                });
            })
            .catch(error => console.error('Error loading friend list:', error));
    }
    searchFriendSubmit.addEventListener('click', () => {
        const email = friendSearchInput.value;
        if (email) {
            fetch(`/api/friends/search?email=${encodeURIComponent(email)}`)
                .then(response => response.json())
                .then(data => {
                    friendSearchResult.innerHTML = ''; // 기존 검색 결과 초기화
                    data.forEach(user => {
                        const li = document.createElement('li');
                        li.textContent = user.name;
                        friendSearchResult.appendChild(li);
                    });
                })
                .catch(error => console.error('Error searching friends:', error));
        } });
    // 친구 요청 수락/거절 처리 함수
    function handleFriendRequest(notificationId, isAccepted) {
        const action = isAccepted ? 'accept' : 'reject';
        fetch(`/api/friends/requests/${notificationId}/${action}`, {
            method: 'PUT',
            credentials: 'include',
        })
            .then(response => {
                if (response.ok) {
                    alert(isAccepted ? '친구 요청을 수락했습니다.' : '친구 요청을 거절했습니다.');
                    loadFriendRequests();
                } else {
                    alert('친구 요청 처리에 실패했습니다.');
                }
            })
            .catch(error => console.error('Error handling friend request:', error));
    }

    // 친구 요청 알림 처리
    function loadFriendRequests() {
        fetch('/api/notifications?type=FRIEND')
            .then(response => response.json())
            .then(data => {
                friendRequestCount.textContent = data.length;
                friendList.innerHTML = '';  // 초기화

                data.forEach(notification => {
                    const li = document.createElement('li');
                    li.textContent = notification.message;

                    const acceptBtn = document.createElement('button');
                    acceptBtn.textContent = '수락';
                    acceptBtn.classList.add('btn', 'btn-success', 'btn-sm');
                    acceptBtn.onclick = () => handleFriendRequest(notification.id, true);

                    const rejectBtn = document.createElement('button');
                    rejectBtn.textContent = '거절';
                    rejectBtn.classList.add('btn', 'btn-danger', 'btn-sm');
                    rejectBtn.onclick = () => handleFriendRequest(notification.id, false);

                    li.appendChild(acceptBtn);
                    li.appendChild(rejectBtn);
                    friendList.appendChild(li);
                });
            })
            .catch(error => console.error('Error loading friend requests:', error));
    }

    // 페이지 로드 시 친구 요청 목록 로드
    loadFriendRequests();
});
