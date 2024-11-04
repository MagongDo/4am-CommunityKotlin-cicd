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

    const friendNotificationCount = document.getElementById('friend-notification-count');
    const friendIcon = document.getElementById('friend-icon');
    const friendPopup = document.getElementById('friend-popup');
    const searchPopup = document.getElementById('search-popup');
    const searchBtn = document.getElementById('search-btn');
    const searchInput = document.getElementById('search-input');
    const sendRequestBtn = document.getElementById('send-request-btn');
    const closeFriendPopupBtn = document.getElementById('close-friend-popup');
    const closeSearchPopupBtn = document.getElementById('close-search-popup');

    // 읽지 않은 일반 알림과 친구 알림의 카운트를 저장하는 변수
    let unreadGeneralCount = 0;
    let unreadFriendCount = 0;

    // WebSocket 연결
    const socket = new WebSocket('wss://localhost:8443/ws/notifications');

    socket.onopen = function () {
        console.log('WebSocket 연결이 열렸습니다.');
    };

    socket.onclose = function (event) {
        console.log('WebSocket 연결이 종료되었습니다.', event);
    };

    socket.onerror = function (error) {
        console.error('WebSocket 오류:', error);
    };

    // 웹소켓으로부터 알림 수신
    socket.onmessage = function (event) {
        const data = JSON.parse(event.data);
        console.log("Received WebSocket data:", data);
        if (data.type === 'NEW_NOTIFICATION') {
            fetchUnreadNotificationCount();  // 새 알림이 올 때마다 읽지 않은 알림 수 갱신
        } else if (data.type === 'NEW_FRIEND_NOTIFICATION') {
            fetchUnreadFriendNotificationCount();  // 친구 알림 수 갱신
        } else {
            console.warn(`알 수 없는 알림 타입입니다: ${data.type}`);
        }
    };

    // 알림 카운트를 업데이트하는 함수
    function updateNotificationCounts() {
        notificationCount.textContent = unreadGeneralCount;
        notificationCount.classList.toggle('hidden', unreadGeneralCount === 0);

        friendNotificationCount.textContent = unreadFriendCount;
        friendNotificationCount.classList.toggle('hidden', unreadFriendCount === 0);
    }

    // 일반 알림 리스트 토글
    notificationIcon.addEventListener('click', () => {
        notificationListPopup.classList.toggle('d-none');
        customAlarmListPopup.classList.add('d-none');
        customAlarmPopup.classList.add('d-none');
        editCustomAlarmPopup.classList.add('d-none'); // 수정 팝업도 닫기
        friendPopup.classList.add('d-none'); // 친구 팝업 닫기
        searchPopup.classList.add('d-none'); // 검색 팝업 닫기
        loadGeneralNotifications(); // 일반 알림 리스트 로드
    });

    // 커스텀 알람 리스트 토글
    customAlarmIcon.addEventListener('click', () => {
        customAlarmListPopup.classList.toggle('d-none');
        notificationListPopup.classList.add('d-none');
        customAlarmPopup.classList.add('d-none');
        editCustomAlarmPopup.classList.add('d-none'); // 수정 팝업도 닫기
        friendPopup.classList.add('d-none'); // 친구 팝업 닫기
        searchPopup.classList.add('d-none'); // 검색 팝업 닫기
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

    friendIcon.addEventListener('click', () => {
        friendPopup.classList.toggle('d-none');
        notificationListPopup.classList.add('d-none');
        customAlarmListPopup.classList.add('d-none');
        customAlarmPopup.classList.add('d-none');
        editCustomAlarmPopup.classList.add('d-none');
        searchPopup.classList.add('d-none'); // 검색 팝업 닫기
        loadFriendList(); // 친구 목록 로드
        loadFriendNotifications(); // 친구 요청 알림 로드
    });

    searchBtn.addEventListener('click', () => {
        searchPopup.classList.remove('d-none');
        friendPopup.classList.add('d-none'); // 친구 팝업 닫기
        notificationListPopup.classList.add('d-none');
        customAlarmListPopup.classList.add('d-none');
        customAlarmPopup.classList.add('d-none');
        editCustomAlarmPopup.classList.add('d-none');
    });

    closeFriendPopupBtn.addEventListener('click', () => {
        friendPopup.classList.add('d-none');
    });

    closeSearchPopupBtn.addEventListener('click', () => {
        searchPopup.classList.add('d-none');
    });

    // 알림 추가 버튼 클릭 시 커스텀 알람 설정 팝업 열기
    addCustomAlarmBtn.addEventListener('click', () => {
        customAlarmPopup.classList.remove('d-none');
        customAlarmListPopup.classList.add('d-none');
        editCustomAlarmPopup.classList.add('d-none'); // 다른 팝업 닫기
        setAlarmBtn.textContent = '알람 설정';
        setAlarmBtn.onclick = createCustomAlarm; // 알람 설정 모드로 변경
    });

    // 일반 알림 읽지 않은 수 가져오기
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
                unreadGeneralCount = data.unreadCount || 0;
                updateNotificationCounts();
            })
            .catch(error => {
                console.error('Error fetching unread notification count:', error);
                unreadGeneralCount = 0;
                updateNotificationCounts();
            });
    }

    // 친구 알림 읽지 않은 수 가져오기
    function fetchUnreadFriendNotificationCount() {
        if (!currentUserId) {
            console.error('User ID is null or undefined.');
            return;
        }

        console.log("Fetching unread friend notification count...");
        fetch('/api/notifications/unread-count/friend', {
            method: 'GET',
            credentials: 'include' // 인증 정보를 포함하여 요청
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error fetching unread friend notification count: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Fetched unread friend notification count data:", data);
                unreadFriendCount = data.unreadCount || 0;
                updateNotificationCounts();
            })
            .catch(error => {
                console.error('Error fetching unread friend notification count:', error);
                unreadFriendCount = 0;
                updateNotificationCounts();
            });
    }

    // 페이지 로드 시마다 알림 수 갱신
    fetchUnreadNotificationCount();
    fetchUnreadFriendNotificationCount();

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
            body: JSON.stringify(updatedAlarm),
            credentials: 'include' // 인증 정보를 포함하여 요청
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

        fetch(`/api/notifications`, {
            credentials: 'include' // 인증 정보를 포함하여 요청
        })
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

        fetch(`/api/notifications/custom`, {
            credentials: 'include' // 인증 정보를 포함하여 요청
        })
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
        const otherAlarms = notifications.filter(n =>
            n.alarmType === 'COMMENT' || n.alarmType === 'RECOMMENT' || n.alarmType === 'LIKE'
        );

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
                    if (targetId != null) {
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

        // 읽지 않은 일반 알림 수 업데이트
        const unreadCustomAlarmsCount = customAlarms.filter(n => !n.isRead).length;
        const unreadOtherAlarmsCount = otherAlarms.filter(n => !n.isRead).length;
        unreadGeneralCount = unreadCustomAlarmsCount + unreadOtherAlarmsCount;
        updateNotificationCounts();
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
            credentials: 'include' // 인증 정보를 포함하여 요청
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

    // 일반 알림을 읽음으로 표시하는 함수
    function markAsRead(notificationId, liElement) {
        fetch(`/api/notifications/read/${notificationId}`, {
            method: 'PUT',
            credentials: 'include',
        })
            .then(response => {
                if (response.ok) {
                    liElement.style.fontWeight = 'normal';
                    unreadGeneralCount = unreadGeneralCount > 0 ? unreadGeneralCount - 1 : 0;
                    updateNotificationCounts();
                }
            })
            .catch(error => console.error('Error marking notification as read:', error));
    }

    // 친구 관련 js

    // 친구 목록 불러오기
    async function loadFriendList() {
        try {
            const response = await fetch("/api/friends/list", {
                credentials: 'include' // 인증 정보를 포함하여 요청
            });
            if (response.ok) {
                const friends = await response.json();
                displayFriendList(friends);
            } else {
                console.error("친구 목록 불러오기 실패:", response.statusText);
            }
        } catch (error) {
            console.error("친구 목록 불러오기 실패:", error);
        }
    }

    function displayFriendList(friends) {
        const friendListDiv = document.getElementById("friend-list");
        friendListDiv.innerHTML = "";

        // 온라인 상태에 따라 정렬: 온라인이 먼저 오도록
        friends.sort((a, b) => {
            if (a.status === "ONLINE" && b.status !== "ONLINE") return -1;
            if (a.status !== "ONLINE" && b.status === "ONLINE") return 1;
            return 0;
        });

        friends.forEach(friend => {
            const friendItem = document.createElement("div");
            friendItem.className = "friend-item";

            // 상태를 나타내는 점 추가
            const statusDot = document.createElement("span");
            statusDot.className = friend.status === "ONLINE" ? "status-dot online" : "status-dot offline";
            statusDot.title = friend.status === "ONLINE" ? "온라인" : "오프라인"; // 툴팁 추가

            // 친구 이름 추가
            const friendName = document.createElement("span");
            friendName.className = "friend-name";
            friendName.textContent = friend.name;

            // 상태 점과 친구 이름을 친구 아이템에 추가
            friendItem.appendChild(statusDot);
            friendItem.appendChild(friendName);

            friendListDiv.appendChild(friendItem);
        });
    }

    // 친구 요청 알림 불러오기
    async function loadFriendNotifications() {
        try {
            const response = await fetch("/api/friends/requests", {
                credentials: 'include' // 인증 정보를 포함하여 요청
            });
            if (response.ok) {
                const notifications = await response.json();
                displayFriendNotifications(notifications);

                // 읽지 않은 친구 알림 수 업데이트
                unreadFriendCount = notifications.filter(n => !n.isRead).length;
                updateNotificationCounts();
            } else {
                console.error("친구 신청 알림 불러오기 실패:", response.statusText);
            }
        } catch (error) {
            console.error("친구 신청 알림 불러오기 실패:", error);
        }
    }

    function displayFriendNotifications(notifications) {
        const notificationListDiv = document.getElementById("friend-notification-list");
        notificationListDiv.innerHTML = "";

        notifications.forEach(notification => {
            const notificationItem = document.createElement("div");
            notificationItem.className = "notification-item";
            notificationItem.innerHTML = `
                <span>${notification.fromUserEmail} 님의 친구 신청</span>
                <button class="accept-btn btn btn-success btn-sm" data-id="${notification.id}">수락</button>
                <button class="reject-btn btn btn-danger btn-sm" data-id="${notification.id}">거절</button>
            `;
            notificationListDiv.appendChild(notificationItem);
        });

        // 수락 및 거절 버튼에 이벤트 리스너 추가
        document.querySelectorAll('.accept-btn').forEach(button => {
            button.addEventListener('click', () => {
                const notificationId = button.getAttribute('data-id');
                acceptFriend(notificationId, button.parentElement);
            });
        });

        document.querySelectorAll('.reject-btn').forEach(button => {
            button.addEventListener('click', () => {
                const notificationId = button.getAttribute('data-id');
                rejectFriend(notificationId, button.parentElement);
            });
        });
    }

    async function acceptFriend(notificationId, notificationElement) {
        try {
            const response = await fetch(`/api/friends/requests/${notificationId}/accept`, {
                method: "PUT",
                credentials: 'include' // 인증 정보를 포함하여 요청
            });
            if (response.ok) {
                alert("친구 요청이 수락되었습니다.");
                notificationElement.remove(); // 알림 항목 제거

                // 읽지 않은 친구 알림 수 감소
                unreadFriendCount = unreadFriendCount > 0 ? unreadFriendCount - 1 : 0;
                updateNotificationCounts();

                loadFriendList();
            } else {
                alert('친구 요청 수락에 실패했습니다.');
            }
        } catch (error) {
            console.error("친구 요청 수락 실패:", error);
        }
    }

    async function rejectFriend(notificationId, notificationElement) {
        try {
            const response = await fetch(`/api/friends/requests/${notificationId}/reject`, {
                method: "PUT",
                credentials: 'include' // 인증 정보를 포함하여 요청
            });
            if (response.ok) {
                alert("친구 요청이 거절되었습니다.");
                notificationElement.remove(); // 알림 항목 제거

                // 읽지 않은 친구 알림 수 감소
                unreadFriendCount = unreadFriendCount > 0 ? unreadFriendCount - 1 : 0;
                updateNotificationCounts();
            } else {
                alert('친구 요청 거절에 실패했습니다.');
            }
        } catch (error) {
            console.error("친구 요청 거절 실패:", error);
        }
    }

    async function sendFriendRequest() {
        const email = searchInput.value.trim();
        if (!email) {
            alert('이메일을 입력하세요.');
            return;
        }

        try {
            const response = await fetch(`/api/friends/request?friendEmail=${encodeURIComponent(email)}`, {
                method: "POST",
                credentials: 'include' // 인증 정보를 포함하여 요청
            });

            if (response.ok) {
                alert("친구 신청이 전송되었습니다.");
                // 추가적인 로직이 필요하다면 여기에 작성
            } else if (response.status === 401) {
                const errorText = await response.text();
                alert(`친구 신청 실패: ${errorText || '로그인이 필요합니다.'}`);
            } else {
                const errorText = await response.text();
                alert(`친구 신청 실패: ${errorText || '오류가 발생했습니다.'}`);
            }
        } catch (error) {
            console.error("친구 신청 실패:", error);
            alert("친구 신청 중 오류가 발생했습니다.");
        }
    }

    sendRequestBtn.addEventListener('click', sendFriendRequest);

    // 페이지 로드 시 친구 목록 및 알림 로드
    loadFriendList();
    loadFriendNotifications();
});
