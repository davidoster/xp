package com.enonic.wem.core.content

import com.enonic.wem.api.content.Content
import com.enonic.wem.api.content.type.ContentType
import com.enonic.wem.api.content.type.component.BreaksRequiredContractException
import com.enonic.wem.api.content.type.component.ComponentSet
import com.enonic.wem.api.content.type.component.FieldSet
import com.enonic.wem.api.content.type.component.inputtype.InputTypes

import static com.enonic.wem.api.content.type.component.Input.newInput

class ContentSpecTest extends spock.lang.Specification
{
    def "Given required Field not given Data then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType );

        when: "checking if the content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required FieldSet not given Data in any of it's fields then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        ComponentSet fieldSet = ComponentSet.newComponentSet().name( "myFieldSet" ).required( true ).build();
        fieldSet.addInput( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( false ).build() )
        contentType.addComponent( fieldSet );
        Content content = new Content();
        content.setType( contentType );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required FieldSet with a Data containing an empty value in it's one and only required text Field then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        ComponentSet fieldSet = ComponentSet.newComponentSet().name( "myFieldSet" ).required( true ).build();
        fieldSet.addInput( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() )
        contentType.addComponent( fieldSet );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFieldSet.myField", "" );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required Field within a FieldSet and the field has a Data containing and empty value then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        ComponentSet fieldSet = ComponentSet.newComponentSet().name( "myFieldSet" ).required( false ).build();
        fieldSet.addInput( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() )
        contentType.addComponent( fieldSet );
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFieldSet.myField", "" );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required Field within a FieldSet and the field has no Data then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        ComponentSet fieldSet = ComponentSet.newComponentSet().name( "myFieldSet" ).required( false ).build();
        fieldSet.addInput( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() )
        contentType.addComponent( fieldSet );
        Content content = new Content();
        content.setType( contentType );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

    def "Given a required Field within a VisualFieldSet and the field has no Data then checkBreaksRequiredContract should throw exception"( )
    {
        given:
        ContentType contentType = new ContentType();
        FieldSet visualFieldSet = FieldSet.newFieldSet().name( "myVisualFieldSet" ).label( "My VisualFieldSet" ).build();
        visualFieldSet.addComponent( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() )
        contentType.addComponent( visualFieldSet );
        Content content = new Content();
        content.setType( contentType );

        when: "checking if the Content breaks the required contract"
        content.checkBreaksRequiredContract();

        then: "exception is thrown"
        thrown( BreaksRequiredContractException )
    }

}
