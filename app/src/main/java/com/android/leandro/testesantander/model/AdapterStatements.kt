package com.android.leandro.testesantander.model


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.leandro.testesantander.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class AdapterStatements(private val listStatement: List<Statement>, private val context: Context) : RecyclerView.Adapter<AdapterStatements.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_statement, parent, false)
        return ViewHolder(v, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val statement = listStatement[position]
        holder.bind(statement, position)

    }

    override fun getItemCount(): Int {
        return listStatement.size
    }

    inner class ViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView
        private val desc: TextView
        private val date: TextView
        private val value: TextView

        init {

            title = itemView.findViewById(R.id.item_statement_tv_tile)
            desc = itemView.findViewById(R.id.item_statement_tv_desc)
            date = itemView.findViewById(R.id.item_statement_tv_date)
            value = itemView.findViewById(R.id.item_statement_value)

        }

        fun bind(statement: Statement, position: Int) {
            title.text = statement.title
            desc.text = statement.desc
            date.text = statement.date


            val value1 = statement.value!!

            val decimalFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("pt", "BR")))
            val valorFormatado = decimalFormat.format(value1)
            value.text = valorFormatado


        }
    }
}
