package com.example.finnishmp_app.db

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.finnishmp_app.ApiConnector
import com.example.finnishmp_app.FinnishMPApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

//Muche Berhanu 2219580

// Object to manage the database synchronization process
object DatabaseSyncManager {
    // Starts the periodic background work for database synchronization
    fun start() {
        // Creates a periodic work request that runs every 15 minutes
        val uploadRequest = PeriodicWorkRequestBuilder<DBWorker>(15, TimeUnit.MINUTES).build()
        val workManager = WorkManager.getInstance(FinnishMPApp.appContext)
        // Enqueues the work request for execution
        workManager.enqueue(uploadRequest)
    }
}

// Worker class for performing background synchronization of the database
class DBWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    // Main method to perform the background work
    override suspend fun doWork(): Result {
        val db = PMDatabase.getInstance() // Get the instance of the database
        val dao = db.memberDao()          // Access the Data Access Object (DAO) for database operations

        return try {
            // Fetch the updated list of parliament members from the remote API
            val parliamentMembers = fetchParliamentMembers()
            if (parliamentMembers.isEmpty()) {
                // Log an error and return failure if the fetched list is empty
                Log.e("DBWorker", "Failed to fetch parliament members.")
                return Result.failure()
            }

            // Update the local database with the fetched list of members
            updateDatabase(dao, parliamentMembers)

            // Log a success message and return a successful result
            Log.d("DBWorker", "Data successfully synchronized with the remote server.")
            Result.success()

        } catch (e: Exception) {
            // Log the exception and return a retry result for transient failures
            Log.e("DBWorker", "Data synchronization failed: ${e.message}")
            Result.retry()
        }
    }

    // Fetches parliament members from the remote API, merging main data with extra data
    private suspend fun fetchParliamentMembers(): List<ParliamentMember> {
        return withContext(Dispatchers.IO) { // Perform the network operations on an I/O thread
            val mainData = ApiConnector.apiService.loadMainData()?.execute()?.body()
            val extraData = ApiConnector.apiService.loadExtraData()?.execute()?.body()

            // Check if any of the fetched data is null, and throw an exception if so
            if (mainData == null || extraData == null) {
                throw Exception("Failed to fetch data from the API.")
            }

            // Merge the main data with extra data based on matching Heteka IDs
            mainData.map { memberData1 ->
                val memberData2 = extraData.find { it.hetekaId == memberData1.hetekaId }
                // Return a new member with additional fields if found in extra data
                memberData1.copy(
                    twitter = memberData2?.twitter,
                    bornYear = memberData2?.bornYear,
                    constituency = memberData2?.constituency
                )
            }
        }
    }

    // Updates the local database with the list of parliament members
    private suspend fun updateDatabase(dao: PMDao, members: List<ParliamentMember>) {
        withContext(Dispatchers.IO) { // Perform the database operation on an I/O thread
            dao.insertAll(members)    // Insert or update the list of members in the database
        }
    }
}
