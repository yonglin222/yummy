$(document).ready(function() {
    // 전역 상태 변수 (중복 확인 통과 여부)
    let isNameChecked = false;
    let isEmailChecked = false;

    // ========== 1. 닉네임 중복 확인 버튼 클릭 이벤트 ==========
    $('.nickname-check').on('click', function() {
        const name = $('#nickname').val().trim();
        const msg = $('#nicknameMsg');

        if (!name) {
            msg.text("닉네임을 입력해주세요.").css("color", "#d42e2e");
            return;
        }
        // 닉네임 유효성 검사 (한글, 영문만 허용)
        const nicknameRegex = /^[A-Za-z가-힣]+$/;
        if (!nicknameRegex.test(name)) {
            msg.text("닉네임은 한글 또는 영문만 사용할 수 있습니다.").css("color", "#d42e2e");
            return;
        }
        if (name.length > 10) {
            msg.text("닉네임은 10자 이하로 작성해주세요.").css("color", "#d42e2e");
            return;
        }

        // 실제 서버 통신
        $.ajax({
            url: '/user/nameCheck',
            type: 'GET',
            data: { name: name },
            success: function(res) {
                if(res === 'OK') {
                    msg.text("사용 가능한 닉네임입니다.").css("color", "#3ca63c");
                    isNameChecked = true;
                } else {
                    msg.text("이미 사용 중인 닉네임입니다.").css("color", "#d42e2e");
                    isNameChecked = false;
                }
            },
            error: function() {
                alert("서버 통신 오류가 발생했습니다.");
            }
        });
    });

    // ========== 2. 이메일 중복 확인 버튼 클릭 이벤트 ==========
    $('.email-check').on('click', function() {
        const email = $('#email').val().trim();
        const msg = $('#emailMsg');

        if (!email) {
            msg.text("이메일을 입력해주세요.").css("color", "#d42e2e");
            return;
        }
        // 이메일 형식 검사
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            msg.text("올바른 이메일 형식이 아닙니다.").css("color", "#d42e2e");
            return;
        }

        // 실제 서버 통신
        $.ajax({
            url: '/user/emailCheck',
            type: 'GET',
            data: { email: email },
            success: function(res) {
                if(res === 'OK') {
                    msg.text("사용 가능한 이메일입니다.").css("color", "#3ca63c");
                    isEmailChecked = true;
                } else {
                    msg.text("이미 사용 중인 이메일입니다.").css("color", "#d42e2e");
                    isEmailChecked = false;
                }
            },
            error: function() {
                alert("서버 통신 오류가 발생했습니다.");
            }
        });
    });

    // ========== 3. 비밀번호 실시간 검사 ==========
    $('#password').on('input', function() {
        const pw = $(this).val();
        const msg = $('#checkMsg');
        
        // 비밀번호 정규식: 8~16자, 영문 소문자/숫자/특수문자 포함
        const pwRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,16}$/;
        
        if (!pwRegex.test(pw)) {
            msg.text("비밀번호는 8자~16자(영어 소문자,특수문자,숫자 각1개 이상)로 작성해주세요.").css("color", "#d42e2e");
        } else {
            msg.text("사용 가능한 비밀번호 형식입니다.").css("color", "#3ca63c");
        }
        
        // 비밀번호가 바뀌면 확인란도 다시 체크
        $('#passwordCheck').trigger('input');
    });

    // ========== 4. 비밀번호 일치 확인 ==========
    $('#passwordCheck').on('input', function() {
        const pw = $('#password').val();
        const pwc = $(this).val();
        const msg = $('#pwMsg');

        if (!pwc) {
            msg.text("");
            return;
        }

        if (pw !== pwc) {
            msg.text("비밀번호가 일치하지 않습니다.").css("color", "#d42e2e");
        } else {
            msg.text("비밀번호가 일치합니다.").css("color", "#3ca63c");
        }
    });

    // ========== 5. 입력값 변경 시 중복확인 초기화 ==========
    $('#nickname').on('input', function() {
        isNameChecked = false;
        $('#nicknameMsg').text("");
    });
    $('#email').on('input', function() {
        isEmailChecked = false;
        $('#emailMsg').text("");
    });

    // ========== 6. 가입하기 버튼 클릭 (폼 제출 전 검증) ==========
    $('#registForm').on('submit', function(e) {
        // 닉네임 중복 확인 여부
        if (!isNameChecked) {
            alert("닉네임 중복 확인을 해주세요.");
            e.preventDefault();
            return false;
        }
        // 이메일 중복 확인 여부
        if (!isEmailChecked) {
            alert("이메일 중복 확인을 해주세요.");
            e.preventDefault();
            return false;
        }
        
        // 비밀번호 일치 여부
        const pw = $('#password').val();
        const pwc = $('#passwordCheck').val();
        if (pw !== pwc) {
            alert("비밀번호가 일치하지 않습니다.");
            e.preventDefault();
            return false;
        }
        
        // 정규식 최종 확인
        const pwRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,16}$/;
        if (!pwRegex.test(pw)) {
            alert("비밀번호 형식이 올바르지 않습니다.");
            e.preventDefault();
            return false;
        }
        
        // 모든 검사 통과 시 폼 제출 진행 (서버로 전송)
        return true;
    });
});