import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.input

@JsExport
class EntryForm: RComponent<EntryFormProps, RState>() {
    override fun RBuilder.render() {
        input {
            attrs {
                type = InputType.file
                onChangeFunction = {
                    setState {
                        props.onSelectImage(it)
                    }
                }
            }
        }
    }
}

external interface EntryFormProps: RProps {
    var onSelectImage: (Event) -> Unit
    var userName: String
}

fun RBuilder.entryForm(handler: EntryFormProps.() -> Unit): ReactElement {
    return child(EntryForm::class) {
        this.attrs(handler)
    }
}