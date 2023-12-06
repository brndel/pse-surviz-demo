import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import render.ImageInput
import java.util.*

class Project(private val blocks: List<Block> = emptyList()) {
    val configuration = ProjectConfiguration()

    fun getImageInput(blockId: Int, situationId: Int): List<ImageInput>? {
        val situation = blocks.getOrNull(blockId)?.situations?.getOrNull(situationId) ?: return null

        return configuration.selectOptionOrder.map { optionName ->
            val selectOption = configuration.selectOptionConfig[optionName]!!

            val singleValue = configuration.singleValueIds.map {
                configuration.singleValueAttributes[it]!! to selectOption.singleValueColumns[it]
            }.map {
                val attr = it.first
                val attributeName = it.second
                if (attributeName != null) {
                    val value = situation.getAttribute(optionName, attributeName) ?: return@map null
                    attr to value
                } else {
                    null
                }
            }.map {
                if (it == null) return@map null
                val attr = it.first
                val value = it.second
                ImageInput.SingleValue(attr.icon.getIcon(value), "$value ${attr.unit.value}")
            }

            val timeline = selectOption.timeAttributes.map {
                val value = situation.getAttribute(optionName, it.column.value) ?: 0.0

                ImageInput.TimeValue(it.icon.value, value, it.lineType.value)
            }

            ImageInput(selectOption.name.value, selectOption.color.value, singleValue, timeline)
        }
    }

    companion object {
        public fun createExampleProject(): Project {
            val dataMap = mapOf(
                "fuss.fz_fuss" to 15.0,
                "rad.fz_rad" to 5.0,
                "car.fz_miv" to 10.0,
                "car.zugang" to 3.0,
                "car.abgang" to 0.0,
                "car.cost_car" to 3.0,
                "oev_fuss.fz_oev" to 13.0,
                "oev_fuss.cost_oev" to 1.5,
                "oev_fuss.freq_oev" to 30.0,
                "oev_fuss.zugang_oevfuss" to 10.0,
                "shuttle_tb.fz_oev" to 9.0,
                "shuttle_tb.cost_oev" to 1.5,
                "shuttle_tb.freq_oev" to 10.0,
                "shuttle_tb.zugang_oevfuss" to 6.0,
                "shuttle_od.fz_oev" to 13.0,
                "shuttle_od.cost_oev" to 2.0,
                "shuttle_od.warten" to 7.0,

            )
            val situation = Situation(
                dataMap.keys.toList(),
                dataMap.values.toList(),
            )

            val block = Block(listOf(situation))

            val project = Project(listOf(block));

            project.configuration.addSelectOption("fuss", listOf("fz_fuss"))
            project.configuration.addSelectOption("rad", listOf("fz_rad"))
            project.configuration.addSelectOption("car", listOf("fz_miv", "zugang", "abgang", "cost_car"))
            project.configuration.addSelectOption("oev_fuss", listOf("fz_oev", "cost_oev", "freq_oev", "zugang_oevfuss"))
            project.configuration.addSelectOption("shuttle_tb", listOf("fz_oev", "cost_oev", "freq_oev", "zugang_oevfuss"))
            project.configuration.addSelectOption("shuttle_od", listOf("fz_oev", "cost_oev", "warten"))


            return project;
        }
    }
}

class Block(val situations: List<Situation> = emptyList()) {
}

class Situation(header: List<String>, data: List<Double>) {
    private val attributes: Map<String, Double>

    init {
        attributes = header.zip(data).toMap()
    }

    fun getAttribute(optionName: String, attributeName: String): Double? {
        return attributes["$optionName.$attributeName"]
    }
}

class ProjectConfiguration {
    val singleValueIds = mutableStateListOf<UUID>()
    val singleValueAttributes = mutableStateMapOf<UUID, SingleValueAttribute>()

    val selectOptionOrder = mutableStateListOf<String>()
    val selectOptionConfig = mutableStateMapOf<String, SelectOptionConfig>()

    fun addSingleValue(): UUID {
        val id = UUID.randomUUID()
        val attr = SingleValueAttribute()

        singleValueAttributes[id] = attr
        singleValueIds.add(id)
        return id
    }

    fun addSelectOption(name: String, attributes: List<String>) {
        selectOptionOrder.add(name)
        selectOptionConfig[name] = SelectOptionConfig(attributes)
    }

    fun removeSingleValue(id: UUID) {
        singleValueAttributes.remove(id)
        singleValueIds.remove(id)
    }

    fun reorderSingleValues(a: Int, b: Int) {
        Collections.swap(singleValueIds, a, b)
    }
}

class SingleValueAttribute {
    val unit = mutableStateOf("")
    val columnSchema = mutableStateOf("")
    val icon = SingleValueIcon()
}

class SingleValueIcon {
    val baseIcon = mutableStateOf<String?>(null)
    val iconLevels = mutableStateListOf<IconLevel>()

    fun addLevel() {
        iconLevels.add(IconLevel())
    }

    fun removeLevel(level: IconLevel) {
        iconLevels.remove(level)
    }

    fun getIcon(value: Double): String? {
        return baseIcon.value // TODO
    }
}

class IconLevel {
    val icon = mutableStateOf<String?>(null)
    val threshold = mutableStateOf(0F)
}

class SelectOptionConfig(
    val attributes: List<String>
) {
    val name = mutableStateOf("")
    val color = mutableStateOf(Color.Blue)
    val singleValueColumns = mutableStateMapOf<UUID, String>()
    val timeAttributes = mutableStateListOf<TimeAttribute>()

    fun findAttributeByPattern(pattern: String): String? {
        val parts = pattern.split("*")
        val prefix = if (parts.count() == 2 && parts[1].isEmpty()) {
            parts[0]
        } else {
            null
        }

        for (attr in attributes) {
            if (prefix?.run { attr.startsWith(this) } ?: (attr == pattern)) {
                return attr
            }
        }

        return null
    }

    fun addTimeAttribute() {
        timeAttributes.add(TimeAttribute())
    }

    fun removeTimeAttribute(attribute: TimeAttribute) {
        timeAttributes.remove(attribute)
    }

    fun reorderTimeAttributes(a: Int, b: Int) {
        Collections.swap(timeAttributes, a, b)
    }
}

class TimeAttribute {
    val icon = mutableStateOf<String?>(null)
    val column = mutableStateOf("")
    val lineType = mutableStateOf(LineType.Solid)
}

enum class LineType(val optionName: String, val pathEffect: PathEffect?) {
    Solid("Solide", null),
    Dotted("Gestrichelt", PathEffect.dashPathEffect(floatArrayOf(16.0F, 16.0F))),
    DottedSmall("Klein Gestrichelt", PathEffect.dashPathEffect(floatArrayOf(8.0F, 8.0F))),
    DotLine("Punkt - Strich", PathEffect.dashPathEffect(floatArrayOf(2.0F, 4.0F, 16.0F, 4.0F))),
}