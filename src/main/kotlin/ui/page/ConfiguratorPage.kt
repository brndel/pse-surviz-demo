package ui.page

import Project
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import ui.page.configurator.ExportPage
import ui.page.configurator.OptionPage
import ui.page.configurator.SingleValuePage

@Composable
fun ConfiguratorPage(project: Project) {
    val selectedTab = remember { mutableStateOf(Tab.SingleValue) }

    val tab by selectedTab;

    Row {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.width(200.dp)
        ) {
            TabButton(selectedTab, Tab.SingleValue, this)
            TabButton(selectedTab, Tab.Option, this)
            TabButton(selectedTab, Tab.Export, this)
        }

        TabContent(tab, project, modifier = Modifier.weight(1.0F))
        PrevievWindow(project, Modifier.weight(1.0F))
    }
}

@Composable
fun TabButton(selectedTab: MutableState<Tab>, tab: Tab, columnScope: ColumnScope) {
    Button(
        onClick = {
            selectedTab.value = tab
        },
        colors = ButtonDefaults.buttonColors(if (selectedTab.value == tab) MaterialTheme.colors.primary else MaterialTheme.colors.surface),
        modifier = columnScope.run { Modifier.weight(1.0F) }.fillMaxWidth(),
        elevation = null
    ) {
        Text(tab.getName())
    }
}


@Composable
fun TabContent(tab: Tab, project: Project, modifier: Modifier) {
    when (tab) {
        Tab.SingleValue -> SingleValuePage(project.configuration, modifier)
        Tab.Option -> OptionPage(project.configuration, modifier)
        Tab.Export -> ExportPage(project, modifier)
    }
}

enum class Tab {
    SingleValue,
    Option,
    Export;

    fun getName(): String {
        return when (this) {
            SingleValue -> "Einzelwerte"
            Option -> "Verkehrsmittel"
            Export -> "Export"
        }
    }
}