package ir.zahrasdg.locationlogger

import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity


abstract class BaseActivity<T : ViewModel> : AppCompatActivity() {

    protected lateinit var viewModel: T

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        viewModel = initViewModel()

        setupObservers()
    }

    abstract fun initViewModel(): T

    protected open fun setupObservers() {}
}
