package com.team7.meetmind.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.team7.meetmind.core.suspendRunCatching
import com.team7.meetmind.data.mapper.mapAuthFailure
import com.team7.meetmind.domain.model.SessionState
import com.team7.meetmind.domain.model.User
import com.team7.meetmind.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    init {
        // Email đặt lại mật khẩu của Firebase sẽ gửi bằng tiếng Việt.
        auth.setLanguageCode("vi")
    }

    /**
     * Khi đăng ký, Firebase đăng nhập ngay sau createUser, TRƯỚC khi tên hiển thị được lưu.
     * Cờ này giữ yên trạng thái phiên gửi lên giao diện cho tới khi cả hai bước xong,
     * để Trang chủ không chớp tên cũ (email) rồi mới đổi sang tên thật.
     */
    private val sessionUpdatesPaused = MutableStateFlow(value = false)

    private fun authState(): Flow<FirebaseUser?> = callbackFlow {
        // Listener được gọi ngay một lần khi đăng ký, rồi mỗi khi đăng nhập/đăng xuất.
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun observeSession(): Flow<SessionState> =
        combine(authState(), sessionUpdatesPaused) { user, paused -> user to paused }
            .filter { (_, paused) -> !paused }
            .map { (user, _) -> user?.toDomainUser()?.let { SessionState.Authenticated(it) } ?: SessionState.Unauthenticated }
            .distinctUntilChanged()

    override suspend fun login(email: String, password: String): Result<Unit> =
        suspendRunCatching {
            auth.signInWithEmailAndPassword(email, password).await()
            Unit
        }.mapAuthFailure()

    override suspend fun register(name: String, email: String, password: String): Result<Unit> =
        suspendRunCatching {
            sessionUpdatesPaused.value = true
            try {
                val user = auth.createUserWithEmailAndPassword(email, password).await().user
                    ?: error("Firebase không trả về người dùng sau khi đăng ký")

                // Lưu tên vào hồ sơ Auth. Nếu bước này lỗi thì tài khoản vẫn hợp lệ (hiển thị email thay tên).
                suspendRunCatching {
                    user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build()).await()
                }

                // Hồ sơ trong Firestore. Không await: Firestore tự xếp hàng và đồng bộ khi có mạng.
                firestore.collection("users").document(user.uid).set(
                    mapOf(
                        "fullName" to name,
                        "email" to email,
                        "createdAt" to FieldValue.serverTimestamp()
                    ),
                    SetOptions.merge()
                )
                Unit
            } finally {
                sessionUpdatesPaused.value = false
            }
        }.mapAuthFailure()

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        suspendRunCatching {
            auth.sendPasswordResetEmail(email).await()
            Unit
        }.mapAuthFailure()

    override suspend fun logout(): Result<Unit> =
        suspendRunCatching {
            auth.signOut() // xoá phiên ngay trên máy, không cần mạng
        }

    private fun FirebaseUser.toDomainUser(): User = User(
        id = uid,
        email = email.orEmpty(),
        displayName = displayName
    )
}
