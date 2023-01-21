package fr.steph.showmemories.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.steph.showmemories.*
import fr.steph.showmemories.databinding.ItemSeasonBinding
import fr.steph.showmemories.models.SeasonModel

class SeasonAdapter(private val context: Context) : ListAdapter<SeasonModel, SeasonAdapter.ViewHolder>(SeasonDiffUtil()) {
    var itemEditedListener: ((SeasonModel) -> Unit)? = null
    var itemDeletedListener: ((SeasonModel) -> Unit)? = null

    class ViewHolder (val binding : ItemSeasonBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(season: SeasonModel) {
            binding.season = season
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSeason = getItem(position)
        holder.bind(currentSeason)

        holder.binding.seasonLayout.setOnHeadingImageClickListener { menuIcon ->
            val popupMenu = PopupMenu(context, menuIcon)
            popupMenu.menuInflater.inflate(R.menu.show_management_menu, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.edit -> {
                        itemEditedListener?.invoke(currentSeason)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete -> {
                        val builder = AlertDialog.Builder(context)
                        val builderTitle = when(currentSeason.movie){
                            true -> context.getString(R.string.movie_suppression_confirmation)
                            false -> context.getString(R.string.season_suppression_confirmation)
                        }
                        builder.setTitle(builderTitle)
                        builder.setCancelable(true)
                        builder.setPositiveButton(context.getString(R.string.confirm_button)) { _, _ -> itemDeletedListener?.invoke(currentSeason)}
                        builder.setNegativeButton(context.getString(R.string.cancel_button)) { _, _ -> }
                        builder.show()
                        return@setOnMenuItemClickListener true
                    }
                    else -> false
                }
            }
        }
    }

    class SeasonDiffUtil: DiffUtil.ItemCallback<SeasonModel>(){
        override fun areItemsTheSame(oldItem: SeasonModel, newItem: SeasonModel): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: SeasonModel, newItem: SeasonModel): Boolean {
            return newItem == oldItem
        }
    }
}