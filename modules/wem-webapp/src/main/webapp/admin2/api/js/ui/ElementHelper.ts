module api_ui {

    export class ElementHelper {

        private el:HTMLElement;

        static fromName(name:string):api_ui.ElementHelper {
            return new api_ui.ElementHelper(document.createElement(name));
        }

        constructor(element:HTMLElement) {
            this.el = element;
        }

        getHTMLElement():HTMLElement {
            return this.el;
        }

        insertBefore(newEl:api_ui.Element, existingEl:api_ui.Element) {
            this.el.insertBefore(newEl.getHTMLElement(), existingEl.getHTMLElement());
        }

        setDisabled(value:bool):api_ui.ElementHelper {
            this.el.disabled = value;
            return this;
        }

        setId(value:string):api_ui.ElementHelper {
            this.el.id = value;
            return this;
        }

        setInnerHtml(value:string):api_ui.ElementHelper {
            this.el.innerHTML = value;
            return this;
        }

        setValue(value:string):api_ui.ElementHelper {
            this.el.setAttribute("value", value);
            return this;
        }

        addClass(clsName:string) {
            if (!this.hasClass(clsName)) {
                if (this.el.className === '') {
                    this.el.className += clsName;
                }
                else {
                    this.el.className += ' ' + clsName;
                }
            }
        }

        hasClass(clsName:string):bool {
            return this.el.className.match(new RegExp('(\\s|^)' + clsName + '(\\s|$)')) !== null;
        }

        removeClass(clsName:string) {
            if (this.hasClass(clsName)) {
                var reg = new RegExp('(\\s|^)' + clsName + '(\\s|$)');
                this.el.className = this.el.className.replace(reg, '');
            }
        }

        addEventListener(eventName:string, f:(event:Event) => any) {
            this.el.addEventListener(eventName, f);
        }

        removeEventListener(eventName:string, f:(event:Event) => any) {
            this.el.removeEventListener(eventName, f);
        }

        appendChild(child:HTMLElement) {
            this.el.appendChild(child);
        }

        setData(name:string, value:string):api_ui.ElementHelper {
            var any = <any>this.el;
            any._data[name] = value;
            return this;
        }

        getData(name:string):string {
            var any = <any>this.el;
            return any._data[name];
        }

        getDisplay():string {
            return this.el.style.display;
        }

        setDisplay(value:string):api_ui.ElementHelper {
            this.el.style.display = value;
            return this;
        }

        getVisibility() {
            return this.el.style.visibility;
        }

        setVisibility(value:string):api_ui.ElementHelper {
            this.el.style.visibility = value;
            return this;
        }

        setPosition(value:string):api_ui.ElementHelper {
            this.el.style.position = value;
            return this;
        }

        setWidth(value:string):api_ui.ElementHelper {
            this.el.style.width = value;
            return this;
        }

        getWidth(): number {
            return this.el.offsetWidth;
        }

        setHeight(value:string):api_ui.ElementHelper {
            this.el.style.height = value;
            return this;
        }

        getHeight(): number {
            return this.el.offsetHeight;
        }

        setTop(value:string):api_ui.ElementHelper {
            this.el.style.top = value;
            return this;
        }

        setLeft(value:string):api_ui.ElementHelper {
            this.el.style.left = value;
            return this;
        }

        setMarginLeft(value:string):api_ui.ElementHelper {
            this.el.style.marginLeft = value;
            return this;
        }

        setMarginRight(value:string):api_ui.ElementHelper {
            this.el.style.marginRight = value;
            return this;
        }

        setMarginTop(value:string):api_ui.ElementHelper {
            this.el.style.marginTop = value;
            return this;
        }

        setMarginBottom(value:string):api_ui.ElementHelper {
            this.el.style.marginBottom = value;
            return this;
        }

        setZindex(value:number):api_ui.ElementHelper {
            this.el.style.zIndex = value.toString();
            return this;
        }

        setBackgroundImage(value:string):api_ui.ElementHelper {
            this.el.style.backgroundImage = value;
            return this;
        }

        remove() {
            var parent = this.el.parentElement;
            parent.removeChild(this.el);
        }

        getOffset() {
            var el = this.el;
            var x = 0,
                y = 0;
            while (el && !isNaN(el.offsetLeft) && !isNaN(el.offsetTop)) {
                x += el.offsetLeft - el.scrollLeft;
                y += el.offsetTop - el.scrollTop;
                el = <HTMLElement> el.offsetParent;
            }
            return { top: y, left: x };
        }

    }
}
