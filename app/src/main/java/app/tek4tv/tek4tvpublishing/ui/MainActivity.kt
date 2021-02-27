package app.tek4tv.tek4tvpublishing.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.network.UserBody
import app.tek4tv.tek4tvpublishing.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        //setSupportActionBar(findViewById(R.id.toolbar))



        btn_login.setOnClickListener {
            btn_login.isClickable = false
            btn_login.text = getString(R.string.processing)
            login()
        }

        registerObservers()
    }

    private fun registerObservers() {
        viewModel.user.observe(this)
        {
            //txt_result.text = it.toString()

            val startVideoActivity = Intent(applicationContext, VideoListActivity::class.java)
            startActivity(startVideoActivity)
            finish()
        }

        viewModel.errorText.observe(this)
        {
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            btn_login.isClickable = true
            btn_login.text = getString(R.string.login)
        }
    }

    private fun login() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show()
            return
        }

       /* if (viewModel.token == "")
            viewModel.getToken()*/

        val username = et_username.text.toString()
        val pass = et_password.text.toString()

        if(username.isEmpty()) {
            Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show()
            return
        }

        if(pass.isEmpty())
        {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
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
}