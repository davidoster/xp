module api.content.page.region {

    export class Region implements api.Equitable, api.Cloneable {

        private name: string;

        private pageComponents: api.content.page.PageComponent[] = [];

        private parent: api.content.page.layout.LayoutComponent;

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.parent = builder.parent;
            this.pageComponents = builder.pageComponents;
            this.pageComponents.forEach((pageComponent: PageComponent, index: number) => {
                pageComponent.setParent(this);
                pageComponent.setIndex(index);
            });
        }

        getName(): string {
            return this.name;
        }

        getParent(): api.content.page.layout.LayoutComponent {
            return this.parent;
        }

        getPath(): api.content.page.RegionPath {
            var parentPath = null;
            if (this.parent) {
                parentPath = this.parent.getPath();
            }
            return new api.content.page.RegionPath(parentPath, this.name);
        }

        ensureUniqueComponentName(wantedName: ComponentName): ComponentName {

            var numberOfDuplicates = this.countNumberOfDuplicates(wantedName);
            if (numberOfDuplicates == 0) {
                return wantedName;
            }

            var duplicateCounter = numberOfDuplicates + 1;
            var possibleNewName = wantedName.createDuplicate(duplicateCounter);
            while (this.hasComponentWithName(possibleNewName)) {
                possibleNewName = wantedName.createDuplicate(++duplicateCounter);
            }

            return possibleNewName;
        }

        private countNumberOfDuplicates(name: api.content.page.ComponentName): number {

            var count = 0;
            this.pageComponents.forEach((component: api.content.page.PageComponent)=> {
                if (component.getName().isDuplicateOf(name)) {
                    count++;
                }
            });
            return count;
        }

        duplicateComponent(source: PageComponent): PageComponent {

            var duplicateName = this.resolveNameOfDuplicatedComponent(source.getName());

            var duplicatedComponent = source.clone();
            duplicatedComponent.setName(duplicateName);
            this.addComponentAfter(duplicatedComponent, source);

            return duplicatedComponent;
        }

        private resolveNameOfDuplicatedComponent(nameOfSource: api.content.page.ComponentName): api.content.page.ComponentName {

            var nameWithoutCountPostFix = null;
            if (!nameOfSource.hasCountPostfix()) {
                nameWithoutCountPostFix = nameOfSource;
            }
            else {
                nameWithoutCountPostFix = nameOfSource.removeCountPostfix();
            }

            var count = this.countNumberOfDuplicates(nameWithoutCountPostFix);
            var possibleNewName = nameWithoutCountPostFix.createDuplicate(count + 1);

            while (this.hasComponentWithName(possibleNewName)) {
                possibleNewName = nameOfSource.createDuplicate(++count);
            }

            return possibleNewName;
        }

        addComponent(pageComponent: PageComponent) {
            this.pageComponents.push(pageComponent);
            pageComponent.setParent(this);
            pageComponent.setIndex(this.pageComponents.length - 1);
        }

        /*
         *  Add component after target component. Component will only be added if target component is found.
         */
        addComponentAfter(component: api.content.page.PageComponent, precedingComponent: PageComponent) {

            api.util.assert(!this.hasComponentWithName(component.getName()),
                    "Component already added to region [" + this.name + "]: " + component.getName().toString());


            var precedingIndex = -1;
            if (precedingComponent != null) {
                precedingIndex = precedingComponent.getIndex();
                if (precedingIndex == -1 && this.pageComponents.length > 1) {
                    return -1;
                }
            }

            component.setParent(this);

            var index = 0;
            if (precedingIndex > -1) {
                index = precedingIndex + 1;
            }
            this.pageComponents.splice(index, 0, component);

            // Update indexes
            this.pageComponents.forEach((curr: PageComponent, index: number) => {
                curr.setIndex(index);
            });
        }

        removeComponent(component: api.content.page.PageComponent): api.content.page.PageComponent {
            if (!component) {
                return null;
            }

            var componentIndex = component.getIndex();
            if (componentIndex == -1) {
                throw new Error("PageComponent [" + component.getPath().toString() + "] to remove does not exist in region: " +
                                this.getPath().toString());
            }
            this.pageComponents.splice(componentIndex, 1);

            // Update indexes
            this.pageComponents.forEach((curr: PageComponent, index: number) => {
                curr.setIndex(index);
            });
            return component;
        }

        hasComponentWithName(name: ComponentName) {
            return this.pageComponents.some((component: api.content.page.PageComponent) => {
                return component.getName().equals(name);
            });
        }

        getComponents(): api.content.page.PageComponent[] {
            return this.pageComponents;
        }

        getComponentByIndex(index: number): api.content.page.PageComponent {
            var pageComponent = this.pageComponents[index];
            api.util.assertState(pageComponent.getIndex() == index,
                    "Index of PageComponent is not as expected. Expected [" + index + "], was: " + pageComponent.getIndex());
            return  pageComponent;
        }

        getComponentByName(name: api.content.page.ComponentName): api.content.page.PageComponent {
            var found: api.content.page.PageComponent = null;
            this.pageComponents.forEach((pageComponent: api.content.page.PageComponent) => {
                if (pageComponent.getName().equals(name)) {
                    found = pageComponent;
                }
            });
            return found;
        }

        removePageComponents() {
            while (this.pageComponents.length > 0) {
                var pageComponent = this.pageComponents.pop();
                pageComponent.setParent(null);
                pageComponent.setIndex(-1);
            }
        }

        toJson(): RegionJson {

            var componentJsons: api.content.page.PageComponentTypeWrapperJson[] = [];

            this.pageComponents.forEach((component: api.content.page.PageComponent) => {
                componentJsons.push(component.toJson());
            });

            return {
                name: this.name,
                components: componentJsons
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Region)) {
                return false;
            }

            var other = <Region>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.pageComponents, other.pageComponents)) {
                return false;
            }

            return true;
        }

        clone(): Region {
            return new RegionBuilder(this).build();
        }
    }

    export class RegionBuilder {

        name: string;

        pageComponents: api.content.page.PageComponent[] = [];

        parent: api.content.page.layout.LayoutComponent;

        constructor(source?: Region) {
            if (source) {
                this.name = source.getName();
                this.parent = source.getParent();
                source.getComponents().forEach((component: api.content.page.PageComponent) => {
                    this.pageComponents.push(component.clone());
                });
            }
        }

        public setName(value: string): RegionBuilder {
            this.name = value;
            return this;
        }

        public setParent(value: api.content.page.layout.LayoutComponent): RegionBuilder {
            this.parent = value;
            return this;
        }

        public addComponent(value: api.content.page.PageComponent): RegionBuilder {
            this.pageComponents.push(value);
            return this;
        }

        public build(): Region {
            return new Region(this);
        }
    }
}