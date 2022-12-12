package sgolbert.example.finalproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.w3c.dom.Text

class RecyclerAdapter(private val dataset: ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    lateinit var context: Context
    lateinit var view1: View
    lateinit var viewHolder1: ViewHolder
    lateinit var textView: TextView

    //ViewHolder class
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        lateinit var addressTextView: TextView

        init {
            addressTextView = view.findViewById<TextView>(R.id.addressTextView)
        }
    }

    //Create new views
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        //Create a new view, defining the UI of the list item

        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recyclerview_item_layout,viewGroup, false)

        //Show 5 recycler items in the activity at a time
        val lp = view.getLayoutParams()
        view.setLayoutParams(lp)
        lp.height = 256
        return ViewHolder(view)
    }

    //Replace the contents of a view
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.addressTextView.setText(dataset.get(position))
    }

    //Return size of dataset
    override fun getItemCount(): Int {
        return dataset.size
    }
}