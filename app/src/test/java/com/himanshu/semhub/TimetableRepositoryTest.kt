package com.himanshu.semhub

import android.util.Log
import com.himanshu.semhub.data.remote.ApiService
import com.himanshu.semhub.data.repository.TimetableRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class TimetableRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var repository: TimetableRepository

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Simulating API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        repository = TimetableRepository(apiService)
    }

    @Test
    fun uploadTimeTableTest() = runBlocking {
        // Simulate API response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
           {"Monday":[["08:00 AM - 08:50 AM","Malware Analysis (Theory Only)"],["09:00 AM - 09:50 AM","Data Privacy (Theory Only)"],["10:00 AM - 10:50 AM","Web Application Security (Theory Only)"],["11:00 AM - 11:50 AM","Digital Watermarking and Steganography (Theory Only)"],["12:00 PM - 12:50 PM","Information Security (Theory Only)"],["03:00 PM - 03:50 PM","Spanish I (Theory Only)"],["05:40 PM - 07:20 PM","Penetration Testing and Vulnerability Analysis Lab (Lab Only)"]],"Tuesday":[["08:00 AM - 08:50 AM","Digital Watermarking and Steganography (Theory Only)"],["09:00 AM - 09:50 AM","Information Security (Theory Only)"],["10:00 AM - 10:50 AM","Digital Forensics (Theory Only)"],["05:40 PM - 07:20 PM","Malware Analysis Lab (Lab Only)"]],"Wednesday":[["08:00 AM - 08:50 AM","Penetration Testing and Vulnerability Analysis (Theory Only)"],["09:00 AM - 09:50 AM","Malware Analysis (Theory Only)"],["10:00 AM - 10:50 AM","Data Privacy (Theory Only)"],["04:00 PM - 04:50 PM","Spanish I (Theory Only)"]],"Thursday":[["08:00 AM - 08:50 AM","Web Application Security (Theory Only)"],["09:00 AM - 09:50 AM","Digital Watermarking and Steganography (Theory Only)"],["10:00 AM - 10:50 AM","Information Security (Theory Only)"],["03:51 PM - 05:30 PM","Digital Forensics Lab (Lab Only)"]],"Friday":[["08:00 AM - 08:50 AM","Digital Forensics (Theory Only)"],["09:00 AM - 09:50 AM","Penetration Testing and Vulnerability Analysis (Theory Only)"],["11:00 AM - 11:50 AM","Data Privacy (Theory Only)"],["12:00 PM - 12:50 PM","Web Application Security (Theory Only)"]],"Saturday":[],"Sunday":[]}
        """.trimIndent())

        mockWebServer.enqueue(mockResponse)

        val file = File("src/test/resources/timetable.jpeg")

        val response = repository.getTimeTable(file)
        println()
        println()
        println(response.body()?.monday.toString())
        println()
        println()
        assertEquals(true, response.isSuccessful)
        assertNotNull(response.body().toString())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    companion object{
        val TAG = "Timetable testing"
    }


}