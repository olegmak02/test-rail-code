// package systems.ajax.codetests.infrastructure.testrail.mapper
//
// import client.testrail.model.Case
// import client.testrail.model.Field
// import org.junit.jupiter.api.Test
// import systems.ajax.codetests.infrastructure.testrail.mapper.CaseMapper.mapFileToCase
// import systems.ajax.codetests.infrastructure.testrail.mapper.CustomFields.PRECONDITIONS
// import systems.ajax.codetests.infrastructure.testrail.mapper.CustomFields.STEPS
// import kotlin.test.assertEquals
//
// internal class CaseMapperTest {
//
//    @Test
//    fun `should return valid case`() {
//        // GIVEN
//        val url = "src/test/kotlin/systems/ajax/codetests/infrastructure/" +
//            "testrail/mapper/testdata/testfolder/Success_Creating_xxx.feature"
//        val steps: List<Field.Step> = listOf(
//            Field.Step()
//                .setContent(
//                    "I wait 1 hours\n" +
//                        "User is PRO"
//                )
//                .setExpected(
//                    "my belly should growl\n" +
//                        "I have 42 cukes in my belly"
//                ),
//            Field.Step()
//                .setContent("I wait 1 hours")
//                .setExpected("my belly should growl"),
//        )
//        val excepted: Case = Case()
//            .setId(1)
//            .setTypeId(1)
//            .setPriorityId(1)
//            .setEstimate("1m")
//            .setRefs("REF1,REF2")
//            .setTitle("Success creating xxx")
//            .setSectionId(2)
//            .addCustomField(
//                PRECONDITIONS.value,
//                "      sdsd dasdasd\n" +
//                    "      dsadas  dsadsa"
//            )
//            .addCustomField(STEPS.value, steps)
//
//        // WHEN
//        val actual: Case = mapFileToCase(url)
//
//        // THEN
//        assertEquals(excepted, actual)
//    }
// }
