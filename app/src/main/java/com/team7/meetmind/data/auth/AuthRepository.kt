package com.team7.meetmind.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Bọc toàn bộ thao tác Firebase Authentication (đăng ký, đăng nhập, đăng xuất, quên mật khẩu)
 * sau một lớp duy nhất, để các màn hình/ViewModel không gọi thẳng FirebaseAuth.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /** Phát ra người dùng hiện tại mỗi khi trạng thái đăng nhập thay đổi (đăng nhập/đăng xuất ở bất kỳ đâu). */
    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth -> trySend(firebaseAuth.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Không tạo được tài khoản")
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build()).await()
        user
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        result.user ?: throw IllegalStateException("Đăng nhập thất bại")
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }
}
