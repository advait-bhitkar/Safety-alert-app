package com.pribha.womenssafetyandsecurityapp.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pribha.womenssafetyandsecurityapp.R

class ContactsAdapter(private val contactList: List<ContactsItem>) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>(){



    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val textViewSymbol: TextView = itemView.findViewById(R.id.textView16)
        val textViewName: TextView = itemView.findViewById(R.id.textView17)
        val textViewNumber: TextView = itemView.findViewById(R.id.textView18)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contacts, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {

        val currentItem = contactList[position]

        holder.textViewName.text = currentItem.name
        holder.textViewSymbol.text = currentItem.name.substring(0,1)
        holder.textViewNumber.text = currentItem.number
    }

    override fun getItemCount(): Int {

        return contactList.size

    }

}