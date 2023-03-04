package com.androidspace.fusion.base

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.base.interfaces.*
import com.androidspace.fusion.data.local.SettingsSharedPreferences
import com.androidspace.fusion.utils.ContextUtils
import org.koin.androidx.viewmodel.compat.ViewModelCompat.viewModel
import org.koin.java.KoinJavaComponent
import java.lang.reflect.ParameterizedType
import java.util.*

abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel<*>> : AppCompatActivity(),
    OnInitViewModel, OnKeyboadVisibleChanged, OnActionBarState, OnLanguageChanged {
    private val TAG = BaseActivity::class.java.simpleName
    val vmClass = (javaClass.genericSuperclass as ParameterizedType?)!!.actualTypeArguments[1] as Class<VM>
    protected lateinit var binding: DB
    protected val viewModel by viewModel<VM>(this, vmClass)
    protected lateinit var navController: NavController

    private var delta:Float = 0f

    private val defaultKeyboardHeightDP = 100
    private val estimatedKeyboardDP = defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
    private var keyboardVisible = false
    private val rect = Rect()

    private val keyboardStateListeners = ArrayList<OnKeyboardStateChanged>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sh by KoinJavaComponent.inject(SettingsSharedPreferences::class.java)
        val config = resources.configuration
        val currLoc = Locale.forLanguageTag("uk")
        val lang = sh.prefs.getString("language", currLoc.language)
        lang?.let {
            val locale = Locale(it)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                config.setLocale(locale)
            else
                config.locale = locale
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        var text = "N/A"
        when (metrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> {
                text = "Low"
            }
            DisplayMetrics.DENSITY_MEDIUM -> {
                text = "Medium"
            }
            DisplayMetrics.DENSITY_HIGH -> {
                text = "Hight"
            }
            DisplayMetrics.DENSITY_XHIGH ->{
                text = "XHight"
            }
            DisplayMetrics.DENSITY_XXHIGH ->{
                text = "XXHight"
            }
            DisplayMetrics.DENSITY_XXXHIGH ->{
                text = "XXXHight"
            }
            else -> {
                text = metrics.densityDpi.toString()
            }
        }
        Log.d("DENSITY", text)
        viewModel.onInitViewModel = this
        val layout = javaClass.getAnnotation(Layout::class.java)
        binding = DataBindingUtil.setContentView(this, layout!!.id)
        val parentView = binding.root
        parentView.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val estimatedKeyboardHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                estimatedKeyboardDP.toFloat(),
                parentView.resources.displayMetrics
            ).toInt()
            parentView.getWindowVisibleDisplayFrame(rect)
            val heightDiff = parentView.rootView.height - (rect.bottom - rect.top)
            val isShown = heightDiff >= estimatedKeyboardHeight
            if (isShown == keyboardVisible) {
                return@OnGlobalLayoutListener
            }
            keyboardVisible = isShown
            onKeyboardVisibleChanged(isShown)
        })
        lifecycle.addObserver(viewModel)
        binding.lifecycleOwner = this
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window,
            window.decorView.findViewById(android.R.id.content)).let { controller ->
/*            controller.hide(WindowInsetsCompat.Type.systemBars())*/
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.hide(WindowInsetsCompat.Type.captionBar())
            controller.hide(WindowInsetsCompat.Type.displayCutout())
            controller.hide(WindowInsetsCompat.Type.ime())
            controller.hide(WindowInsetsCompat.Type.mandatorySystemGestures())
            //controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.hide(WindowInsetsCompat.Type.systemGestures())
            controller.hide(WindowInsetsCompat.Type.tappableElement())

            // When the screen is swiped up at the bottom
            // of the application, the navigationBar shall
            // appear for some time
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val sh by KoinJavaComponent.inject(SettingsSharedPreferences::class.java)
        val currLoc = Locale.forLanguageTag("uk")
        val lang = sh.prefs.getString("language", currLoc.language)
        Log.d(TAG, "Language:"+ lang)
        val locale = when(lang){
            "en" -> {
                Locale.forLanguageTag("en")
                //Locale("en","GB")
            }
            "uk" -> {
                Locale.forLanguageTag("uk")
                //Locale("uk","UA")
            }
            "ru" -> {
                Locale.forLanguageTag("ru")
                //Locale("ru","RU")
            }
            else -> {
                Locale.forLanguageTag("en")
                //Locale("en","GB")
            }
        }
        Log.i(TAG, locale.toString())
        sh.prefs.edit().putString("language", locale.language).commit()
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, locale)
        super.attachBaseContext(localeUpdatedContext)
    }



    override fun onInitViewModel() {
        viewModel.initNavController(navController)
    }

    fun addKeyboardListener(listener: OnKeyboardStateChanged){
        keyboardStateListeners.add(listener)
    }

    fun removeKeyboardListener(listener: OnKeyboardStateChanged){
        keyboardStateListeners.remove(listener)
    }

    abstract fun getNavHostFragment(): Fragment?

    fun getCurrentFragment(): Fragment? {
        //supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val currentNavHost = getNavHostFragment()
        val currentFragmentClassName =
            (navController.currentDestination as FragmentNavigator.Destination).className
        return currentNavHost?.childFragmentManager?.fragments?.filterNotNull()?.find {
            it.javaClass.name == currentFragmentClassName
        }
    }

    override fun onBackPressed() {
        val fr = getCurrentFragment()
        fr?.let {
            if (it is BaseFragment<*, *>) {
                it.onBackPressed()
            } else {
                super.onBackPressed()
            }
        } ?: kotlin.run {
            super.onBackPressed()
        }
    }

    override fun setKeyboarVisible(isVisible: Boolean) {
        if(isVisible) showKeyboard(binding.root) else hideKeyboard(binding.root)
    }
     override fun setKeyboarVisible(view: View, isVisible: Boolean) {
         if(isVisible) showKeyboard(view) else hideKeyboard(view)
     }

    override fun onKeyboardVisibleChanged(isShow: Boolean) {
        for(item in keyboardStateListeners){
            item.onKeyboardStateChanged(isShow)
        }
        val fr = getCurrentFragment()
        fr?.let {
            //if(it is BaseFragment<*,*>){
                //it.OnKeyboardVisibleChanged(isShow)
                (it as OnKeyboardStateChanged).onKeyboardStateChanged(isShow)
            //}
        }
    }

    override fun isKeyboardVisible(): Boolean {
        return keyboardVisible
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        onKeyboardVisibleChanged(true)

    }
    private fun showKeyboard(view: View) {
        val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        onKeyboardVisibleChanged(false)
    }

    override fun onActionBarView(isView: Boolean) {
        if(isView) supportActionBar?.show() else supportActionBar?.hide()
    }
    override fun onLanguageChanged(str: String, back: Int) {
        val sh by KoinJavaComponent.inject(SettingsSharedPreferences::class.java)
        sh.prefs.edit().putString("language", str).commit()
        if(back > 0) {
            this.intent?.putExtra("start", back)
        }
        startActivity(Intent.makeRestartActivityTask(this.intent?.component))
    }
}

