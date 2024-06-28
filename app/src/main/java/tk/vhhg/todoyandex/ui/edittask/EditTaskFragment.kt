package tk.vhhg.todoyandex.ui.edittask

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
import tk.vhhg.todoyandex.ui.edittask.composables.EditTaskScreen
import tk.vhhg.todoyandex.ui.theme.AppTheme
import java.util.Date

class EditTaskFragment : Fragment() {

    private val viewModel: EditTaskViewModel by viewModels { EditTaskViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val navController = findNavController()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                AppTheme {
                    val uiState by viewModel.uiState.collectAsState()
                    EditTaskScreen(
                        body = uiState.body,
                        priority = uiState.priority,
                        deadline = uiState.deadline?.let { Date(it) },
                        deleteButtonEnabled = viewModel.taskId != null,
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
                            navController.popBackStack()
                        })
                }
            }
        }
    }
}
