package ir.zahrasdg.locationlogger.view

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel


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
