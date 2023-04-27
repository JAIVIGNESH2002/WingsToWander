package com.example.wanderwings

import android.app.Activity
import android.content.ContentResolver
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EmergencyContacts.newInstance] factory method to
 * create an instance of this fragment.
 */

class EmergencyContacts : Fragment() {
    private lateinit var  contactPickerContract : ActivityResultLauncher<Void?>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    val items = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency_contacts, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Recycler view
        val recyclerView =view.findViewById<RecyclerView>(R.id.emergency_view)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = RecyclerViewAdapter(items)
        recyclerView?.adapter = adapter

        contactPickerContract = registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->

            if (contactUri != null)  {
                val contentResolver: ContentResolver? = context?.contentResolver
                val projection = arrayOf(
                    ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY)
                val cursor = contentResolver?.query(
                    contactUri,
                    projection,
                    null,
                    null,
                    null
                )

                if (cursor != null && cursor.moveToFirst()) {
                    val name = cursor.getString(0)
                    val contactId = cursor.getLong(1)
                    val mContactKey = cursor.getString(2)
                    val contactUri = ContactsContract.Contacts.getLookupUri(contactId, mContactKey)

                    items.add(name)
                    adapter.notifyDataSetChanged()
                }
                cursor?.close()
            }
        }

        val addEcon = view.findViewById<FloatingActionButton>(R.id.addEcon)
        addEcon?.setOnClickListener {
            contactPickerContract.launch(null)

        }

    }


}