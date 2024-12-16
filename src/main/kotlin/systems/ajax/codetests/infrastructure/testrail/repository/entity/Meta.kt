package systems.ajax.codetests.infrastructure.testrail.repository.entity

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val id: Int? = null,
    val description: String? = null,
)
