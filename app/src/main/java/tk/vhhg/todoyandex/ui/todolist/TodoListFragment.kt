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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.FragmentItemsListBinding
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem

/**
 * UI controller for the [TodoItem]s list
 */
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
            onToggle = { todoItem ->
                viewModel.toggle(todoItem)
            },
            onItemClick = { todoItem: TodoItem? ->
                val directions =
                    TodoListFragmentDirections.actionItemsListFragmentToEditTaskFragment(todoItem)
                navController.navigate(directions)
            }
        )
        binding.recycler.adapter = adapter
        observeWithLifecycle(viewModel.uiState) { uiState ->
            binding.swipeRefreshLayout.isRefreshing = uiState.isLoading
            adapter.submitList(uiState.filteredList)
            binding.tasksDoneToolbarTextView.text =
                getString(R.string.tasks_done_format, uiState.tasksDone)
            val iconResource = if (uiState.areDoneTasksVisible) {
                R.drawable.visibility_off_24px
            } else {
                R.drawable.visibility_24px
            }
            binding.visibilityButton.icon =
                AppCompatResources.getDrawable(requireContext(), iconResource)
        }
        observeWithLifecycle(viewModel.errors) { _: Result<Unit> ->
            Snackbar.make(
                binding.root,
                R.string.error_happened,
                Snackbar.LENGTH_LONG
            ).setAction(R.string.refresh){
                viewModel.refresh()
            }.show()
        }
        binding.visibilityButton.setOnClickListener {
            viewModel.toggleDoneTasksVisibility()
        }
        binding.fab.setOnClickListener {
            val directions =
                TodoListFragmentDirections.actionItemsListFragmentToEditTaskFragment(null)
            navController.navigate(directions)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun <T> observeWithLifecycle(flow: Flow<T>, block: (T) -> Unit) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(block)
            }
        }
    }
}