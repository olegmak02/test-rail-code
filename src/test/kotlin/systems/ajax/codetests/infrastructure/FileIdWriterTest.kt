// package systems.ajax.codetests.infrastructure
//
// import java.io.File
// import kotlin.test.assertTrue
// import org.junit.jupiter.api.Test
//
// internal class FileIdWriterTest {
//
//    @Test
//    fun `should write id to the feature file`() {
//        // GIVEN
//        val parentPath = "src/test/kotlin/systems/ajax/codetests/infrastructure/testfilesforidwriter"
//        val featureFileName = "withoutId.feature"
//        val idToWrite = 1
//        val file = File(parentPath, featureFileName)
//        file.createNewFile()
//
//        // WHEN
//        FileIdWriter.writeIdToCaseFile(idToWrite, "$parentPath/$featureFileName")
//
//        // THEN
//        val text = file.bufferedReader().use { it.readLines() }
//        assertTrue(text[0].contains("@id:$idToWrite"), "Feature file should contain id")
//        file.delete()
//    }
//
//    @Test
//    fun `should write ids to the feature files only`() {
//        // GIVEN
//        val parentPath = "src/test/kotlin/systems/ajax/codetests/infrastructure/testfilesforidwriter"
//        val wrongFile = "wrongFile.text"
//        val idToWrite = 1
//
//        // WHEN THEN
//        FileIdWriter.writeIdToCaseFile(idToWrite, "$parentPath/$wrongFile")
//    }
//
//    @Test
//    fun `should do nothing when write to the non-existing feature file`() {
//        // GIVEN
//        val parentPath = "src/test/kotlin/systems/ajax/codetests/infrastructure/testfilesforidwriter"
//        val idToWrite = 1
//
//        // WHEN THEN
//        FileIdWriter.writeIdToCaseFile(idToWrite, parentPath)
//    }
//
//    @Test
//    fun `should do nothing when id exists in the feature file`() {
//        // GIVEN
//        val parentPath = "src/test/kotlin/systems/ajax/codetests/infrastructure/testfilesforidwriter"
//        val featureFileName = "withId.feature"
//        val idToWrite = 1
//        val file = File(parentPath, featureFileName)
//
//        // WHEN
//        FileIdWriter.writeIdToCaseFile(idToWrite, "$parentPath/$featureFileName")
//
//        // THEN
//        val text = file.bufferedReader().use { it.readLines() }
//        assertTrue(text[0].contains("@id:$idToWrite"), "Feature file should contain id")
//    }
// }
