package com.enonic.wem.api.content.type;

import org.joda.time.DateMidnight;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.component.BreaksRegexValidationException;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.InvalidDataException;
import com.enonic.wem.api.content.type.component.InvalidValueException;
import com.enonic.wem.api.content.type.component.inputtype.HtmlAreaConfig;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.content.type.component.inputtype.SingleSelectorConfig;

import static com.enonic.wem.api.content.type.Validator.newValidator;
import static com.enonic.wem.api.content.type.component.Input.newInput;
import static org.junit.Assert.*;


public class ValidatorTest
{

    @Test
    public void given_invalid_content_and_validator_that_recordExceptions_when_validate_then_no_exception_is_thrown()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).build();

        ContentType contentType = new ContentType();
        contentType.addComponent(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "mySingleSelector", "nonExistingOption" );

        // exercise & verify
        Validator validator = newValidator().contentType( contentType ).recordExceptions( true ).build();
        try
        {
            validator.validate( content.getData() );
        }
        catch ( Throwable e )
        {
            fail( "Validator is not supposed to throw any exception" );
        }
    }

    @Test
    public void given_invalid_content_and_validator_that_recordExceptions_when_validate_then_getInvalidDataExceptions_returns_exceptions()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeRadio().addOption( "Option 1", "o1" ).build();

        ContentType contentType = new ContentType();
        contentType.addComponent(
            newInput().name( "mySingleSelector1" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        contentType.addComponent(
            newInput().name( "mySingleSelector2" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "mySingleSelector1", "nonExistingOption" );
        content.setData( "mySingleSelector2", "nonExistingOption" );

        // exercise & verify
        Validator validator = newValidator().contentType( contentType ).recordExceptions( true ).build();
        try
        {
            validator.validate( content.getData() );
        }
        catch ( Throwable e )
        {
            fail( "Validator is not supposed to throw any exception" );
        }
        assertEquals( 2, validator.getInvalidDataExceptions().size() );
    }

    @Test
    public void given_valid_content_with_all_inputs_when_validate_then_no_exception_is_thrown()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).build();
        HtmlAreaConfig htmlAreaConfig = HtmlAreaConfig.newHtmlAreaConfig().build();

        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() );
        contentType.addComponent( newInput().name( "myDate" ).type( InputTypes.DATE ).build() );
        contentType.addComponent( newInput().name( "myDecimalNumber" ).type( InputTypes.DECIMAL_NUMBER ).build() );
        contentType.addComponent(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        contentType.addComponent( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );
        contentType.addComponent( newInput().name( "myHtmlArea" ).type( InputTypes.HTML_AREA ).inputTypeConfig( htmlAreaConfig ).build() );
        contentType.addComponent( newInput().name( "myPhone" ).type( InputTypes.PHONE ).build() );
        contentType.addComponent( newInput().name( "myTextArea" ).type( InputTypes.TEXT_AREA ).build() );
        contentType.addComponent( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.addComponent( newInput().name( "myWholeNumber" ).type( InputTypes.WHOLE_NUMBER ).build() );
        contentType.addComponent( newInput().name( "myXml" ).type( InputTypes.XML ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myColor.red", 0l );
        content.setData( "myColor.blue", 0l );
        content.setData( "myColor.green", 0l );
        content.setData( "myDate", new DateMidnight( 2012, 9, 11 ) );
        content.setData( "myDecimalNumber", 12.34 );
        content.setData( "mySingleSelector", "o1" );
        content.setData( "myGeoLocation.latitude", 0.0 );
        content.setData( "myGeoLocation.longitude", 0.0 );
        content.setData( "myHtmlArea", "<h1>Hello world</h1>" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validator.validate( content.getData() );
    }

    @Test
    public void given_invalid_data_according_to_inputs_validationRegex_when_validate_then_exception()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTextLine", "aax" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).checkValidationRegexp( true ).build();
        validateAndAssertInvalidDataException( validator, content, BreaksRegexValidationException.class, content.getData( "myTextLine" ) );
    }

    @Test
    public void given_content_with_invalid_dataSet_according_to_input_inside_a_componentSet_when_validate_then_exception_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        ComponentSet componentSet = ComponentSet.newComponentSet().name( "myComponentSet" ).build();
        contentType.addComponent( componentSet );
        componentSet.add( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myComponentSet.myColor.red", 0l );
        content.setData( "myComponentSet.myColor.green", 0l );
        content.setData( "myComponentSet.myColor.blue", -1l );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class,
                                               content.getData( "myComponentSet.myColor" ) );

    }

    @Test
    public void given_content_with_invalid_dataSet_according_to_input_inside_a_layout_when_validate_then_exception_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        FieldSet layout = FieldSet.newFieldSet().name( "myComponentSet" ).label( "Label" ).build();
        contentType.addComponent( layout );
        layout.addComponent( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myColor.red", 0l );
        content.setData( "myColor.green", 0l );
        content.setData( "myColor.blue", -1l );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "myColor" ) );

    }

    @Test
    public void given_content_with_invalid_dataSet_according_to_dataType_inside_a_componentSet_when_validate_then_exception_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        ComponentSet componentSet = ComponentSet.newComponentSet().name( "myComponentSet" ).build();
        contentType.addComponent( componentSet );
        componentSet.add( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myComponentSet.myGeoLocation.latitude", 0.0 );
        content.setData( "myComponentSet.myGeoLocation.longitude", -181.00 );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class,
                                               content.getData( "myComponentSet.myGeoLocation" ) );
    }

    @Test
    public void given_content_with_invalid_data_according_to_dataType_inside_a_componentSet_when_validate_then_exception_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        ComponentSet componentSet = ComponentSet.newComponentSet().name( "myComponentSet" ).build();
        contentType.addComponent( componentSet );
        componentSet.add( newInput().name( "myDate" ).type( InputTypes.DATE ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myComponentSet.myDate", "2000-01-01" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueTypeException.class,
                                               content.getData( "myComponentSet.myDate" ) );
    }

    @Test
    public void given_nonExistingValue_for_radio_button_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).build();

        ContentType contentType = new ContentType();

        contentType.addComponent(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "mySingleSelector", "nonExistingOption" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "mySingleSelector" ) );
    }

    @Test
    public void given_illegal_type_for_longitude_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myGeoLocation.latitude", 0.0 );
        content.setData( "myGeoLocation.longitude", "0.0" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueTypeException.class, content.getData( "myGeoLocation" ) );
    }

    @Test
    public void given_illegal_value_for_a_dataSet_defined_by_a_dataType_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup:
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myGeoLocation.latitude", 0.0 );
        content.setData( "myGeoLocation.longitude", -181.00 );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "myGeoLocation" ) );
    }

    @Test
    public void given_untyped_content_when_setting_type_that_fits_then_everything_validates()
    {
        // setup
        Content content = new Content();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getDataType() );
        Assert.assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        Assert.assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );

        // exercise
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        ComponentSet personalia = ComponentSet.newComponentSet().name( "personalia" ).multiple( false ).build();
        contentType.addComponent( personalia );
        personalia.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        personalia.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        ComponentSet crimes = ComponentSet.newComponentSet().name( "crimes" ).multiple( true ).build();
        contentType.addComponent( crimes );
        crimes.addInput( newInput().name( "description" ).type( InputTypes.TEXT_LINE ).build() );
        crimes.addInput( newInput().name( "year" ).type( InputTypes.TEXT_LINE ).build() );
        content.setType( contentType );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getDataType() );
        Assert.assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );

        // verify
        Validator validator = newValidator().contentType( contentType ).build();
        validator.validate( content.getData() );
    }

    private void validateAndAssertInvalidDataException( Validator validator, Content content, Class cause, Data data )
    {
        try
        {
            validator.validate( content.getData() );
            fail( "Expected " + InvalidDataException.class.getSimpleName() );
        }
        catch ( Exception e )
        {
            assertTrue( "Expected exception" + InvalidDataException.class.getSimpleName(), InvalidDataException.class.isInstance( e ) );
            assertTrue( "Expected cause" + cause.getSimpleName(), cause.isInstance( e.getCause() ) );
            InvalidDataException invalidDataException = (InvalidDataException) e;
            assertEquals( data.getPath(), invalidDataException.getData().getPath() );
        }
    }
}
