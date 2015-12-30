module app.view {

    import RenderingMode = api.rendering.RenderingMode;
    import ContentImageUrlResolver = api.content.ContentImageUrlResolver;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class ContentItemPreviewPanel extends api.app.view.ItemPreviewPanel {

        private image: api.dom.ImgEl;
        private item: ViewItem<ContentSummaryAndCompareStatus>;

        constructor() {
            super("content-item-preview-panel");
            this.image = new api.dom.ImgEl();
            this.image.onLoaded((event: UIEvent) => {
                this.mask.hide();
                var imgEl = this.image.getEl();
                var myEl = this.getEl();
                this.centerImage(imgEl.getWidth(), imgEl.getHeight(), myEl.getWidth(), myEl.getHeight());
            });

            this.image.onError((event: UIEvent) => {
                this.setNoPreview();
            });

            this.appendChild(this.image);

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                if (this.hasClass("image-preview")) {
                    var imgEl = this.image.getEl(),
                        el = this.getEl();
                    this.centerImage(imgEl.getWidth(), imgEl.getHeight(), el.getWidth(), el.getHeight());
                }
            });

            this.onShown((event) => {
                if (this.item && this.hasClass("image-preview")) {
                    this.addImageSizeToUrl(this.item);
                }
            });

            this.frame.onLoaded((event: UIEvent) => {
                var frameWindow = this.frame.getHTMLElement()["contentWindow"];

                try {
                    if (frameWindow) {
                        var pathname: string = frameWindow.location.pathname;
                        if (pathname && pathname !== 'blank') {
                            new ContentPreviewPathChangedEvent(pathname).fire();
                        }
                    }
                } catch (reason) {}

            });
        }

        private centerImage(imgWidth, imgHeight, myWidth, myHeight) {
            var imgMarginTop = 0;
            if (imgHeight < myHeight) {
                // image should be centered vertically
                imgMarginTop = (myHeight - imgHeight) / 2;
            }
            this.image.getEl().setMarginTop(imgMarginTop + "px");

        }

        public addImageSizeToUrl(item: ViewItem<ContentSummaryAndCompareStatus>) {
            var imgSize = Math.max(this.getEl().getWidth(), this.getEl().getHeight());
            var imgUrl = new ContentImageUrlResolver().
                setContentId(item.getModel().getContentId()).
                setTimestamp(item.getModel().getContentSummary().getModifiedTime()).
                setSize(imgSize).resolve();
            this.image.setSrc(imgUrl);
        }

        public setItem(item: ViewItem<ContentSummaryAndCompareStatus>) {
            if (item && !item.equals(this.item)) {
                if (typeof item.isRenderable() === "undefined") {
                    return;
                }
                if (item.getModel().getContentSummary().getType().isImage()) {
                    this.getEl().removeClass("no-preview page-preview").addClass("image-preview");
                    if (this.isVisible()) {
                        this.addImageSizeToUrl(item);
                    }
                    if (!this.image.isLoaded()) {
                        this.showMask();
                    }
                } else {
                    this.showMask();
                    if (item.isRenderable()) {
                        this.getEl().removeClass("image-preview no-preview").addClass('page-preview');
                        var src = api.rendering.UriHelper.getPortalUri(item.getPath(), RenderingMode.PREVIEW,
                            api.content.Branch.DRAFT);
                        if (!this.frame.isSrcAlreadyShown(src)) {
                            this.frame.setSrc(src);
                        } else {
                            this.mask.hide();
                        }
                    } else {
                        this.setNoPreview();
                    }
                }
            }
            this.item = item;
        }

        public getItem(): ViewItem<ContentSummaryAndCompareStatus> {
            return this.item;
        }

        private setNoPreview() {
            this.getEl().removeClass("image-preview page-preview").addClass('no-preview');
            this.frame.setSrc("about:blank");
            this.mask.hide();
        }

        private showMask() {
            if (this.isVisible()) {
                this.mask.show();
            }
        }

    }
}
