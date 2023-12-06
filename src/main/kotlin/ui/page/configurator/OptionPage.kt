package ui.page.configurator

import ColorField
import LineType
import Project
import ProjectConfiguration
import SelectOptionConfig
import SingleValueAttribute
import TimeAttribute
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.*
import ui.component.IconField

@Composable
fun OptionPage(configuration: ProjectConfiguration, modifier: Modifier) {
    var selectedTab by remember { mutableStateOf(0) }
    var color by remember { mutableStateOf(Color.Red) }

    Surface(modifier.padding(16.dp).fillMaxHeight(), shape = RoundedCornerShape(4.dp)) {
        Column {
            TabRow(selectedTab) {
                configuration.selectOptionOrder.forEachIndexed { index, name ->
                    Tab(
                        selected = index == selectedTab,
                        onClick = { selectedTab = index },
                    ) {
                        Text(name, modifier = Modifier.padding(0.dp, 16.dp))
                    }
                }
            }
            SelectOption(
                configuration.selectOptionConfig[configuration.selectOptionOrder[selectedTab]]!!,
                configuration
            )
        }
    }
}

@Composable
fun SelectOption(option: SelectOptionConfig, configuration: ProjectConfiguration) {
    var name by option.name
    var color by option.color

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(option.attributes.joinToString(", "))

        TextField(name, onValueChange = {
            name = it
        }, label = { Text("Name") }, singleLine = true)

        ColorField(color, onValueChange = {
            color = it
        }, label = { Text("Color") })

        SingleValueConfig(option, configuration)
        TimeAttributeConfig(option, configuration)
    }
}

@Composable
fun SingleValueConfig(option: SelectOptionConfig, configuration: ProjectConfiguration) {
    Card(
        backgroundColor = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text("Einzelwerte")
            for (singleValueId in configuration.singleValueIds) {
                val singleValue = configuration.singleValueAttributes[singleValueId]!!
                val icon = singleValue.icon.baseIcon.value
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (icon != null) {
                        Icon(painterResource(icon), null, Modifier.size(64.dp))
                    }
                    TextField(option.singleValueColumns[singleValueId] ?: "", onValueChange = {
                        if (it.isEmpty()) {
                            option.singleValueColumns.remove(singleValueId)
                        } else {
                            option.singleValueColumns[singleValueId] = it
                        }
                    }, placeholder = {
                        if (!option.singleValueColumns.containsKey(singleValueId)) {
                            val attr = option.findAttributeByPattern(singleValue.columnSchema.value)
                            if (attr != null) {
                                Text(attr)
                            }
                        }
                    })
                    if (!option.singleValueColumns.containsKey(singleValueId)) {
                        Text("*")
                    }
                }
            }
        }
    }
}

@Composable
fun TimeAttributeConfig(option: SelectOptionConfig, configuration: ProjectConfiguration) {
    val reorderState = rememberReorderableLazyListState(
        onMove = { a, b -> option.reorderTimeAttributes(a.index, b.index) }
    )

    Card(
        backgroundColor = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("Zeitattribute")

            LazyColumn(
                state = reorderState.listState,
                modifier = Modifier.reorderable(reorderState),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(option.timeAttributes, { it }) { timeAttr ->
                    ReorderableItem(reorderState, key = timeAttr) {
                        TimeAttrCard(option, timeAttr, reorderState)
                    }
                }
            }
            Button(onClick = {
                option.addTimeAttribute()
            }) {
                Text("Add")
            }
        }
    }
}

@Composable
fun TimeAttrCard(option: SelectOptionConfig, timeAttr: TimeAttribute, reorderState: ReorderableLazyListState) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(4.dp),
        ) {
            Box(
                modifier = Modifier.detectReorder(reorderState).size(32.dp).background(MaterialTheme.colors.secondary)
            ) {
                Text("Drag")
            }
            IconField(timeAttr.icon)
            TextField(timeAttr.column.value, onValueChange = {
                timeAttr.column.value = it
            }, label = { Text("Spalte") })
            Button(onClick = {
                dropdownExpanded = true
            }) {
                Text(timeAttr.lineType.value.optionName)
            }
            DropdownMenu(dropdownExpanded, onDismissRequest = {
                dropdownExpanded = false
            }, focusable = true) {
                LineType.values().forEach {lineType ->
                    DropdownMenuItem(onClick = {
                        timeAttr.lineType.value = lineType
                    }) {
                        Text(lineType.optionName)
                    }
                }
            }
            Button(onClick = {
                option.removeTimeAttribute(timeAttr)
            }) {
                Text("X")
            }
        }
    }
}