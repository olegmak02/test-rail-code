package systems.ajax.codetests.infrastructure.testrail.mapper

import client.testrail.model.Case
import client.testrail.model.Field
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
import systems.ajax.codetests.infrastructure.testrail.mapper.CustomFields.PRECONDITIONS
import systems.ajax.codetests.infrastructure.testrail.mapper.CustomFields.STEPS
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils.DELIMITER
import systems.ajax.codetests.infrastructure.testrail.repository.ExtractionUtils.extractSectionIdFromFile
import java.io.File.separator
import java.nio.file.Paths

internal object CaseMapper {

    fun mapFileToCase(url: String): Case {
        val envelope: Envelope = PARSER.parse(
            Paths.get(url)
        ).toList()[0]

        val case = Case()

        envelope.gherkinDocument.flatMap { document ->
            document.feature
        }.ifPresent { feature: Feature ->
            case.title = feature.name
            extractSectionIdFromFile(extractParentFolder(url))?.let { case.sectionId = it }
            setTags(feature, case)
            setPreconditions(feature, case)
            setSteps(feature, case)
        }

        return case
    }

    private fun setSteps(
        feature: Feature,
        case: Case,
    ) {
        val list = mutableListOf<Field.Step>()
        feature.children.forEach { child ->
            child.scenario.ifPresent { scenario ->
                populateListWithStep(scenario, list)
            }
        }
        case.addCustomField(STEPS.value, list)
    }

    private fun populateListWithStep(
        scenario: Scenario,
        list: MutableList<Field.Step>,
    ) {
        val caseStep = Field.Step()
        populateContextAndExpected(scenario, caseStep)
        list.add(caseStep)
    }

    private fun populateContextAndExpected(
        scenario: Scenario,
        caseStep: Field.Step,
    ) {
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

        caseStep.expected = expected.toString()
        caseStep.content = content.toString()
    }

    private fun setPreconditions(feature: Feature, case: Case) {
        feature.children[0].background.ifPresent {
            case.addCustomField(PRECONDITIONS.value, it.description)
        }
    }

    private fun setTags(feature: Feature, case: Case) {
        feature.tags
            .asSequence()
            .mapNotNull { tag ->
                val keyValue = tag.name.substring(1).split(DELIMITER)
                keyValue[0].trim().lowercase() to keyValue[1].trim()
            }
            .forEach { (key, value) ->
                when (key) {
                    SupportedTags.ID.value -> case.id = value.toInt()
                    SupportedTags.TYPE.value -> case.typeId = value.toIntOrNull()
                    SupportedTags.ESTIMATE.value -> case.estimate = value
                    SupportedTags.PRIORITY.value -> case.priorityId = value.toIntOrNull()
                    SupportedTags.REFS.value -> case.refs = value
                    else -> log.warn("Unknown tag key: {}", key)
                }
            }
    }

    private fun extractParentFolder(url: String): String =
        url.substringBeforeLast(separator)
            .plus(separator)
            .plus(META_SECTION_NAME)

    private val log = LoggerFactory.getLogger(CaseMapper::class.java)
    private const val META_SECTION_NAME = "_Meta.json"

    private val PARSER: GherkinParser = GherkinParser.builder()
        .includeSource(false)
        .includePickles(false)
        .includeGherkinDocument(true)
        .build()
}
