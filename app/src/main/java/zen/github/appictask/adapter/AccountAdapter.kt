package zen.github.appictask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import zen.github.appictask.R
import zen.github.appictask.databinding.FilterItemBinding
import zen.github.appictask.models.Hierarchy

class AccountAdapter(private val checkedList: MutableList<Hierarchy>) :
    RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    private val diffCallback by lazy {
        object : DiffUtil.ItemCallback<Hierarchy>() {
            override fun areItemsTheSame(oldItem: Hierarchy, newItem: Hierarchy): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Hierarchy, newItem: Hierarchy): Boolean {
                return oldItem.accountNumber == newItem.accountNumber
            }
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = FilterItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            checkBox.text = item.accountNumber

            checkBox.isChecked = (checkedList.contains(item))

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!checkedList.contains(item)) {
                        checkedList.add(item)
                    }
                } else {
                    checkedList.remove(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setSelectedItems(selectedItems: List<Hierarchy>) {
        checkedList.addAll(selectedItems)
        notifyItemRangeChanged(0, itemCount)
    }
}