package com.example.flamingcoding.kotlinTrials

class ProjectFrameworkMVX {


}

// ############################################## MVP ##############################################
// View 接口
//interface LoginView {
//    fun showProgress(show: Boolean)
//    fun showError(message: String)
//    fun navigateToHome()
//}
//
//// Presenter
//class LoginPresenter(private val view: LoginView, private val model: LoginModel) {
//    fun onLoginButtonClicked(username: String, password: String) {
//        view.showProgress(true)
//        model.login(username, password, object : Callback<User> {
//            override fun onSuccess(user: User) {
//                view.showProgress(false)
//                view.navigateToHome()
//            }
//
//            override fun onError(error: String) {
//                view.showProgress(false)
//                view.showError(error)
//            }
//        })
//    }
//}
//
//// Activity 实现 View 接口
//class LoginActivity : AppCompatActivity(), LoginView {
//    private lateinit var presenter: LoginPresenter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//        presenter = LoginPresenter(this, LoginModel())
//
//        btnLogin.setOnClickListener {
//            presenter.onLoginButtonClicked(etUsername.text.toString(), etPassword.text.toString())
//        }
//    }
//
//    override fun showProgress(show: Boolean) { /* 显示/隐藏进度条 */
//    }
//
//    override fun showError(message: String) { /* 显示错误提示 */
//    }
//
//    override fun navigateToHome() { /* 跳转到主页 */
//    }
//}

// ############################################## MVVM ##############################################
//// ViewModel
//class LoginViewModel : ViewModel() {
//    private val _loginState = MutableLiveData<LoginState>()
//    val loginState: LiveData<LoginState> = _loginState
//
//    fun login(username: String, password: String) {
//        _loginState.value = LoginState.Loading
//        viewModelScope.launch {
//            try {
//                val user = repository.login(username, password)
//                _loginState.value = LoginState.Success(user)
//            } catch (e: Exception) {
//                _loginState.value = LoginState.Error(e.message)
//            }
//        }
//    }
//}
//
//// Activity/Fragment
//class LoginActivity : AppCompatActivity() {
//    private val viewModel: LoginViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        btnLogin.setOnClickListener {
//            viewModel.login(etUsername.text.toString(), etPassword.text.toString())
//        }
//
//        viewModel.loginState.observe(this) { state ->
//            when (state) {
//                is LoginState.Loading -> showProgress(true)
//                is LoginState.Success -> navigateToHome(state.user)
//                is LoginState.Error -> showError(state.message)
//            }
//        }
//    }
//}

// ############################################## MVI ##############################################
// 定义状态
//sealed class LoginState {
//    object Idle : LoginState()
//    object Loading : LoginState()
//    data class Success(val user: User) : LoginState()
//    data class Error(val message: String) : LoginState()
//}
//
// 定义意图
//sealed class LoginIntent {
//    data class Login(val username: String, val password: String) : LoginIntent()
//}
//
//// ViewModel 处理意图并暴露状态
//class LoginViewModel : ViewModel() {
//    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
//    val state: StateFlow<LoginState> = _state.asStateFlow()
//
//    fun processIntent(intent: LoginIntent) {
//        when (intent) {
//            is LoginIntent.Login -> login(intent.username, intent.password)
//        }
//    }
//
//    private fun login(username: String, password: String) {
//        _state.value = LoginState.Loading
//        viewModelScope.launch {
//            val result = repository.login(username, password)
//            _state.value = result.fold(
//                onSuccess = { LoginState.Success(it) },
//                onFailure = { LoginState.Error(it.message) }
//            )
//        }
//    }
//}
//
//// Activity
//class LoginActivity : AppCompatActivity() {
//    private val viewModel: LoginViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        btnLogin.setOnClickListener {
//            viewModel.processIntent(LoginIntent.Login(etUsername.text.toString(), etPassword.text.toString()))
//        }
//
//        lifecycleScope.launchWhenStarted {
//            viewModel.state.collect { state ->
//                when (state) {
//                    is LoginState.Loading -> showProgress(true)
//                    is LoginState.Success -> navigateToHome(state.user)
//                    is LoginState.Error -> showError(state.message)
//                    else -> {}
//                }
//            }
//        }
//    }
//}