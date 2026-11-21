package com.a2511500030.mahasiswa

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.ConnectException
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.a2511500030.mahasiswa.api.ApiConfig
import com.a2511500030.mahasiswa.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTestConnection.setOnClickListener {
            testConnection()
        }
    }

    private fun testConnection() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvConnectionStatus.text = "Menghubungkan ke server..."

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().getDaftarMahasiswa()
                }

                if (response.isSuccessful && response.body()?.status == true) {
                    binding.tvConnectionStatus.text =
                        "Koneksi berhasil! Server merespons."

                } else {
                    binding.tvConnectionStatus.text = when (response.code()) {
                        404 -> "Endpoint tidak ditemukan. Periksa URL API."
                        500 -> "Terjadi kesalahan pada server."
                        else -> "Koneksi gagal. Kode: ${response.code()}"
                    }
                }

            } catch (e: SocketTimeoutException) {
                binding.tvConnectionStatus.text =
                    "Koneksi timeout. Server terlalu lama merespons."

            } catch (e: UnknownHostException) {
                binding.tvConnectionStatus.text =
                    "IP server tidak ditemukan. Periksa alamat IP."

            } catch (e: ConnectException) {
                binding.tvConnectionStatus.text =
                    "Server tidak dapat dihubungi. Periksa koneksi server."

            } catch (e: Exception) {
                binding.tvConnectionStatus.text =
                    "Terjadi kesalahan: ${e.message}"
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}