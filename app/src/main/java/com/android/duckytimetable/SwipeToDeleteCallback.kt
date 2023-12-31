package com.android.duckytimetable
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.duckytimetable.data.TimetableViewModel
import kotlinx.coroutines.launch

class SwipeToDeleteCallback(private val adapter: CustomAdapter, private val mTimetableViewModel: TimetableViewModel) : ItemTouchHelper.SimpleCallback(
    0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val parent: ViewGroup = viewHolder.itemView.parent as ViewGroup
        val textViews: Array<TextView> = getTextViewsFromAdapter(position, parent)
        val nameTextView: String = textViews[0].text.toString()
        val hoursTextView: String = textViews[1].text.toString()
        val minutesTextView: String = textViews[2].text.toString()
        val weekDaysTextView: String = textViews[3].text.toString()
        val descriptionTextView: String = textViews[4].text.toString()
        if (direction == ItemTouchHelper.LEFT) {
            Log.d("mu", "$nameTextView $minutesTextView $hoursTextView $descriptionTextView $weekDaysTextView")

            mTimetableViewModel.viewModelScope.launch {
                val timetableToDelete = mTimetableViewModel.getTimetableByNameAndTime(nameTextView, minutesTextView, hoursTextView)
                if (timetableToDelete != null) {
                    mTimetableViewModel.deleteTimetable(timetableToDelete)
                    adapter.deleteItem(position) // Обновите адаптер для отображения удаленного элемента
                } else {
                    Log.d("mu", "Расписание не найдено в базе данных") // Обработайте случай, когда расписание не удалось найти в базе данных
                }
            }

//            adapter.deleteItem(position)
        } else {
            adapter.notifyItemChanged(position)
            val intent = Intent(parent.context, ChangeActivity::class.java)
            intent.putExtra("name",nameTextView)
            intent.putExtra("hours",minutesTextView)
            intent.putExtra("minutes",hoursTextView)
            intent.putExtra("weekDay",weekDaysTextView)
            intent.putExtra("details",descriptionTextView)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            parent.context.startActivity(intent)
        }
    }

    fun getTextViewsFromAdapter(position: Int, parent: ViewGroup): Array<TextView> {
        return adapter.getTextViews(position, parent)
    }
}