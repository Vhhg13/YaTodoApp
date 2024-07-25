package tk.vhhg.todoyandex.ui.todolist

import android.content.Context
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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.FragmentItemsListBinding
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem
import javax.inject.Inject

/**
 * UI controller for the [TodoItem]s list
 */
class TodoListFragment : Fragment() {
    private var _binding: FragmentItemsListBinding? = null
    private val binding: FragmentItemsListBinding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: TodoListViewModel.Factory.AFactory

    private val viewModel: TodoListViewModel by viewModels { viewModelFactory.create(this) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).appComponent.getTodoListFragmentComponent()
            .inject(this)
    }

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
        savedStateHandle(navController)?.getStateFlow<TodoItem?>("wasDeleted", null)?.let { deletedItemsFlow ->
            observeWithLifecycle(deletedItemsFlow) { deletedItem ->
                if (deletedItem != null) createCountDownSnackbar(deletedItem) {
                    savedStateHandle(navController)?.set<TodoItem?>(
                        "wasDeleted",
                        null
                    )
                }
            }
        }
        binding.recycler.adapter = adapter
        observeWithLifecycle(viewModel.uiState) { uiState ->
            binding.swipeRefreshLayout.isRefreshing = uiState.isLoading
            adapter.submitList(uiState.filteredList)
            binding.tasksDoneToolbarTextView.text =
                getString(R.string.tasks_done_format, uiState.tasksDone)
            binding.tasksDoneToolbarTextView.contentDescription = resources.getQuantityString(R.plurals.tasks_done_content_description, uiState.tasksDone, uiState.tasksDone)
            val iconResource: Int
            val contentDescriptionResource: Int
            if (uiState.areDoneTasksVisible) {
                iconResource = R.drawable.visibility_off_24px
                contentDescriptionResource = R.string.hide_done
            } else {
                iconResource = R.drawable.visibility_24px
                contentDescriptionResource = R.string.show_done
            }
            binding.visibilityButton.apply {
                icon = AppCompatResources.getDrawable(requireContext(), iconResource)
                contentDescription = getString(contentDescriptionResource)
            }
        }
        observeWithLifecycle(viewModel.errors) { r: Result<Unit> ->
            if(r !is Result.Success)
                Snackbar.make(
                    binding.root,
                    R.string.error_happened,
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.refresh) {
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
        binding.infoButton.setOnClickListener {
            navController.navigate(TodoListFragmentDirections.actionItemsListFragmentToInfoFragment())
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun savedStateHandle(navController: NavController) =
        navController.currentBackStackEntry?.savedStateHandle

    private fun createCountDownSnackbar(deletedItem: TodoItem, onDismissSnackbarCallback: () -> Unit) {
        lifecycleScope.launch {
            val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            snackbar.setTextMaxLines(1)
            snackbar.setAction(R.string.restore_deleted){
                viewModel.restore(deletedItem)
                snackbar.dismiss()
                onDismissSnackbarCallback()
                cancel()
            }
            snackbar.show()
            for(countdown in 5 downTo 1){
                snackbar.setText(resources.getString(R.string.deletion_countdown, deletedItem.body, countdown))
                snackbar.setBackgroundTint((0xFFCC0000 - 0x220000*countdown).toInt())
                delay(1000)
            }
            snackbar.setText(resources.getString(R.string.deletion_countdown, deletedItem.body, 0))
            snackbar.dismiss()
            onDismissSnackbarCallback()
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