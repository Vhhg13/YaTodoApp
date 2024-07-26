package tk.vhhg.todoyandex.ui.todolist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.R
import tk.vhhg.todoyandex.databinding.FragmentItemsListBinding
import tk.vhhg.todoyandex.model.Result
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.util.observeWithLifecycle
import javax.inject.Inject

/**
 * UI controller for the [TodoItem]s list
 */
class TodoListFragment : Fragment() {
    companion object {
        const val DELETION_COUNTDOWN_DURATION = 5
        const val WAS_DELETED_SS_HANDLE_KEY = "wasDeleted"
    }

    private var _binding: FragmentItemsListBinding? = null
    private val binding: FragmentItemsListBinding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: TodoListViewModel.Factory.AFactory
    private val viewModel: TodoListViewModel by viewModels { viewModelFactory.create(this) }

    private val navController by lazy { findNavController() }

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
        binding.myTasksToolbarTextView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)

        val adapter = TodoListAdpater()
        binding.recycler.adapter = adapter

        observeDeletedItems()

        observeWithLifecycle(viewModel.uiState) { uiState ->
            adapter.submitList(uiState.filteredList)

            val iconResource: Int
            val contentDescriptionResource: Int
            if (uiState.areDoneTasksVisible) {
                iconResource = R.drawable.visibility_off_24px
                contentDescriptionResource = R.string.hide_done
            } else {
                iconResource = R.drawable.visibility_24px
                contentDescriptionResource = R.string.show_done
            }

            binding.swipeRefreshLayout.isRefreshing = uiState.isLoading
            binding.visibilityButton.apply {
                icon = AppCompatResources.getDrawable(requireContext(), iconResource)
                contentDescription = getString(contentDescriptionResource)
            }
            binding.tasksDoneToolbarTextView.apply {
                contentDescription = resources.getQuantityString(
                    R.plurals.tasks_done_content_description,
                    uiState.tasksDone,
                    uiState.tasksDone
                )
                text = getString(R.string.tasks_done_format, uiState.tasksDone)
            }
        }

        observeWithLifecycle(viewModel.errors) { r: Result<Unit> ->
            if (r is Result.Success) return@observeWithLifecycle
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
            val directions = TodoListFragmentDirections
                .actionItemsListFragmentToEditTaskFragment(null)
            navController.navigate(directions)
        }
        binding.infoButton.setOnClickListener {
            navController.navigate(TodoListFragmentDirections.actionItemsListFragmentToInfoFragment())
        }
        binding.swipeRefreshLayout.setOnRefreshListener(viewModel::refresh)
    }

    private val savedStateHandle: SavedStateHandle?
        get() = navController.currentBackStackEntry?.savedStateHandle

    private fun observeDeletedItems() {
        savedStateHandle?.getStateFlow<TodoItem?>(WAS_DELETED_SS_HANDLE_KEY, null)?.let { deletedItemsFlow ->
            observeWithLifecycle(deletedItemsFlow) { deletedItem ->
                if (deletedItem != null) createCountDownSnackbar(deletedItem) {
                    savedStateHandle?.set<TodoItem?>(WAS_DELETED_SS_HANDLE_KEY, null)
                }
            }
        }
    }

    private fun createCountDownSnackbar(
        deletedItem: TodoItem,
        onDismissSnackbarCallback: () -> Unit
    ) {
        DeletionCountdownSnackbar(
            view = binding.root,
            countdownDuration = DELETION_COUNTDOWN_DURATION,
            deletedItem = deletedItem,
            restore = viewModel::restore,
            onCloseSnackbar = onDismissSnackbarCallback
        ).showOn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun TodoListAdpater() = TodoListAdapter(
        onToggle = { todoItem ->
            viewModel.toggle(todoItem)
        },
        onItemClick = { todoItem: TodoItem? ->
            val directions =
                TodoListFragmentDirections.actionItemsListFragmentToEditTaskFragment(todoItem)
            navController.navigate(directions)
        }
    )
}