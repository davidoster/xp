package com.enonic.xp.lib.websocket;

import com.enonic.xp.portal.websocket.WebSocketManager;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public abstract class AbstractWebSocketBean
    implements ScriptBean
{
    protected WebSocketManager webSocketManager;

    @Override
    public void initialize( final BeanContext context )
    {
        this.webSocketManager = context.getService( WebSocketManager.class ).get();
    }
}
