package ui.page.configurator

import ProjectConfiguration
import SingleValueAttribute
import SingleValueIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.*
import ui.component.FloatField
import ui.component.IconField

@Composable
fun SingleValuePage(configuration: ProjectConfiguration, modifier: Modifier) {
    val reorderState = rememberReorderableLazyListState(
        onMove = { a, b -> configuration.reorderSingleValues(a.index, b.index) }
    )

    Column(modifier) {
        LazyColumn(
            state = reorderState.listState,
            modifier = Modifier.reorderable(reorderState).weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(configuration.singleValueIds, { it }) { id ->
                ReorderableItem(reorderState, key = id) {
                    SingleValueCard(configuration.singleValueAttributes[id]!!, onRemove = {
                        configuration.removeSingleValue(id)
                    }, reorderState)
                }
            }
        }
        Button(onClick = {
            configuration.addSingleValue()
        }) {
            Text("add")
        }
    }
}

@Composable
fun <T> SingleValueCard(attribute: SingleValueAttribute, onRemove: () -> Unit, reorderableState: ReorderableState<T>) {
    var unit by attribute.unit;
    var columnSchema by attribute.columnSchema;
    Card(
    ) {
        Row(
            Modifier.padding(4.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.detectReorder(reorderableState).size(32.dp).background(MaterialTheme.colors.secondary)
            ) {
                Text("Drag")
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1.0F)
            ) {
                TextField(
                    unit, { unit = it },
                    label = { Text("Einheit") },
                    singleLine = true
                )
                TextField(
                    columnSchema,
                    { columnSchema = it },
                    label = { Text("Spalte") },
                    singleLine = true
                )
                ValueIconCard(attribute.icon)
            }
            Button(onClick = onRemove) {
                Text("X")
            }
        }
    }
}

@Composable
fun ValueIconCard(icon: SingleValueIcon) {
    Card(
        backgroundColor = MaterialTheme.colors.background
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconField(icon.baseIcon)
            }
            for (iconLevel in icon.iconLevels) {
                Divider(Modifier.width(64.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconField(iconLevel.icon)
                    FloatField(
                        iconLevel.threshold.value,
                        onValueChange = { iconLevel.threshold.value = it },

                        label = {
                            Text("Untergrenze")
                        })
                    Button(onClick = {
                        icon.removeLevel(iconLevel)
                    }) {
                        Text("X")
                    }
                }
            }
            Button(onClick = {
                icon.addLevel()
            }) {
                Text("Add")
            }
        }
    }
}
