package com.andrecampos.lucronarota.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.andrecampos.lucronarota.util.LeitorOdometro
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * Botão que abre a câmera do aparelho, tira uma foto do painel do carro e
 * usa reconhecimento de texto (ML Kit, on-device) para tentar ler o km do
 * odômetro. Se não conseguir, avisa o usuário para digitar manualmente.
 */
@Composable
fun BotaoLerKmCamera(
    onKmLido: (String) -> Unit,
    onErro: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var lendo by remember { mutableStateOf(false) }

    val capturaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap == null) {
            lendo = false
            return@rememberLauncherForActivityResult
        }
        val imagem = InputImage.fromBitmap(bitmap, 0)
        val reconhecedor = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        reconhecedor.process(imagem)
            .addOnSuccessListener { resultado ->
                lendo = false
                val km = LeitorOdometro.extrairKm(resultado.text)
                if (km != null) {
                    onKmLido(km)
                } else {
                    onErro("Não consegui identificar o km no painel. Tire a foto de novo ou digite manualmente.")
                }
            }
            .addOnFailureListener {
                lendo = false
                onErro("Falha ao ler a imagem. Digite o km manualmente.")
            }
    }

    val permissaoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) {
            lendo = true
            capturaLauncher.launch(null)
        } else {
            onErro("Permissão de câmera negada. Digite o km manualmente.")
        }
    }

    OutlinedButton(
        onClick = {
            val temPermissao = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (temPermissao) {
                lendo = true
                capturaLauncher.launch(null)
            } else {
                permissaoLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        enabled = !lendo,
        modifier = modifier
    ) {
        if (lendo) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Lendo painel...")
        } else {
            Icon(Icons.Filled.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ler km pela câmera")
        }
    }
}
