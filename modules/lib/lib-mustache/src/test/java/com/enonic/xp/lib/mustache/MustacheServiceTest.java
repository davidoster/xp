package com.enonic.xp.lib.mustache;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptBeanTestSupport2;

public class MustacheServiceTest
    extends ScriptBeanTestSupport2
{
    private MustacheService service;

    @Override
    protected void initialize()
    {
        super.initialize();
        this.service = new MustacheService();
        this.service.initialize( newBeanContext( ResourceKey.from( "myapp:/site" ) ) );
    }

    @Test
    public void testProcess()
    {
        final MustacheProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapp:/site/test/view/test.html" ) );
        processor.setModel( null );
        processor.process();
    }
}
