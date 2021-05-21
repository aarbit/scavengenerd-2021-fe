import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.keyGen
import kotlinx.html.pre
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
                        updateEntryStatus("SUBMITTED")
                    }
                }
                +"Submit"
            }
            styledButton {
                attrs {
                    onClickFunction = {
                        updateEntryStatus("APPROVED")
                    }
                }
                +"Approve"
            }
            styledButton {
                attrs {
                    onClickFunction = {
                        for(entryId in state.checkedPhotos) {
                            val mainScope = MainScope()
                            mainScope.launch {
                                val isOK = window.fetch(
                                    "${rootUrl}/entry/${entryId}",
                                    RequestInit(method = "DELETE")
                                )
                                    .await()
                                    .ok
                                if(isOK) {
                                    val entryIndex = props.entries.indexOfFirst { it.id == entryId }
                                    setState {
                                        props.entries.removeAt(entryIndex)
                                    }
                                }
                            }
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

    private fun updateEntryStatus(status: String) {
        for(entryId in state.checkedPhotos) {
            val formData = FormData()
            formData.append("status", status)
            val mainScope = MainScope()
            mainScope.launch {
                val isOK = window.fetch(
                    "${rootUrl}/entry/${entryId}",
                    RequestInit(method = "PATCH", body = formData)
                )
                    .await()
                    .ok
                if(isOK) {
                    val entryIndex = props.entries.indexOfFirst { it.id == entryId }
                    val entry = props.entries.get(entryIndex)
                    entry.status = status
                    setState {
                        props.entries.set(entryIndex, entry)
                    }
                }
            }
        }
    }
}

external interface EntryDetailsProps: RProps {
    var itemId: Int
    var entries: MutableList<ItemEntryDetail>
}

external interface EntryDetailsState: RState {
    var checkedPhotos: MutableList<Int>
}

fun RBuilder.entryDetails(handler: EntryDetailsProps.() -> Unit): ReactElement {
    return child(EntryDetails::class) {
        this.attrs(handler)
    }
}