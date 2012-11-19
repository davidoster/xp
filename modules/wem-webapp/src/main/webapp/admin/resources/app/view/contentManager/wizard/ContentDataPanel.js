Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    typeMapping: {
        TextLine: "textfield",
        TextArea: "textarea"
    },

    contentTypeItems: [],
    content: null, // content to be edited

    initComponent: function () {
        var me = this;
        var fieldSet = {
            xtype: 'fieldset',
            title: 'A Separator',
            padding: '10px 15px',
            items: []
        };
        // set values in fields if editing existing content
        var editedContentValues = {};
        if (this.content && this.content.data) {
            Ext.each(this.content.data, function (dataItem) {
                editedContentValues[dataItem.name] = dataItem.value;
            });
        }
        me.items = [fieldSet];
        Ext.each(this.contentTypeItems, function (contentTypeItem) {
            var widgetItemType = me.parseItemType(contentTypeItem);
            if (!widgetItemType) {
                console.log('Unsupported input type', contentTypeItem);
                return;
            }
            var item = Ext.create({
                xclass: "widget." + widgetItemType,
                fieldLabel: contentTypeItem.label,
                name: contentTypeItem.name,
                itemId: contentTypeItem.name,
                cls: 'span-3',
                listeners: {
                    render: function (cmp) {
                        Ext.tip.QuickTipManager.register({
                            target: cmp.el,
                            text: contentTypeItem.helpText
                        });
                    }
                },
                value: editedContentValues[contentTypeItem.name]
            });

            fieldSet.items.push(item);
        });

        me.callParent(arguments);
    },

    parseItemType: function (contentItem) {
        if (!contentItem.inputType) {
            return null;
        }
        return this.typeMapping[contentItem.inputType.name];
    },

    getData: function () {
        return this.getForm().getFieldValues();
    }

});