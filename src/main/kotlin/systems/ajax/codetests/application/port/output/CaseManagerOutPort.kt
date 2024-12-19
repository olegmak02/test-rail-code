package systems.ajax.codetests.application.port.output

import client.testrail.model.Case
import client.testrail.model.CaseField
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.appcase.AppCase

interface CaseManagerOutPort {
    fun add(appCase: AppCase, customCaseFields: List<CaseField>): Int
    fun update(appCase: AppCase, customCaseFields: List<CaseField>): Case
    fun delete(filePath: FilePath)
    fun move(appCase: AppCase, customCaseFields: List<CaseField>)
}
