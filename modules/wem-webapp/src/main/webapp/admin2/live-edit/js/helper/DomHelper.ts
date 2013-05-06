interface DocumentSize {
    width: number;
    height: number;
}

interface ViewPortSize {
    width: number;
    height: number;
}


module liveedit {
    export class DomHelper {

        static $ = $liveedit;

        public static getDocumentSize():DocumentSize {
            var $document = $(document);
            return {
                width: $document.width(),
                height: $document.height()
            };
        }


        public static getViewPortSize():ViewPortSize {
            var $window = $(window);
            return {
                width: $window.width(),
                height: $window.height()
            };
        }


        public static getDocumentScrollTop():number {
            return $(document).scrollTop();
        }

    }
}