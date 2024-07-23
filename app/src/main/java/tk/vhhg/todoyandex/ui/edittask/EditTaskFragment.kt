package tk.vhhg.todoyandex.ui.edittask

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tk.vhhg.todoyandex.App
import tk.vhhg.todoyandex.model.TodoItem
import tk.vhhg.todoyandex.ui.edittask.composables.EditTaskScreen
import tk.vhhg.todoyandex.ui.theme.AppTheme
import java.util.Date
import javax.inject.Inject

/**
 * UI controller for the task editing screen
 */
class EditTaskFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: EditTaskViewModel.Factory.AFactory

    private val args by navArgs<EditTaskFragmentArgs>()

    private val viewModel: EditTaskViewModel by viewModels {
        viewModelFactory.create(args.todoItem, this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).appComponent.getEditTaskFragmentComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set<TodoItem?>("wasDeleted", null)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    val uiState by viewModel.uiState.collectAsState()
                    EditTaskScreen(
                        body = uiState.body,
                        priority = uiState.priority,
                        deadline = uiState.deadline?.let { Date(it) },
                        deleteButtonEnabled = viewModel.item != null,
                        onBodyChange = { viewModel.changeBody(it) },
                        onPriorityChange = { viewModel.changePriority(it) },
                        onDeadlineSwitchToggle = { viewModel.toggleDeadline(it) },
                        onDeadlineDatePick = { viewModel.changeDeadline(it) },
                        onTopBarCloseClick = { navController.popBackStack() },
                        onTopBarSaveClick = {
                            viewModel.save()
                            navController.popBackStack()
                        },
                        onDeleteButtonClick = {
                            viewModel.delete()
                            navController.previousBackStackEntry?.savedStateHandle?.set("wasDeleted", viewModel.item)
                            navController.popBackStack()
                        })
                }
            }
        }
    }
}
