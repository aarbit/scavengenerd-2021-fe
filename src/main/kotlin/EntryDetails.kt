import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.div
import react.dom.form
import react.dom.img
import react.dom.key
import styled.*

external interface EntryDetailsProps: RProps {
    var item: ItemDetail
    var onCheckPhoto: (HTMLInputElement, ItemEntryDetail) -> Unit
    var onSubmit: () -> Unit
    var onApprove: () -> Unit
    var onDelete: () -> Unit
}

@JsExport
class EntryDetails: RComponent<EntryDetailsProps, RState>() {
    override fun RBuilder.render() {
        div {
            styledLabel {
                css(ScavengenerdStyles.pushButton)
                styledButton {
                    css(ScavengenerdStyles.goAwayThing)
                    attrs {
                        onClickFunction = {
                            props.onSubmit()
                        }
                    }
                }
                +"Submit"
            }
            styledLabel {
                css(ScavengenerdStyles.pushButton)
                styledButton {
                    css(ScavengenerdStyles.goAwayThing)
                    attrs {
                        onClickFunction = {
                            props.onApprove()
                        }
                    }
                }
                +"Approve"
            }
            styledLabel {
                css(ScavengenerdStyles.pushButton)
                styledButton {
                    css(ScavengenerdStyles.goAwayThing)
                    attrs {
                        onClickFunction = {
                            props.onDelete()
                        }
                    }
                }
                +"Delete"
            }

        }
        form {
            attrs {
                name = ""
            }
            for (entry in props.item.entries) {
                div {
                    key = entry.id.toString()
                    styledInput {
                        css(ScavengenerdStyles.checkBox)
                        attrs {
                            type = InputType.checkBox
                            key = entry.id.toString()
                            onChangeFunction = {
                                props.onCheckPhoto((it.target as HTMLInputElement), entry)

                            }
                        }
                    }
                    styledSpan {
                        css(ScavengenerdStyles.smallerText)
                        +entry.status
                    }
                    +" "
                    styledSpan {
                        css(ScavengenerdStyles.smallerText)
                        +entry.userName
                    }
                    div {
                        img {
                            attrs {
                                src = "data:image/jpeg;base64,${entry.photo}"
                                width = "75%"
                            }
                        }

                    }
                }
            }
        }
    }
}

fun RBuilder.entryDetails(handler: EntryDetailsProps.() -> Unit): ReactElement {
    return child(EntryDetails::class) {
        this.attrs(handler)
    }
}