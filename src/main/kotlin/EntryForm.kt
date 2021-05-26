import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.p
import react.dom.span
import styled.css
import styled.styledInput
import styled.styledLabel

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
            styledLabel {
                css {
                    +ScavengenerdStyles.pushButton
                }
                styledInput {
                    css {
                        +ScavengenerdStyles.goAwayThing
                    }
                    attrs {
                        type = InputType.file
                        onChangeFunction = { event ->
                            props.onSelectImage(event.target as HTMLInputElement)
                        }
                        accept = "image/*"
                    }
                }
                span {
                    +"Add photo."
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