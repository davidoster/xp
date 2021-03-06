package com.enonic.xp.admin.impl.json.content;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;

public class ContentWorkflowInfoJson
{
    private WorkflowState state;

    private Map<String, WorkflowCheckState> checks;

    @JsonCreator
    public ContentWorkflowInfoJson( @JsonProperty("state") WorkflowState state,
                                    @JsonProperty("checks") Map<String, WorkflowCheckState> checks )
    {
        this.state = state;
        this.checks = checks;
    }

    public ContentWorkflowInfoJson( final WorkflowInfo workflowInfo )
    {
        this.state = workflowInfo.getState();
        this.checks = workflowInfo.getChecks();
    }

    public WorkflowState getState()
    {
        return state;
    }

    @SuppressWarnings("unused")
    public Map<String, WorkflowCheckState> getChecks()
    {
        return checks;
    }

    @JsonIgnore
    public WorkflowInfo getWorkflowInfo()
    {
        return WorkflowInfo.create().
            state( state ).
            checks( checks ).
            build();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ContentWorkflowInfoJson that = (ContentWorkflowInfoJson) o;
        return state == that.state && Objects.equals( checks, that.checks );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( state, checks );
    }
}
