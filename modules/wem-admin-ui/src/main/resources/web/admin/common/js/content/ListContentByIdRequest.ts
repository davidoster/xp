module api.content {

    export class ListContentByIdRequest extends ContentResourceRequest<ListContentResult<api.content.json.ContentSummaryJson>, ContentResponse<ContentSummary>> {

        private parentId:string;

        private expand:api.rest.Expand = api.rest.Expand.SUMMARY;

        private from: number;

        private size: number;

        constructor(parentId:string) {
            super();
            super.setMethod("GET");
            this.parentId = parentId;
        }

        setExpand(value:api.rest.Expand): ListContentByIdRequest {
            this.expand = value;
            return this;
        }

        setFrom(value: number): ListContentByIdRequest {
            this.from = value;
            return this;
        }

        setSize(value: number): ListContentByIdRequest {
            this.size = value;
            return this;
        }

        getParams(): Object {
            return {
                parentId: this.parentId,
                expand: this.expand,
                from: this.from,
                size: this.size
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }

        sendAndParse(): Q.Promise<ContentResponse<ContentSummary>> {

            return this.send().then((response:api.rest.JsonResponse<ListContentResult<api.content.json.ContentSummaryJson>>) => {
                return new ContentResponse(
                    ContentSummary.fromJsonArray(response.getResult().contents),
                    new ContentMetadata(response.getResult().metadata["hits"], response.getResult().metadata["totalHits"])
                );
            });
        }
    }
}