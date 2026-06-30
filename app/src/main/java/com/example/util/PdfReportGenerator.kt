package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.Entry
import java.io.File
import java.io.FileOutputStream

object PdfReportGenerator {

    fun generateDoctorReport(
        context: Context,
        doctorName: String,
        monthYear: String,
        entries: List<Entry>
    ): File? {
        try {
            val pdfDocument = PdfDocument()
            // A4 Size: 595 x 842
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.rgb(10, 80, 150)
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                isAntiAlias = true
            }

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val boldPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val headerPaint = Paint().apply {
                color = Color.WHITE
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val rectPaint = Paint().apply {
                style = Paint.Style.FILL
            }

            val linePaint = Paint().apply {
                color = Color.LTGRAY
                strokeWidth = 1f
            }

            var y = 50f

            // --- Header Banner ---
            rectPaint.color = Color.rgb(10, 80, 150)
            canvas.drawRect(30f, y, 565f, y + 60f, rectPaint)

            headerPaint.textSize = 20f
            canvas.drawText("MICRO PATHOLOGY LAB", 50f, y + 38f, headerPaint)
            headerPaint.textSize = 10f
            headerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            canvas.drawText("Professional Laboratory Diagnostic Reports & Commission Statement", 50f, y + 52f, headerPaint)

            y += 85f

            // --- Report Info ---
            canvas.drawText("DOCTOR COMMISSION REPORT", 30f, y, titlePaint)
            y += 20f
            canvas.drawText("Doctor Name: $doctorName", 30f, y, boldPaint)
            canvas.drawText("Billing Period: $monthYear", 350f, y, textPaint)
            y += 15f
            canvas.drawText("Generated On: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}", 30f, y, textPaint)
            canvas.drawText("Total Entries: ${entries.size}", 350f, y, textPaint)

            y += 30f

            // --- Table Headers ---
            rectPaint.color = Color.rgb(40, 50, 60)
            canvas.drawRect(30f, y, 565f, y + 25f, rectPaint)

            headerPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            headerPaint.textSize = 9f
            canvas.drawText("Date", 35f, y + 16f, headerPaint)
            canvas.drawText("Patient Name", 110f, y + 16f, headerPaint)
            canvas.drawText("Age", 240f, y + 16f, headerPaint)
            canvas.drawText("Test Type", 280f, y + 16f, headerPaint)
            canvas.drawText("Amount (₹)", 380f, y + 16f, headerPaint)
            canvas.drawText("Comm (₹)", 450f, y + 16f, headerPaint)
            canvas.drawText("Other (₹)", 510f, y + 16f, headerPaint)

            y += 25f

            var totalAmount = 0.0
            var totalComm = 0.0
            var totalOther = 0.0

            // --- Table Content ---
            for ((index, entry) in entries.withIndex()) {
                // Handle pagination if table goes too long (simple multi-page support)
                if (y > 750f) {
                    pdfDocument.finishPage(page)
                    val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
                    page = pdfDocument.startPage(newPageInfo)
                    canvas = page.canvas
                    y = 50f
                    
                    // Re-draw table headers on new page
                    rectPaint.color = Color.rgb(40, 50, 60)
                    canvas.drawRect(30f, y, 565f, y + 25f, rectPaint)
                    canvas.drawText("Date", 35f, y + 16f, headerPaint)
                    canvas.drawText("Patient Name", 110f, y + 16f, headerPaint)
                    canvas.drawText("Age", 240f, y + 16f, headerPaint)
                    canvas.drawText("Test Type", 280f, y + 16f, headerPaint)
                    canvas.drawText("Amount (₹)", 380f, y + 16f, headerPaint)
                    canvas.drawText("Comm (₹)", 450f, y + 16f, headerPaint)
                    canvas.drawText("Other (₹)", 510f, y + 16f, headerPaint)
                    y += 25f
                }

                // Alternate background shading
                if (index % 2 == 1) {
                    rectPaint.color = Color.rgb(245, 247, 250)
                    canvas.drawRect(30f, y, 565f, y + 20f, rectPaint)
                }

                canvas.drawText(entry.date, 35f, y + 14f, textPaint)
                
                // Truncate patient name if too long to avoid overlaps
                val dispName = if (entry.patientName.length > 22) entry.patientName.substring(0, 20) + ".." else entry.patientName
                canvas.drawText(dispName, 110f, y + 14f, textPaint)
                
                canvas.drawText(entry.age.toString(), 240f, y + 14f, textPaint)
                
                val dispTest = if (entry.test.length > 16) entry.test.substring(0, 14) + ".." else entry.test
                canvas.drawText(dispTest, 280f, y + 14f, textPaint)
                
                canvas.drawText(String.format("₹%.2f", entry.amount), 380f, y + 14f, textPaint)
                canvas.drawText(String.format("₹%.2f", entry.doctorAmount), 450f, y + 14f, textPaint)
                canvas.drawText(String.format("₹%.2f", entry.otherAmount), 510f, y + 14f, textPaint)

                totalAmount += entry.amount
                totalComm += entry.doctorAmount
                totalOther += entry.otherAmount

                canvas.drawLine(30f, y + 20f, 565f, y + 20f, linePaint)
                y += 20f
            }

            // --- Totals Row ---
            if (y > 730f) {
                pdfDocument.finishPage(page)
                val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
                page = pdfDocument.startPage(newPageInfo)
                canvas = page.canvas
                y = 50f
            }

            rectPaint.color = Color.rgb(230, 240, 250)
            canvas.drawRect(30f, y, 565f, y + 30f, rectPaint)

            canvas.drawText("TOTALS", 35f, y + 19f, boldPaint)
            canvas.drawText(String.format("₹%.2f", totalAmount), 380f, y + 19f, boldPaint)
            canvas.drawText(String.format("₹%.2f", totalComm), 450f, y + 19f, boldPaint)
            canvas.drawText(String.format("₹%.2f", totalOther), 510f, y + 19f, boldPaint)

            y += 50f

            // --- Footer Area ---
            canvas.drawLine(30f, y, 565f, y, linePaint)
            y += 20f

            val notePaint = Paint().apply {
                color = Color.GRAY
                textSize = 8f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            canvas.drawText("* This is a computer-generated commission statement. No signature is required.", 30f, y, notePaint)
            canvas.drawText("Page ${pdfDocument.pages.size} of ${pdfDocument.pages.size}", 500f, y, notePaint)

            pdfDocument.finishPage(page)

            // Save PDF
            val file = File(context.getExternalFilesDir(null), "Commission_Report_${doctorName.replace(" ", "_")}_$monthYear.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun openPdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "com.aistudio.micropathlab.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
        }
    }

    fun sharePdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "com.aistudio.micropathlab.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Doctor Commission Report")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share Commission Report via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
