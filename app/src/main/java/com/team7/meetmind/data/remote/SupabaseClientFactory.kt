package com.team7.meetmind.data.remote

import com.team7.meetmind.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Tạo SupabaseClient duy nhất cho cả app (Koin giữ nó ở dạng singleton).
 * URL và key lấy từ local.properties qua BuildConfig, KHÔNG viết thẳng vào mã nguồn.
 */
object SupabaseClientFactory {
    fun create(): SupabaseClient {
        require(BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_PUBLISHABLE_KEY.isNotBlank()) {
            "Thiếu SUPABASE_URL hoặc SUPABASE_PUBLISHABLE_KEY trong local.properties (xem README.md)."
        }
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}
