package systems.ajax.codetests.infrastructure.testrail.repository

import io.cucumber.gherkin.GherkinParser
import io.cucumber.messages.types.Envelope
import io.cucumber.messages.types.Feature
import io.cucumber.messages.types.Scenario
import io.cucumber.messages.types.StepKeywordType.ACTION
import io.cucumber.messages.types.StepKeywordType.CONJUNCTION
import io.cucumber.messages.types.StepKeywordType.CONTEXT
import io.cucumber.messages.types.StepKeywordType.OUTCOME
import io.cucumber.messages.types.StepKeywordType.UNKNOWN
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import systems.ajax.codetests.application.extensions.isValidFile
import systems.ajax.codetests.application.model.FileId
import systems.ajax.codetests.application.model.FilePath
import systems.ajax.codetests.application.model.appcase.AppCase
import systems.ajax.codetests.application.model.appcase.AppCase.CaseType
import systems.ajax.codetests.application.model.appcase.AppCase.PriorityType
import systems.ajax.codetests.application.model.appcase.AppStep
import systems.ajax.codetests.application.port.output.CaseRepositoryOutPort
import systems.ajax.codetests.infrastructure.FileIdWriter.writeIdToCaseFile
import systems.ajax.codetests.infrastructure.testrail.mapper.CaseMapper
import systems.ajax.codetests.infrastructure.testrail.mapper.SupportedTags
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils.DELIMITER
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils.extractSectionIdFromFile
import java.io.File
import java.io.File.separator
import java.nio.file.Paths
import kotlin.jvm.optionals.getOrNull

@Component
class CaseFileRepository : CaseRepositoryOutPort {

    override fun writeIdToFile(fileId: FileId) {
        val caseFile = File(fileId.filePath.path)

        if (caseFile.isValidFile()) {
            writeIdToCaseFile(fileId.id, fileId.filePath.path)
        } else {
            log.warn("Invalid .section file path! {}", fileId.filePath)
        }
    }

    override fun get(filePath: FilePath): AppCase {
        return getCaseFromFile(filePath.path)
    }

    private fun getCaseFromFile(url: String): AppCase {
        val envelope: Envelope = PARSER.parse(
            Paths.get(url)
        ).toList()[0]

        var tags: Map<SupportedTags, String> = mapOf()
        var title = ""
        var preconditions: String? = null
        var steps: List<AppStep> = listOf()

        envelope.gherkinDocument.flatMap { document ->
            document.feature
        }.ifPresent { feature: Feature ->
            title = feature.name
            tags = getTags(feature)
            preconditions = getPreconditions(feature)
            steps = getSteps(feature)
        }

        return AppCase(
            id = tags[SupportedTags.ID]?.toInt(),
            title = title,
            sectionId = requireNotNull(extractSectionIdFromFile(extractParentFolder(url))) {
                "sectionId should not be null"
            },
            type = tags[SupportedTags.TYPE]?.let { CaseType.valueOf(it) },
            priority = tags[SupportedTags.PRIORITY]?.let { PriorityType.valueOf(it) },
            estimate = tags[SupportedTags.ESTIMATE],
            refs = tags[SupportedTags.REFS],
            precondition = preconditions,
            steps = steps,
        )
    }

    private fun getSteps(
        feature: Feature,
    ): List<AppStep> {
        val list = mutableListOf<AppStep>()
        feature.children.forEach { child ->
            child.scenario.ifPresent { scenario ->
                populateListWithStep(scenario, list)
            }
        }
        return list
    }

    private fun populateListWithStep(
        scenario: Scenario,
        list: MutableList<AppStep>,
    ) {
        list.add(getContextAndExpected(scenario))
    }

    private fun getContextAndExpected(
        scenario: Scenario,
    ): AppStep {
        var lastKeywordType = UNKNOWN
        val content = StringBuilder()
        val expected = StringBuilder()

        scenario.steps.forEach { step ->
            when (step.keywordType.get()) {
                ACTION -> {
                    content.append(step.text)
                    lastKeywordType = ACTION
                }

                OUTCOME -> {
                    expected.append(step.text)
                    lastKeywordType = OUTCOME
                }

                CONJUNCTION -> {
                    if (lastKeywordType == ACTION) {
                        content.append("\n").append(step.text)
                    } else if (lastKeywordType == OUTCOME) {
                        expected.append("\n").append(step.text)
                    }
                }

                UNKNOWN -> log.warn("Unknown keyword type for step: {}", step)
                CONTEXT -> throw UnsupportedOperationException()
            }
        }

        return AppStep(
            content.toString(),
            expected.toString(),
        )
    }

    private fun getPreconditions(feature: Feature): String? {
        return feature.children.getOrNull(0)?.background?.getOrNull()?.description
    }

    private fun getTags(feature: Feature): Map<SupportedTags, String> {
        val tags = mutableMapOf<SupportedTags, String>()

        feature.tags
            .asSequence()
            .mapNotNull { tag ->
                val keyValue = tag.name.substring(1).split(DELIMITER)
                keyValue[0].trim().lowercase() to keyValue[1].trim()
            }
            .forEach { (key, value) ->
                when (key) {
                    SupportedTags.ID.value -> tags[SupportedTags.ID] = value
                    SupportedTags.TYPE.value -> tags[SupportedTags.TYPE] = value.uppercase()
                    SupportedTags.ESTIMATE.value -> tags[SupportedTags.ESTIMATE] = value
                    SupportedTags.PRIORITY.value -> tags[SupportedTags.PRIORITY] = value.uppercase()
                    SupportedTags.REFS.value -> tags[SupportedTags.REFS] = value
                    else -> log.warn("Unknown tag key: {}", key)
                }
            }

        return tags
    }

    private fun extractParentFolder(url: String): String =
        url.substringBeforeLast(separator)
            .plus(separator)
            .plus(META_SECTION_NAME)

    companion object {
        private val log = LoggerFactory.getLogger(CaseMapper::class.java)
        private const val META_SECTION_NAME = "_Meta.json"

        private val PARSER: GherkinParser = GherkinParser.builder()
            .includeSource(false)
            .includePickles(false)
            .includeGherkinDocument(true)
            .build()
    }
}
