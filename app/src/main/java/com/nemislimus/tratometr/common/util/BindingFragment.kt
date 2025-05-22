package com.nemislimus.tratometr.common.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/** Этот класс наследуется от Fragment и убирает однотипный код при инициализации
 * ViewBinding класса.
 * По факту нужно только переопределить абстрактный метод [createBinding].
 * Если есть необходимость очищать и освобождать фрагмент от данных или слушателей,
 * то можно переопределить метод [onDestroyFragment].
 */
abstract class BindingFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    open fun onDestroyFragment() {}

    override fun onDestroyView() {
        onDestroyFragment()
        _binding = null
        super.onDestroyView()
    }

    final override fun onDestroy() {
        super.onDestroy()
    }

    final override fun onDetach() {
        super.onDetach()
    }
}