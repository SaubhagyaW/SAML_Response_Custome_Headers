package org.wso2.carbon.identity.sso.saml.endpoints;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.saml.saml2.core.Assertion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;

/**
 * Created by saubhagya on 1/30/18.
 */
@Path("/headerFormatter")
public class CustomEndpoint {

    private DocumentBuilder docBuilder;
    UnmarshallerFactory unmarshallerFactory;

    @PostConstruct
    public void init() {
        try {
            //Initializing Open SAML Library
            InitializationService.initialize();

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            docBuilder = documentBuilderFactory.newDocumentBuilder();

            unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
        } catch (InitializationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    @POST
    @Path("/format")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response formatHeader(@FormParam("SAMLResponse") String samlResponse) throws Exception {

        //Decoding the extracted encoded SAML Response
        byte[] base64DecodedResponse = Base64.decode(samlResponse);
        ByteArrayInputStream inputStreams = new ByteArrayInputStream(base64DecodedResponse);

        Document document = docBuilder.parse(inputStreams);

        Element element = document.getDocumentElement();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        XMLObject responseXmlObj = unmarshaller.unmarshall(element);
        org.opensaml.saml.saml2.core.Response response = (org.opensaml.saml.saml2.core.Response) responseXmlObj;

        if (response.getAssertions().size() > 0) {
            Assertion assertion = response.getAssertions().get(0);
            String username = assertion.getSubject().getNameID().getValue();

            StringBuffer result = new StringBuffer("{\"username\": \"" + username + "\"}");

            return Response.ok().entity(result.toString()).build();
        } else {
            return Response.status(204).build();
        }
    }
}
