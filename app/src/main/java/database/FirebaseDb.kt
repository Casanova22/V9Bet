package database

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import model.AppModel

class FirebaseDB {
    lateinit var databaseReference: DatabaseReference

    fun getDatabase(): DatabaseReference {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("App3")
        return databaseReference
    }

    fun add(listModel: AppModel): Task<Void> {
        return databaseReference.push().setValue(listModel)
    }

    fun update(key : String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference.child(key).updateChildren(hashMap)
    }

    fun remove(key : String) : Task<Void> {
        return databaseReference.child(key).removeValue()

    }

}