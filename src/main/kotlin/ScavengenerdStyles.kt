import kotlinx.css.*
import styled.StyleSheet

object ScavengenerdStyles : StyleSheet("ScavengenerdStyles", isStatic = true) {

    val pushButton by css {
        display = Display.inlineBlock
        border = "2px solid #AAA"
        backgroundColor = Color.lightGray
        padding = ".2vh"
    }

    val goAwayThing by css {
        position = Position.absolute
        top = -1000.px
    }
}
