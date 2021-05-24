import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.input
import react.dom.p

external interface EntryFormProps: RProps {
    var onSelectImage: (HTMLInputElement) -> Unit
    var uploading: Boolean
}

@JsExport
class EntryForm: RComponent<EntryFormProps, RState>() {
    override fun RBuilder.render() {
        if(props.uploading) {
            p {
                +"UPLOADING!"
            }
        } else {
            input {
                attrs {
                    type = InputType.file
                    onChangeFunction = { event ->
                        props.onSelectImage(event.target as HTMLInputElement)
                    }
                }
            }
        }
    }
}

fun RBuilder.entryForm(handler: EntryFormProps.() -> Unit): ReactElement {
    return child(EntryForm::class) {
        this.attrs(handler)
    }
}