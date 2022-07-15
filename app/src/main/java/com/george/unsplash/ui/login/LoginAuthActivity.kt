package com.george.unsplash.ui.login

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.george.unsplash.databinding.ActivityLoginAuthBinding
import com.george.unsplash.utils.Keys
import net.openid.appauth.*

class LoginAuthActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginAuthBinding
    private lateinit var service: AuthorizationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val ex = AuthorizationException.fromIntent(it.data!!)
                val result = AuthorizationResponse.fromIntent(it.data!!)
                if (ex != null) {
                    Log.e("Auth", "launcher: $ex")
                } else {
                    val secret = ClientSecretBasic(Keys.SECRET_KEY)
                    val tokenRequest = result?.createTokenExchangeRequest()

                    service.performTokenRequest(tokenRequest!!, secret) { res, exception ->
                        if(exception == null) {

                        }

                    }
                }
            }
        }

    private fun auth() {
        val redirectUrl = Uri.parse("urn:ietf:wg:oauth:2.0:oob")
        val authUrl = Uri.parse("https://unsplash.com/oauth/authorize")
        val tokenUri = Uri.parse("https://unsplash.com/oauth/token")

        val config = AuthorizationServiceConfiguration(authUrl, tokenUri)
        val request = AuthorizationRequest
            .Builder(config, Keys.USER_ID, ResponseTypeValues.CODE, redirectUrl)
            .build()

        val intent = service.getAuthorizationRequestIntent(request)
    }

}