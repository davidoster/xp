//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7-b41 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.02.12 at 07:12:54 PM CET 
//


package com.enonic.wem.export.internal.xml;

import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.enonic.wem.export.internal.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.enonic.wem.export.internal.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XmlIndexConfigs }
     * 
     */
    public XmlIndexConfigs createXmlIndexConfigs() {
        return new XmlIndexConfigs();
    }

    /**
     * Create an instance of {@link XmlNode }
     * 
     */
    public XmlNode createXmlNode() {
        return new XmlNode();
    }

    /**
     * Create an instance of {@link XmlReferenceProperty }
     * 
     */
    public XmlReferenceProperty createXmlReferenceProperty() {
        return new XmlReferenceProperty();
    }

    /**
     * Create an instance of {@link XmlDateTimeProperty }
     * 
     */
    public XmlDateTimeProperty createXmlDateTimeProperty() {
        return new XmlDateTimeProperty();
    }

    /**
     * Create an instance of {@link XmlLocalDateTimeProperty }
     * 
     */
    public XmlLocalDateTimeProperty createXmlLocalDateTimeProperty() {
        return new XmlLocalDateTimeProperty();
    }

    /**
     * Create an instance of {@link XmlBinaryReferenceProperty }
     * 
     */
    public XmlBinaryReferenceProperty createXmlBinaryReferenceProperty() {
        return new XmlBinaryReferenceProperty();
    }

    /**
     * Create an instance of {@link XmlPropertySet }
     * 
     */
    public XmlPropertySet createXmlPropertySet() {
        return new XmlPropertySet();
    }

    /**
     * Create an instance of {@link XmlIndexConfig }
     * 
     */
    public XmlIndexConfig createXmlIndexConfig() {
        return new XmlIndexConfig();
    }

    /**
     * Create an instance of {@link XmlPropertyTree }
     * 
     */
    public XmlPropertyTree createXmlPropertyTree() {
        return new XmlPropertyTree();
    }

    /**
     * Create an instance of {@link XmlLongProperty }
     * 
     */
    public XmlLongProperty createXmlLongProperty() {
        return new XmlLongProperty();
    }

    /**
     * Create an instance of {@link XmlXmlProperty }
     * 
     */
    public XmlXmlProperty createXmlXmlProperty() {
        return new XmlXmlProperty();
    }

    /**
     * Create an instance of {@link XmlStringProperty }
     * 
     */
    public XmlStringProperty createXmlStringProperty() {
        return new XmlStringProperty();
    }

    /**
     * Create an instance of {@link XmlBooleanProperty }
     * 
     */
    public XmlBooleanProperty createXmlBooleanProperty() {
        return new XmlBooleanProperty();
    }

    /**
     * Create an instance of {@link XmlDoubleProperty }
     * 
     */
    public XmlDoubleProperty createXmlDoubleProperty() {
        return new XmlDoubleProperty();
    }

    /**
     * Create an instance of {@link XmlPathIndexConfig }
     * 
     */
    public XmlPathIndexConfig createXmlPathIndexConfig() {
        return new XmlPathIndexConfig();
    }

    /**
     * Create an instance of {@link XmlDateProperty }
     * 
     */
    public XmlDateProperty createXmlDateProperty() {
        return new XmlDateProperty();
    }

    /**
     * Create an instance of {@link XmlTimeProperty }
     * 
     */
    public XmlTimeProperty createXmlTimeProperty() {
        return new XmlTimeProperty();
    }

    /**
     * Create an instance of {@link XmlHtmlPartProperty }
     * 
     */
    public XmlHtmlPartProperty createXmlHtmlPartProperty() {
        return new XmlHtmlPartProperty();
    }

    /**
     * Create an instance of {@link XmlGeoPointProperty }
     * 
     */
    public XmlGeoPointProperty createXmlGeoPointProperty() {
        return new XmlGeoPointProperty();
    }

    /**
     * Create an instance of {@link XmlLinkProperty }
     * 
     */
    public XmlLinkProperty createXmlLinkProperty() {
        return new XmlLinkProperty();
    }

    /**
     * Create an instance of {@link XmlIndexConfigs.PathIndexConfigs }
     * 
     */
    public XmlIndexConfigs.PathIndexConfigs createXmlIndexConfigsPathIndexConfigs() {
        return new XmlIndexConfigs.PathIndexConfigs();
    }

    /**
     * Create an instance of {@link XmlNodeElem }}
     * 
     */
    @XmlElementDecl(namespace = "urn:enonic:xp:export:1.0", name = "node")
    public XmlNodeElem createXmlNodeElem(XmlNode value) {
        return new XmlNodeElem(value);
    }

}
