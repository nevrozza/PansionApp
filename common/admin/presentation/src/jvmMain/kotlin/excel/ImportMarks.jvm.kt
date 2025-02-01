package excel

import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream


actual fun importMarks(path: String) {
    val inputStream = FileInputStream(File(path))
    val workbook = WorkbookFactory.create(inputStream)

    workbook.forEach { ws ->
        print(ws)
    }
}