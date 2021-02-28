package app.tek4tv.tek4tvpublishing.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.network.UserBody
import app.tek4tv.tek4tvpublishing.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn_login
import kotlinx.android.synthetic.main.activity_main.et_password
import kotlinx.android.synthetic.main.activity_main.et_username
import kotlinx.android.synthetic.main.login_layout.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        btn_login.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
            setLoadingState(true)
            login()
        }

        et_password.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
                btn_login.performClick()
                true
            }
            else false
        }

        registerObservers()
    }

    private fun registerObservers() {
        viewModel.user.observe(this)
        {
            //txt_result.text = it.toString()

            val startVideoActivity = Intent(applicationContext, VideoListActivity::class.java)
            startActivity(startVideoActivity)
            setLoadingState(false)
            finish()
        }

        viewModel.errorText.observe(this)
        {
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            setLoadingState(false)
        }
    }

    private fun login() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Kiểm tra lại kết nối internet.", Toast.LENGTH_SHORT).show()
            setLoadingState(false)
            return
        }

        val username = et_username.text.toString()
        val pass = et_password.text.toString()

        if(username.isEmpty()) {
            Toast.makeText(this, "Nhập tên đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        if(pass.isEmpty())
        {
            Toast.makeText(this, "Nhập mật khẩu", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(UserBody(username, pass))
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork ?: return false

        val nc = cm.getNetworkCapabilities(activeNetwork) ?: return false

        return (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun setLoadingState(isLoading : Boolean)
    {
        if(isLoading)
        {
            loading_overlay.visibility = View.VISIBLE
            loading_bar.visibility = View.VISIBLE
            btn_login.isClickable = false
            btn_login.text = getString(R.string.processing)
        }
        else
        {
            loading_overlay.visibility = View.GONE
            loading_bar.visibility = View.GONE
            btn_login.isClickable = true
            btn_login.text = getString(R.string.login)
        }
    }
}