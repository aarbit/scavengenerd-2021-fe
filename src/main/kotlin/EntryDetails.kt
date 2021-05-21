import kotlinx.browser.window
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
import react.*
import react.dom.*
import styled.styledButton

@JsExport
class EntryDetails: RComponent<EntryDetailsProps, EntryDetailsState>() {
    override fun RBuilder.render() {
        div {
            styledButton {
                attrs {
                    onClickFunction = {
                        for(entryId in state.checkedPhotos) {
                            val formData = FormData()
                            formData.append("status", "SUBMITTED")
                            window.fetch(
                                "http://localhost:8081/entry/${entryId}",
                                RequestInit(method = "PATCH", body = formData)
                            )
                        }
                    }
                }
                +"Submit"
            }
            styledButton {
                attrs {
                    onClickFunction = {
                        for(entryId in state.checkedPhotos) {
                            val formData = FormData()
                            formData.append("status", "APPROVED")
                            window.fetch(
                                "http://localhost:8081/entry/${entryId}",
                                RequestInit(method = "PATCH", body = formData)
                            )
                        }
                    }
                }
                +"Approve"
            }
            styledButton {
                attrs {
                    onClickFunction = {
                        for(entryId in state.checkedPhotos) {
                            window.fetch(
                                "http://localhost:8081/entry/${entryId}",
                                RequestInit(method = "DELETE")
                            )
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
            for (entry in props.entries) {
                div {
                    key = entry.id.toString()
                    input {
                        attrs {
                            type = InputType.checkBox
                            key = entry.id.toString()
                            onChangeFunction = {
                                setState {
                                    if ((it.target as HTMLInputElement).checked) {
                                        state.checkedPhotos.add(entry.id)
                                    }
                                    else {
                                        state.checkedPhotos.remove(entry.id)
                                    }
                                }
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
    override fun EntryDetailsState.init() {
        checkedPhotos = mutableListOf()
    }
}

external interface EntryDetailsProps: RProps {
    var itemId: Int
    var entries: List<ItemEntryDetail>
}

external interface EntryDetailsState: RState {
    var checkedPhotos: MutableList<Int>
}

fun RBuilder.entryDetails(handler: EntryDetailsProps.() -> Unit): ReactElement {
    return child(EntryDetails::class) {
        this.attrs(handler)
    }
}