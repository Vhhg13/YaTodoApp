package tk.vhhg.todoyandex.ui.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.FragmentItemsListBinding
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.repo.TodoItemsFakeRepository

class TodoListFragment : Fragment() {
    private var _binding: FragmentItemsListBinding? = null
    private val binding: FragmentItemsListBinding get() = _binding!!

    private val viewModel: TodoListViewModel by viewModels { TodoListViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val adapter = TodoListAdapter(
            onToggle = { todoItemId ->
                viewModel.toggle(todoItemId)
            },
            onItemClick = { todoItemId ->
                val directions =
                    TodoListFragmentDirections.actionItemsListFragmentToEditTaskFragment(todoItemId)
                navController.navigate(directions)
            }
        )
        binding.recycler.adapter = adapter
        observeWithLifecycle(viewModel.items) { list ->
            adapter.submitList(list)
        }
        observeWithLifecycle(viewModel.tasksDone) { amount ->
            binding.tasksDoneToolbarTextView.text = getString(R.string.tasks_done_format, amount)
        }
        observeWithLifecycle(viewModel.areDoneTasksVisible) { areVisible ->
            val iconResource = if (areVisible) {
                R.drawable.visibility_24px
            } else {
                R.drawable.visibility_off_24px
            }
            binding.visibilityButton.icon =
                AppCompatResources.getDrawable(requireContext(), iconResource)
        }
        binding.visibilityButton.setOnClickListener {
            viewModel.toggleDoneTasksVisibility()
        }
        binding.fab.setOnClickListener {
            val directions =
                TodoListFragmentDirections.actionItemsListFragmentToEditTaskFragment(null)
            navController.navigate(directions)
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