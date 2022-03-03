package com.pribha.womenssafetyandsecurityapp.ui.contacts

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pribha.womenssafetyandsecurityapp.R
import com.wafflecopter.multicontactpicker.ContactResult
import com.wafflecopter.multicontactpicker.LimitColumn
import com.wafflecopter.multicontactpicker.MultiContactPicker
import kotlin.math.sign


class ContactsFragment : Fragment() {

    private lateinit var dashboardViewModel: ContactViewModel
    private lateinit var addContactsButton: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactsAdapter
    private lateinit var contactList: List<ContactsItem>
    val CONTACT_PICKER_REQUEST = 88
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(ContactViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)

        addContactsButton = root.findViewById(R.id.materialButton)
        recyclerView = root.findViewById(R.id.recyclerView2)
        return root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerview()

        addContactsButton.setOnClickListener(View.OnClickListener {

            addContacts()


        })


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                val db = Firebase.firestore
                val docRef =db.collection("users").document("${FirebaseAuth.getInstance().uid}")
                    .collection("contacts")
                val source = Source.CACHE

                val results: List<ContactResult> = MultiContactPicker.obtainResult(data)

                var docCount: Int = 0

                docRef.get(source)
                    .addOnSuccessListener { documents ->

                        docCount = documents.size().toInt() + results.size.toInt()


                        if (docCount  < 4)
                        {
                            for (result in results){
                                saveContactsToFireStore(result.phoneNumbers[0].number.toString(),result.displayName)

                                val list = ContactsItem(result.displayName , result.phoneNumbers[0].toString())
                                contactList = contactList + list
                            }
                            recyclerView.adapter?.notifyDataSetChanged()
                            Toast.makeText(requireContext(), "$docCount Contacts", Toast.LENGTH_SHORT).show()

                        }
                        else
                        {
                            Toast.makeText(requireContext(), "You can only add up to 3 Contacts", Toast.LENGTH_SHORT).show()

                        }

                    }



//                Log.d("MyfefeeeTag",  results[0].phoneNumbers[0].number.toString())




            } else if (resultCode == RESULT_CANCELED) {
                println("User closed the picker without selecting items.")
            }
        }

    }


    private fun setupRecyclerview(){

        contactList = ArrayList<ContactsItem>()

        val db = Firebase.firestore
        val docRef =db.collection("users").document("${FirebaseAuth.getInstance().uid}")
            .collection("contacts")





        docRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val name = document.get("name")
                    val phoneNumber = document.get("phoneNo")

//                    Toast.makeText(requireContext(),   name.toString(), Toast.LENGTH_SHORT).show()

                    val list = ContactsItem(name.toString() , phoneNumber.toString())
                    contactList = contactList + list
                }


                adapter = ContactsAdapter(contactList)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.setHasFixedSize(true)


            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


//        adapter = ContactsAdapter(contactList)
//        recyclerView.adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.setHasFixedSize(true)
//
    }

    private fun addContacts(){


        MultiContactPicker.Builder(requireActivity()) //Activity/fragment context
//                .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
            .hideScrollbar(false) //Optional - default: false
            .showTrack(true) //Optional - default: true
            .searchIconColor(Color.WHITE) //Option - default: White
            .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
            .handleColor(ContextCompat.getColor(requireActivity(), R.color.blue)) //Optional - default: Azure Blue
            .bubbleColor(ContextCompat.getColor(requireActivity(), R.color.blue)) //Optional - default: Azure Blue
            .bubbleTextColor(Color.WHITE) //Optional - default: White
            .setTitleText("Select Contacts") //Optional - default: Select Contacts
            .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
            .limitToColumn(LimitColumn.PHONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
            .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out) //Optional - default: No animation overrides
            .showPickerForResult(CONTACT_PICKER_REQUEST)




    }
    
    
    
    private fun saveContactsToFireStore( phoneNumber: String, name: String){
        val db = Firebase.firestore

        // Create a new user with a first and last name
        val contact = hashMapOf(
            "phoneNo" to phoneNumber,
            "name" to name
        )

        db.collection("users").document("${FirebaseAuth.getInstance().uid}")
            .collection("contacts")
            .document(phoneNumber)
            .set(contact)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }


    }
}