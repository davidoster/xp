package com.enonic.wem.api.command.module;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleKey;

public final class UpdateModule
    extends Command<Boolean>
{
    private ModuleKey moduleKey;

    private ModuleEditor editor;

    public UpdateModule module( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
        return this;
    }

    public UpdateModule editor( final ModuleEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    public ModuleEditor getEditor()
    {
        return editor;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.moduleKey, "module key cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
