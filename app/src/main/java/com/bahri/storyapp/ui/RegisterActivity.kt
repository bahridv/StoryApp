package com.bahri.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.bahri.storyapp.R
import com.bahri.storyapp.data.DataRepository
import com.bahri.storyapp.data.remote.network.ApiClient
import com.bahri.storyapp.databinding.ActivityRegisterBinding
import com.bahri.storyapp.utils.Message
import com.bahri.storyapp.utils.NetworkResult
import com.bahri.storyapp.utils.ViewModelFactory
import com.bahri.storyapp.viewmodel.LoginAndRegisterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: LoginAndRegisterViewModel
    private var registerJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(this, ViewModelFactory(dataRepository))[LoginAndRegisterViewModel::class.java]
        setData()

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        playPropertyAnimation()
    }

    private fun playPropertyAnimation() {
        ObjectAnimator.ofFloat(binding.imgRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.textView2, View.ALPHA, 1f).setDuration(500)
        val edtName = ObjectAnimator.ofFloat(binding.edtUsername, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtPass = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, edtName, edtEmail, edtPass, btnRegister)
            start()
        }
    }

    private fun setData() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = binding.edtUsername.text.toString().trim()
                val email = binding.edtEmail.text.toString().trim()
                val password = binding.edtPassword.text.toString().trim()
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
                    Message.setMessage(this@RegisterActivity, getString(R.string.warning_input))
                } else {
                    setLoadingState(true)
                    lifecycle.coroutineScope.launchWhenResumed {
                        if(registerJob.isActive) registerJob.cancel()
                        registerJob = launch {
                            viewModel.register(name, email, password).collect { result ->
                                when(result) {
                                    is NetworkResult.Success -> {
                                        setLoadingState(false)
                                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                        Message.setMessage(this@RegisterActivity, getString(R.string.success_register))
                                        finish()
                                    }
                                    is NetworkResult.Loading -> {
                                        setLoadingState(true)
                                    }
                                    is NetworkResult.Error -> {
                                        Message.setMessage(this@RegisterActivity, resources.getString(R.string.error_register))
                                        setLoadingState(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(loading: Boolean) {
        when(loading) {
            true -> {
                binding.btnRegister.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
            }
            false -> {
                binding.btnRegister.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}