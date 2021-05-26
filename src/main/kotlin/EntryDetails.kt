import kotlinx.browser.window
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*
import styled.styledButton

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
            styledButton {
                attrs {
                    onClickFunction = {
                        props.onSubmit()
                    }
                }
                +"Submit"
            }
            styledButton {
                attrs {
                    onClickFunction = {
                        props.onApprove()
                    }
                }
                +"Approve"
            }
            styledButton {
                attrs {
                    onClickFunction = {
                        props.onDelete()
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
                    input {
                        attrs {
                            type = InputType.checkBox
                            key = entry.id.toString()
                            onChangeFunction = {
                                props.onCheckPhoto((it.target as HTMLInputElement), entry)

                            }
                        }
                    }
                    span {
                        +entry.status
                    }
                    +" "
                    span {
                        +entry.userName
                    }
                    div {
                        img {
                            attrs {
                                src = "data:image/jpeg;base64,${entry.photo}"
                                width = "25%"
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