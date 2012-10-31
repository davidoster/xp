package com.enonic.wem.api.content.type;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;
import gherkin.formatter.model.DataTableRow;

import com.enonic.wem.api.content.type.component.ComponentPath;
import com.enonic.wem.api.content.type.component.ComponentType;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.InputSubType;
import com.enonic.wem.api.content.type.component.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.component.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.component.SubTypeReference;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.component.Input.newInput;
import static com.enonic.wem.api.content.type.component.InputSubType.newInputSubType;

public class ContentTypeStepDefs
{
    public final MockSubTypeFetcher mockSubTypeFetcher = new MockSubTypeFetcher();

    public final Map<String, Module> moduleByName = new HashMap<String, Module>();

    public final Map<String, ContentType> contentTypeByName = new HashMap<String, ContentType>();

    public final Map<String, Input> inputByName = new HashMap<String, Input>();

    public final Map<SubTypeQualifiedName, InputSubType> inputSubTypeByQualifiedName = new HashMap<SubTypeQualifiedName, InputSubType>();


    @Given("^a Module named (.+)$")
    public void a_module_named_name( String name )
        throws Throwable
    {
        Module module = Module.newModule().name( name ).build();
        moduleByName.put( name, module );
    }

    @Given("^a Input named (.+) of type (.+)$")
    public void a_input_named_name_of_type_type( String inputName, String inputTypeName )
        throws Throwable
    {
        Input input = newInput().name( inputName ).type( InputTypes.parse( inputTypeName ) ).build();
        inputByName.put( inputName, input );
    }

    @Given("^a InputSubType named (.+) in module (.+) with input (.+)$")
    public void a_inputSubType_named_name_in_module_module_having_input( String subTypeName, String moduleName, String inputName )
        throws Throwable
    {
        InputSubType inputSubType =
            newInputSubType().module( moduleByName.get( moduleName ) ).input( inputByName.get( inputName ) ).build();
        inputSubTypeByQualifiedName.put( new SubTypeQualifiedName( moduleName, subTypeName ), inputSubType );
    }

    @Given("^a ContentType named (.+)")
    public void a_ContentType_named_name( String contentTypeName )
        throws Throwable
    {
        ContentType contentType = new ContentType();
        contentType.setName( contentTypeName );
        contentTypeByName.put( contentTypeName, contentType );
    }

    @Given("^adding SubTypeReference named (.+) referencing InputSubType (.+) to ContentType (.+)$")
    public void adding_SubTypeReference_named_name_referencing_InputSubType_name_to_ContentType_myContentType( String subTypeReferenceName,
                                                                                                               String subTypeQualifiedName,
                                                                                                               String contentTypeName )
        throws Throwable
    {

        InputSubType inputSubType = inputSubTypeByQualifiedName.get( new SubTypeQualifiedName( subTypeQualifiedName ) );
        ContentType contentType = contentTypeByName.get( contentTypeName );
        contentType.addComponent( SubTypeReference.newSubTypeReference( inputSubType ).name( subTypeReferenceName ).build() );

        mockSubTypeFetcher.add( inputSubType );
        contentType.subTypeReferencesToComponents( mockSubTypeFetcher );
    }

    @When("^translating subType references to components for all content types$")
    public void translating_subType_references_to_components_for_all_content_types()
        throws Throwable
    {
        for ( ContentType contentType : contentTypeByName.values() )
        {
            contentType.subTypeReferencesToComponents( mockSubTypeFetcher );
        }
    }

    @Then("^the following Components should exist in the following ContentTypes:$")
    public void the_following_Components_should_exist_in_the_following_ContentTypes( DataTable dataTable )
        throws Throwable
    {

        List<DataTableRow> list = dataTable.getGherkinRows();
        for ( DataTableRow row : list )
        {
            String contentTypeName = row.getCells().get( 0 );
            String componentPath = row.getCells().get( 1 );
            ComponentType componentType = ComponentType.valueOf( row.getCells().get( 2 ) );
            ContentType contentType = contentTypeByName.get( contentTypeName );
            Assert.assertNotNull( "component not found at path: " + componentPath,
                                  contentType.getComponents().getHierarchicalComponent( new ComponentPath( componentPath ) ) );
            Assert.assertEquals( ComponentType.INPUT, componentType );
        }
    }

}
