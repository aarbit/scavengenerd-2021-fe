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

external interface EntryDetailsProps: RProps {
    var item: ItemDetail
    var onSubmit: () -> Unit
}

external interface EntryDetailsState: RState {
    var checkedPhotos: MutableList<Int>
}

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
                                    val entryIndex = props.item.entries.indexOfFirst { it.id == entryId }
                                    props.item.entries = props.item.entries.removeAt(entryIndex)
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
            for (entry in props.item.entries) {
                div {
                    key = entry.id.toString()
                    input {
                        attrs {
                            type = InputType.checkBox
                            key = entry.id.toString()
                            onChangeFunction = {
                                setState {
                                    if ((it.target as HTMLInputElement).checked) {
                                        checkedPhotos.add(entry.id)
                                    }
                                    else {
                                        checkedPhotos.remove(entry.id)
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
                    val entryIndex = props.item.entries.indexOfFirst { it.id == entryId }
                    val entry = props.item.entries.get(entryIndex)
                    entry.status = status
                    props.item.entries[entryIndex] = entry
                }
            }
        }
    }
}

fun <T: Any> Array<T>.removeAt(index: Int): Array<T> {
    val list = mutableListOf<T>()
    for (i in 0 until this.size) {
        if(i != index) {
            list.add(this[i])
        }
    }
    return list.toTypedArray()
}

fun RBuilder.entryDetails(handler: EntryDetailsProps.() -> Unit): ReactElement {
    return child(EntryDetails::class) {
        this.attrs(handler)
    }
}