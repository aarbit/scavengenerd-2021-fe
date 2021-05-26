import kotlinx.css.*
import styled.StyleSheet

object ScavengenerdStyles : StyleSheet("ScavengenerdStyles", isStatic = true) {

    val pushButton by css {
        display = Display.inlineBlock
        border = "2px solid #555"
        backgroundColor = Color.lightGray
        padding = ".4vh"
        margin = "1vh"
        borderRadius = 1.vh
    }

    val goAwayThing by css {
        position = Position.absolute
        top = -1000.px
    }

    val smallerText by css {
        fontSize = 1.vh
    }

    val checkBox by css {
        appearance = Appearance.none
        width = 3.vh
        height = 3.vh
        backgroundColor = Color.white
        borderRadius = 1.vh
        border = "2px solid #555"
        checked {
            backgroundColor = Color.grey
        }
    }
}
