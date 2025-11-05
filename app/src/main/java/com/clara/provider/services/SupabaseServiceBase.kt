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

    // TODO: Load from environment or config file
    private val supabaseUrl = "" // Configure in secrets/env
    private val supabaseKey = "" // Configure in secrets/env
    private val supabaseAnonKey = "" // Configure in secrets/env

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
