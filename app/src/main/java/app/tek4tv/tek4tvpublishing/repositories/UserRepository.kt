package app.tek4tv.tek4tvpublishing.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.tek4tv.tek4tvpublishing.model.PlaylistItem
import app.tek4tv.tek4tvpublishing.model.User
import app.tek4tv.tek4tvpublishing.network.AuthService
import app.tek4tv.tek4tvpublishing.network.UserBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val authService: AuthService,
) {
    var currentToken = ""
    var currentUser: User? = null

    private val _errorText = MutableLiveData<String>()
    val errorText: LiveData<String> = _errorText


    suspend fun login(body: UserBody, token: String): Response<User>? {
        val mbody = mapOf(
            "UserName" to body.username,
            "PassWord" to body.password
        )
        return try {
            val response = authService.login(mbody)

            if (response.isSuccessful) {
                currentUser = response.body()
                Log.e("userid",response.body()!!.userId)
            }
            else
                _errorText.value = "Error: ${response.code()} - Error Message: ${response.message()}"

            response
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("getToken()", e.message!!)
            _errorText.value = e.message ?: ""
            null
        }
    }

    suspend fun getToken(): String {
        val body = mapOf(
            "AppID" to "bc6da08b-3ad4-4452-8f29-d56bc69e31995",
            "ApiKey" to "5G2Zix5YcWLdatLFrr+81d7ldMV7Yt5CGftGF5VTqhM=8",
            "AccountId" to "64857311-d116-4c38-b0ab-1643050c441d"
        )

        return try {
            val response = authService.getToken(body)
            if (response.isSuccessful)
                currentToken = response.body()!!
            else
                _errorText.value = "Error: ${response.code()} - Error Message: ${response.message()}"
            currentToken
        } catch (e: Exception) {
            Log.e("getToken()", e.message!!)
            _errorText.value = e.message ?: ""
            ""
        }
    }

    suspend fun getUserPlaylist(): Response<List<PlaylistItem>>?
    {
        return try
        {
            val response = authService.getPlaylistListUser(currentUser!!.userId)

            if (response.isSuccessful)
            {
                response
            }
            else
                null
        } catch (e: Exception)
        {
            Log.e("VideoRepo.getVideos", e.message ?: "")
            null
        }
    }
}