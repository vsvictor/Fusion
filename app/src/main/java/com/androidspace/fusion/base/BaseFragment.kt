package com.androidspace.fusion.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.base.errors.ErrorData
import com.androidspace.fusion.base.interfaces.*
import org.koin.androidx.viewmodel.compat.SharedViewModelCompat.sharedViewModel
import java.lang.reflect.ParameterizedType


open class BaseFragment<DB : ViewDataBinding, VM : BaseViewModel<*>> : Fragment(), DefaultLifecycleObserver, OnLoader, OnInitViewModel, OnKeyboardStateChanged, OnMessage {
    private val TAG = BaseFragment::class.java.simpleName
    val vmClass = (javaClass.genericSuperclass as ParameterizedType?)!!.actualTypeArguments[1] as Class<VM>
    protected lateinit var binding: DB
    protected val viewModel by sharedViewModel(this, vmClass)
    protected var navController: NavController? = null
    protected var factory: ViewModelProvider.Factory? = null
    private var lastContentHeight = 0

    protected var onActionBarState: OnActionBarState? = null
    protected var onKeyboadVisibleChanged: OnKeyboadVisibleChanged? = null
    protected var onLoader: OnLoader? = null
    private var toast:Toast? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = javaClass.getAnnotation(Layout::class.java)
        binding = DataBindingUtil.inflate(inflater, layout!!.id, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInit()
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            onLoader = context as OnLoader
        }catch (ex: Exception){}
    }

    open fun onInit() {
        viewModel.onInitViewModel = this
        viewModel.onMessage = this
        viewModel.setOnLoaderListener(this)
        navController = findNavController()// Navigation.findNavController(binding.root)
        lifecycle.addObserver(viewModel)
        viewModel.error.observe(viewLifecycleOwner, Observer {
            it?.let {
                onError(it)
                viewModel.clearError()
            }
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
        onKeyboadVisibleChanged = context as OnKeyboadVisibleChanged
        onActionBarState = context as OnActionBarState
    }
    override fun onDetach() {
        onLoader = null
        onKeyboadVisibleChanged = null
        onActionBarState = null
        show(false)
        activity?.lifecycle?.removeObserver(this)
        super.onDetach()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clear()
        onLoader = null
    }


    open fun onBackPressed() {
        viewModel.onBackPressed()
    }

    open fun onNavigationOnClickListener(){
        onBackPressed()
    }
    open fun onError(data: ErrorData){
        toast?.cancel()
        toast = null
        val builder = StringBuilder()
        builder.append(data.body.message)
        builder.append("\n")
        for(err in data.body.errs){
            builder.append(err.first)
            builder.append(" : ")
            builder.append(err.second)
            builder.append("\n")
        }
        Log.d(TAG, "Error: "+builder.toString())
        toast = Toast.makeText(requireContext(), builder.toString(), Toast.LENGTH_SHORT)
        toast?.setGravity(Gravity.CENTER, 0, 0)
        toast?.show()
    }

    override fun inShowed(): Boolean {
        return onLoader?.inShowed()?:false
    }

    override fun show(isShow: Boolean) {
        onLoader?.show(isShow)
    }

    override fun onInitViewModel() {
        viewModel.initNavController(navController)
    }

    override fun onKeyboardStateChanged(isVisible: Boolean) {}

    override fun onMessage(textID: Int) {
        Toast.makeText(requireContext(), textID, Toast.LENGTH_LONG).show()
    }

    override fun onMessage(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }
}
