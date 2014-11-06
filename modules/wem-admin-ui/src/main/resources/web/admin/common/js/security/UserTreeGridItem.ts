module api.security {

    export enum UserTreeGridItemType {
        USER_STORE,
        PRINCIPAL,
        GROUPS,
        ROLES
    }

    export class UserTreeGridItem implements api.Equitable {

        private userStore: UserStore;

        private principal: Principal;

        private type: UserTreeGridItemType;

        constructor(builder: UserTreeGridItemBuilder) {
            this.userStore = builder.userStore;
            this.principal = builder.principal;
            this.type = builder.type;
        }

        setUserStore(userStore: UserStore) {
            this.userStore = userStore;
        }

        getUserStore(): UserStore {
            return this.userStore;
        }

        setPrincipal(principal: Principal) {
            this.principal = principal;
        }

        setType(type: UserTreeGridItemType) {
            this.type = type;
        }

        getType() {
            return this.type;
        }

        getPrincipal(): Principal {
            return this.principal;
        }

        getItemDisplayName(): string {
            switch (this.type) {
            case UserTreeGridItemType.USER_STORE:
            {
                return this.userStore.getDisplayName();

            }
            case UserTreeGridItemType.PRINCIPAL:
            {
                return this.principal.getDisplayName();

            }
            case UserTreeGridItemType.ROLES:
            {
                return this.principal.getDisplayName();

            }
            case UserTreeGridItemType.GROUPS:
            {
                return this.principal.getDisplayName();

            }
            }

        }

        getDataId(): string {
            switch (this.type) {
            case UserTreeGridItemType.USER_STORE:
            {
                return this.userStore.getKey().toString();
            }
            case UserTreeGridItemType.PRINCIPAL:
            {
                return this.principal.getKey().toString();
            }
            case UserTreeGridItemType.GROUPS:
            {    //TODO what should be returned?
                return this.principal.getKey().toString();
            }
            case UserTreeGridItemType.ROLES:
            {    //TODO what should be returned?
                return this.principal.getKey().toString();
            }
            }

        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserTreeGridItem)) {
                return false;
            }

            var other = <UserTreeGridItem> o;
            return this.principal === other.getPrincipal() && this.userStore == other.getUserStore();
        }

    }
    export class UserTreeGridItemBuilder {
        userStore: UserStore;
        principal: Principal;
        type: UserTreeGridItemType;

        constructor() {
        }


        setUserStore(userStore: UserStore): UserTreeGridItemBuilder {
            this.userStore = userStore;
            return this;
        }

        setPrincipal(principal: Principal): UserTreeGridItemBuilder {
            this.principal = principal;
            return this;
        }

        setType(type: UserTreeGridItemType): UserTreeGridItemBuilder {
            this.type = type;
            return this;
        }

        build(): UserTreeGridItem {
            return new UserTreeGridItem(this);
        }
    }
}
