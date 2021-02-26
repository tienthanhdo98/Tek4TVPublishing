package app.tek4tv.tek4tvpublishing.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tek4tv.tek4tvpublishing.repositories.UserRepository
import app.tek4tv.tek4tvpublishing.model.User
import app.tek4tv.tek4tvpublishing.network.UserBody
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject
constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var token : String = ""
    private var getTokenJob : Job? = null

    private val _user  = MutableLiveData<User>()
    val user : LiveData<User> = _user

    val errorText : LiveData<String> = userRepository.errorText

    fun getToken()
    {
        getTokenJob = viewModelScope.launch {
            token = userRepository.getToken()
            Log.d("Token", token)
        }
    }

    fun login(userBody: UserBody)
    {
        viewModelScope.launch {
            getTokenJob?.join()
            val response = userRepository.login(userBody, token)
            if(response != null)
            {
                if(response.isSuccessful)
                {
                    _user.value = response.body()
                }
            }
        }
    }

}