package systems.ajax.codetests.application.model.appcase

data class AppCase(
    val id: Int?,
    val sectionId: Int,
    val title: String,
    val type: CaseType?,
    val priority: PriorityType?,
    val estimate: String?,
    val refs: String?,
    val steps: List<AppStep>,
    val precondition: String?,
) {

    enum class CaseType(val typeId: Int) {
        ACCEPTANCE(1),
        ACCESSIBILITY(2),
        AUTOMATED(3),
        COMPATIBILITY(4),
        DESTRUCTIVE(5),
        FUNCTIONAL(6),
        OTHER(7),
        PERFORMANCE(8),
        REGRESSION(9),
        SECURITY(10),
        SMOKE_AND_SANITY(11),
        USABILITY(12),
    }

    enum class PriorityType(val priorityId: Int) {
        LOW(1),
        MEDIUM(2),
        HIGH(3),
        CRITICAL(4),
    }
}
