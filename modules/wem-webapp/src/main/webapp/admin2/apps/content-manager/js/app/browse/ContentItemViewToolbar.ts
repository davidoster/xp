module app_browse {

    export interface ContentItemViewToolbarParams {
        editAction: api_ui.Action;
        deleteAction: api_ui.Action;
        closeAction: api_ui.Action;
    }

    export class ContentItemViewToolbar extends api_ui_toolbar.Toolbar {

        constructor(params:ContentItemViewToolbarParams) {
            super();
            super.addAction(params.editAction);
            super.addAction(params.deleteAction);
            super.addGreedySpacer();
            super.addAction(params.closeAction)

        }
    }
}
