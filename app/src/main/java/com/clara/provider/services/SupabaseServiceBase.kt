package com.clara.provider.services

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * Base Supabase service with HTTP client setup
 * Handles authentication and common API operations
 */
class SupabaseServiceBase(context: Context) {

    // Supabase configuration
    private val supabaseUrl = "https://dmfsaoawhomuxabhdubw.supabase.co"
    private val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRtZnNhb2F3aG9tdXhhYmhkdWJ3Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc2MDM1MjcyOSwiZXhwIjoyMDc1OTI4NzI5fQ.rOuK231ZrvXZBKrwD248Y5I-P3UzyjKXexJ7on_M6UY"
    private val supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRtZnNhb2F3aG9tdXhhYmhkdWJ3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjAzNTI3MjksImV4cCI6MjA3NTkyODcyOX0.X8zyqgFWNQ8Rk_UB096gaVTv709SAKI7iJc61UJn-L8"

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Authorization", "Bearer $supabaseAnonKey")
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")

            chain.proceed(requestBuilder.build())
        }
        .build()

    protected fun makeGetRequest(endpoint: String): Result<String> {
        return try {
            val request = Request.Builder()
                .url("$supabaseUrl/rest/v1/$endpoint")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(response.body?.string() ?: "")
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    protected fun makePostRequest(endpoint: String, body: JSONObject): Result<String> {
        return try {
            val request = Request.Builder()
                .url("$supabaseUrl/rest/v1/$endpoint")
                .post(body.toString().toRequestBody())
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(response.body?.string() ?: "")
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    protected fun makePatchRequest(endpoint: String, body: JSONObject): Result<String> {
        return try {
            val request = Request.Builder()
                .url("$supabaseUrl/rest/v1/$endpoint")
                .patch(body.toString().toRequestBody())
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(response.body?.string() ?: "")
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    protected fun makeDeleteRequest(endpoint: String): Result<String> {
        return try {
            val request = Request.Builder()
                .url("$supabaseUrl/rest/v1/$endpoint")
                .delete()
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(response.body?.string() ?: "")
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
