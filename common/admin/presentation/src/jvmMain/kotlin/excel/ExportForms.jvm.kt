package excel

import admin.groups.forms.Form
import io.github.evanrupert.excelkt.workbook
import server.getDate

actual fun exportForms(forms: List<Form>) {

    workbook {
        sheet {
            val topStyle = createCellStyle {
                setFont(createFont {
                    bold = true
                })
            }
            row(topStyle) {
                cell("Id")
                cell("Короткое")
                cell("Полное название")
            }
            val normalStyle = createCellStyle {
                setFont(createFont {
                    bold = false
                })
            }

            forms.filter { it.isActive }.forEach { f ->
                row(normalStyle) {
                    cell(f.id)
                    cell("${f.form.classNum}${if (f.form.shortTitle.length < 2) "-" else " "}${f.form.shortTitle}")
                    cell("${f.form.classNum} ${f.form.title}")
                }
            }
        }
    }.write("Классы ${getDate()}.xlsx")
}