package com.enonic.wem.api.content.datatype;


import com.enonic.wem.api.blob.BlobKey;

public class Blob
    extends BaseDataType
{
    Blob( int key )
    {
        super( key, JavaType.BLOB );
    }

    @Override
    public Object ensureTypeOfValue( final Object value )
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        else if ( value instanceof String )
        {
            return new BlobKey( (String) value );
        }
        else
        {
            throw new InconvertibleValueException( value, this );
        }
    }

    @Override
    public boolean hasCorrectType( final Object value )
    {
        return byte[].class.isInstance( value ) || BlobKey.class.isInstance( value ) || super.hasCorrectType( value );
    }
}
