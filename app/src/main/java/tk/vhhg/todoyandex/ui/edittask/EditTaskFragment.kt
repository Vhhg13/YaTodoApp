package tk.vhhg.todoyandex.ui.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.PopupMenu
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.FragmentEditTaskBinding
import tk.vhhg.todoyandex.model.TodoItemPriority
import tk.vhhg.todoyandex.ui.todolist.DateFormatter
import java.util.Date
import java.util.GregorianCalendar

class EditTaskFragment : Fragment() {
    private var _binding: FragmentEditTaskBinding? = null
    private val binding: FragmentEditTaskBinding get() = _binding!!

    private val viewModel: EditTaskViewModel by viewModels { EditTaskViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        binding.bodyEditText.setText(viewModel.initialBodyContents)
        observeWithLifecycle(viewModel.uiState) { uiState ->
            binding.priorityTextView.text = getString(
                when (uiState.priority) {
                    TodoItemPriority.HIGH -> R.string.high_priority_menu_item
                    TodoItemPriority.MEDIUM -> R.string.medium_priority_menu_item
                    TodoItemPriority.LOW -> R.string.low_priority_menu_item
                }
            )
            binding.deadlineDateTextView.isGone = uiState.deadline == null
            binding.deadlineDateTextView.text =
                uiState.deadline?.let { DateFormatter.sdf.format(Date(it)) }
            binding.hasDeadlineSwitch.isChecked = uiState.deadline != null
            binding.deadlineLinearLayout.isClickable = uiState.deadline != null
            binding.deleteButton.isEnabled = viewModel.taskId != null
            //binding.deleteButtonDivider.isGone = viewModel.taskId == ""
        }
        binding.priorityLinearLayout.setOnClickListener { priorityView ->
            val popup = PopupMenu(context, priorityView)
            popup.menuInflater.inflate(R.menu.priority_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                viewModel.changePriority(
                    when (menuItem.itemId) {
                        R.id.medium_priority -> TodoItemPriority.MEDIUM
                        R.id.low_priority -> TodoItemPriority.LOW
                        else -> TodoItemPriority.HIGH
                    }
                )
                true
            }
            popup.show()
        }
        binding.deadlineLinearLayout.setOnClickListener {
            DatePickerFragment { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                viewModel.changeDeadline(GregorianCalendar().apply {
                    set(year, month, dayOfMonth)
                }.timeInMillis)
            }.show(requireActivity().supportFragmentManager, "datePicker")
        }

        binding.cancelButton.setOnClickListener {
            navController.popBackStack()
        }
        binding.saveButton.setOnClickListener {
            viewModel.save(binding.bodyEditText.text.toString())
            navController.popBackStack()
        }
        binding.hasDeadlineSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDeadline(isChecked)
        }
        binding.deleteButton.setOnClickListener {
            viewModel.delete()
            navController.popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun <T> observeWithLifecycle(flow: StateFlow<T>, block: (T) -> Unit) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(block)
            }
        }
    }
}